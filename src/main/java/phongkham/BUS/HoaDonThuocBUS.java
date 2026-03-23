package phongkham.BUS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.XuatThuocTheoLoDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.dao.HoaDonThuocDAO;
import phongkham.db.DBConnection;

public class HoaDonThuocBUS {

  private HoaDonThuocDAO hoaDonThuocDAO = new HoaDonThuocDAO();
  private ThuocBUS thuocBUS = new ThuocBUS();

  // Thêm HoaDonThuoc
  public boolean addHoaDonThuoc(HoaDonThuocDTO hoaDon) {
    if (!validateHoaDonInput(hoaDon, false)) {
      return false;
    }

    return hoaDonThuocDAO.insert(hoaDon);
  }

  // Cập nhật HoaDonThuoc
  public boolean updateHoaDonThuoc(HoaDonThuocDTO hoaDon) {
    if (!validateHoaDonInput(hoaDon, true)) {
      return false;
    }
    return hoaDonThuocDAO.update(hoaDon);
  }

  // Xóa HoaDonThuoc
  public boolean deleteHoaDonThuoc(String maHoaDon) {
    HoaDonThuocDTO hoaDon = getHoaDonOrNull(maHoaDon);
    if (hoaDon == null) {
      System.err.println("Hóa đơn không tồn tại");
      return false;
    }

    // Không cho xóa hóa đơn đã thanh toán
    if (isTrangThaiDaThanhToan(hoaDon.getTrangThaiThanhToan())) {
      System.err.println("Không thể xóa hóa đơn đã thanh toán");
      return false;
    }

    hoaDon.setActive(false);
    return hoaDonThuocDAO.update(hoaDon);
  }

  // Lấy chi tiết HoaDonThuoc
  public HoaDonThuocDTO getHoaDonThuocDetail(String maHoaDon) {
    return hoaDonThuocDAO.getById(maHoaDon);
  }

  // Lấy tất cả HoaDonThuoc
  public List<HoaDonThuocDTO> getAllHoaDonThuoc() {
    return hoaDonThuocDAO.getAll();
  }

  public boolean createInvoiceFromPrescription(
    HoaDonThuocDTO hoaDon,
    List<CTHDThuocDTO> chiTietThuoc
  ) {
    if (!validateHoaDonInput(hoaDon, false)) {
      return false;
    }
    if (isBlank(hoaDon.getMaDonThuoc())) {
      System.err.println("Mã đơn thuốc không được rỗng");
      return false;
    }
    if (chiTietThuoc == null || chiTietThuoc.isEmpty()) {
      System.err.println("Đơn thuốc chưa có chi tiết thuốc");
      return false;
    }

    double tongTien = 0;
    for (CTHDThuocDTO ct : chiTietThuoc) {
      if (ct == null || isBlank(ct.getMaThuoc()) || ct.getSoLuong() <= 0) {
        System.err.println("Chi tiết thuốc không hợp lệ");
        return false;
      }
      if (ct.getDonGia() <= 0) {
        System.err.println("Đơn giá thuốc phải lớn hơn 0");
        return false;
      }
      tongTien += ct.getSoLuong() * ct.getDonGia();
    }

    hoaDon.setNgayLap(LocalDateTime.now());
    hoaDon.setTongTien(tongTien);
    hoaDon.setTrangThaiThanhToan(StatusNormalizer.CHUA_THANH_TOAN);
    hoaDon.setTrangThaiLayThuoc(StatusNormalizer.CHO_LAY);
    hoaDon.setNgayThanhToan(null);
    hoaDon.setActive(true);

    try (Connection conn = DBConnection.getConnection()) {
      conn.setAutoCommit(false);
      try {
        if (isBlank(hoaDon.getMaHoaDon())) {
          hoaDon.setMaHoaDon(
            generateNextId(conn, "HoaDonThuoc", "MaHoaDon", "HDT", 3)
          );
        }

        String sqlInsertHoaDon =
          "INSERT INTO HoaDonThuoc (MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu, TrangThaiThanhToan, NgayThanhToan, TrangThaiLayThuoc, TenBenhNhan, SdtBenhNhan, Active) " +
          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsertHoaDon)) {
          ps.setString(1, hoaDon.getMaHoaDon());
          ps.setString(2, hoaDon.getMaDonThuoc());
          ps.setObject(3, hoaDon.getNgayLap());
          ps.setDouble(4, hoaDon.getTongTien());
          ps.setString(5, hoaDon.getGhiChu());
          ps.setString(6, StatusNormalizer.CHUA_THANH_TOAN);
          ps.setObject(7, null);
          ps.setString(8, StatusNormalizer.CHO_LAY);
          ps.setString(9, hoaDon.getTenBenhNhan());
          ps.setString(10, hoaDon.getSdtBenhNhan());
          ps.setBoolean(11, true);
          if (ps.executeUpdate() <= 0) {
            conn.rollback();
            return false;
          }
        }

        String sqlInsertCt =
          "INSERT INTO CTHDThuoc (MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int idCounter = getCurrentNumericPart(
          conn,
          "CTHDThuoc",
          "MaCTHDThuoc",
          "CTHD"
        );
        for (CTHDThuocDTO ct : chiTietThuoc) {
          idCounter++;
          String maCt = String.format("CTHD%03d", idCounter);
          try (PreparedStatement ps = conn.prepareStatement(sqlInsertCt)) {
            ps.setString(1, maCt);
            ps.setString(2, hoaDon.getMaHoaDon());
            ps.setString(3, ct.getMaThuoc());
            ps.setInt(4, ct.getSoLuong());
            ps.setDouble(5, ct.getDonGia());
            ps.setDouble(6, ct.getSoLuong() * ct.getDonGia());
            ps.setString(7, ct.getGhiChu());
            ps.setBoolean(8, true);
            if (ps.executeUpdate() <= 0) {
              conn.rollback();
              return false;
            }
          }
        }

        conn.commit();
        return true;
      } catch (Exception ex) {
        conn.rollback();
        System.err.println("Lỗi tạo hóa đơn từ đơn thuốc: " + ex.getMessage());
        return false;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (Exception ex) {
      System.err.println(
        "Không thể mở transaction tạo hóa đơn từ đơn thuốc: " + ex.getMessage()
      );
      return false;
    }
  }

  // Lấy hóa đơn theo khoảng thời gian
  public List<HoaDonThuocDTO> getHoaDonByDateRange(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return hoaDonThuocDAO.getByDate(startDate, endDate);
  }

  // Lấy hóa đơn chưa thanh toán
  public List<HoaDonThuocDTO> getUnpaidInvoices() {
    return filterByPaymentStatus("CHUA_THANH_TOAN");
  }

  // Lấy hóa đơn đã thanh toán
  public List<HoaDonThuocDTO> getPaidInvoices() {
    return filterByPaymentStatus("DA_THANH_TOAN");
  }

  // Thanh toán hóa đơn
  public boolean payInvoice(String maHoaDon) {
    HoaDonThuocDTO hoaDon = getHoaDonOrNull(maHoaDon);
    if (hoaDon == null) {
      System.err.println("Hóa đơn không tồn tại");
      return false;
    }

    if (hoaDon.getTongTien() <= 0) {
      System.err.println("Tổng tiền phải lớn hơn 0");
      return false;
    }

    return hoaDonThuocDAO.updatePaymentStatus(
      maHoaDon,
      StatusNormalizer.DA_THANH_TOAN,
      LocalDateTime.now()
    );
  }

  // Hoàn hóa đơn
  // Cập nhật trạng thái thành "Hoàn hóa đơn" và xóa thời gian thanh toán
  public boolean refundInvoice(String maHoaDon) {
    HoaDonThuocDTO hoaDon = getHoaDonOrNull(maHoaDon);
    if (hoaDon == null) {
      System.err.println("Hóa đơn không tồn tại");
      return false;
    }

    if (!isTrangThaiDaThanhToan(hoaDon.getTrangThaiThanhToan())) {
      System.err.println("Chỉ có thể hoàn hóa đơn đã thanh toán");
      return false;
    }

    return hoaDonThuocDAO.updatePaymentStatus(
      maHoaDon,
      StatusNormalizer.HOAN_HOA_DON,
      null
    );
  }

  public boolean cancelInvoice(String maHoaDon) {
    HoaDonThuocDTO hoaDon = getHoaDonOrNull(maHoaDon);
    if (hoaDon == null) {
      System.err.println("Hóa đơn không tồn tại");
      return false;
    }

    String pickupStatus = StatusNormalizer.normalizePickupStatus(
      hoaDon.getTrangThaiLayThuoc()
    );
    if (StatusNormalizer.DA_HOAN_THANH.equals(pickupStatus)) {
      System.err.println("Hóa đơn đã giao thuốc, không thể hủy");
      return false;
    }
    if (StatusNormalizer.DA_HUY.equals(pickupStatus)) {
      System.err.println("Hóa đơn đã hủy trước đó");
      return false;
    }

    return hoaDonThuocDAO.updatePaymentAndPickupStatus(
      maHoaDon,
      StatusNormalizer.HOAN_HOA_DON,
      null,
      StatusNormalizer.DA_HUY
    );
  }

  // Tính tổng doanh thu trong khoảng thời gian
  public double calculateTotalRevenue(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return hoaDonThuocDAO.getTotalRevenue(startDate, endDate);
  }

  // Tính tổng tiền của hóa đơn (Gọi CTHDThuocBUS để tính tổng tiền)
  public double calculateInvoiceTotal(String maHoaDon) {
    HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
    return hoaDon != null ? hoaDon.getTongTien() : 0;
  }

  // Kiểm tra xem hóa đơn có thể chỉnh sửa không
  public boolean canEditInvoice(String maHoaDon) {
    HoaDonThuocDTO hoaDon = getHoaDonOrNull(maHoaDon);
    if (hoaDon == null) return false;

    // Không cho chỉnh sửa hóa đơn đã thanh toán
    return !isTrangThaiDaThanhToan(hoaDon.getTrangThaiThanhToan());
  }

  // Hoàn thành lấy thuốc - Trừ kho và cập nhật trạng thái
  public boolean completePickup(String maHoaDon) {
    return completePickupInternal(maHoaDon, false, false);
  }

  public boolean completePickupWithSimulation(
    String maHoaDon,
    boolean testFailAfterFirstLine
  ) {
    return completePickupInternal(maHoaDon, testFailAfterFirstLine, false);
  }

  public boolean completePickupForTesting(
    String maHoaDon,
    boolean testFailAfterFirstLine
  ) {
    return completePickupInternal(maHoaDon, testFailAfterFirstLine, true);
  }

  private boolean completePickupInternal(
    String maHoaDon,
    boolean testFailAfterFirstLine,
    boolean skipPermissionCheck
  ) {
    if (
      !skipPermissionCheck &&
      !Session.hasPermission("HOADONTHUOC_MANAGE") &&
      !Session.hasPermission("HOADONTHUOC_CREATE")
    ) {
      System.err.println("Không có quyền hoàn tất xuất thuốc");
      return false;
    }

    HoaDonThuocDTO hoaDon = getHoaDonOrNull(maHoaDon);
    if (hoaDon == null) {
      System.err.println("Hóa đơn không tồn tại");
      return false;
    }

    // Kiểm tra trạng thái hiện tại
    if (!isTrangThaiLayThuocChoLay(hoaDon.getTrangThaiLayThuoc())) {
      System.err.println("Hóa đơn không ở trạng thái ĐANG CHỜ LẤY");
      return false;
    }

    // Kiểm tra đã thanh toán chưa
    if (!isTrangThaiDaThanhToan(hoaDon.getTrangThaiThanhToan())) {
      System.err.println("Hóa đơn chưa thanh toán, không thể lấy thuốc");
      return false;
    }

    try (Connection conn = DBConnection.getConnection()) {
      conn.setAutoCommit(false);
      try {
        List<CTHDThuocDTO> chiTiet = loadChiTietHoaDon(conn, maHoaDon);
        if (chiTiet == null || chiTiet.isEmpty()) {
          conn.rollback();
          System.err.println("Hóa đơn chưa có chi tiết thuốc");
          return false;
        }

        // Đồng bộ tồn kho theo hạn sử dụng trước khi trừ kho thực tế.
        Set<String> medicines = new HashSet<>();
        for (CTHDThuocDTO ct : chiTiet) {
          if (ct != null && ct.getMaThuoc() != null) {
            medicines.add(ct.getMaThuoc());
          }
        }
        for (String maThuoc : medicines) {
          if (!thuocBUS.dongBoTonKhoTheoHanSuDung(maThuoc)) {
            conn.rollback();
            System.err.println(
              "Không thể đồng bộ tồn kho theo hạn sử dụng cho thuốc: " + maThuoc
            );
            return false;
          }
        }

        int soDongDaXuLy = 0;
        for (CTHDThuocDTO ct : chiTiet) {
          if (
            !xuatKhoTheoLoFefoTrongTransaction(
              conn,
              maHoaDon,
              ct.getMaCTHDThuoc(),
              ct.getMaThuoc(),
              ct.getSoLuong()
            )
          ) {
            conn.rollback();
            System.err.println(
              "Lỗi trừ số lượng tồn thuốc mã: " + ct.getMaThuoc()
            );
            return false;
          }

          soDongDaXuLy++;
          if (testFailAfterFirstLine && soDongDaXuLy == 1) {
            throw new SQLException("TEST_SIMULATION_PICKUP_ROLLBACK");
          }
        }

        if (
          !updateTrangThaiLayThuoc(
            conn,
            maHoaDon,
            StatusNormalizer.DA_HOAN_THANH
          )
        ) {
          conn.rollback();
          return false;
        }

        conn.commit();
        System.out.println("✓ Hoàn thành lấy thuốc và trừ kho thành công!");
        return true;
      } catch (Exception e) {
        conn.rollback();
        System.err.println("Lỗi khi hoàn thành lấy thuốc: " + e.getMessage());
        e.printStackTrace();
        return false;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (Exception e) {
      System.err.println(
        "Không thể mở transaction giao thuốc: " + e.getMessage()
      );
      return false;
    }
  }

  private List<CTHDThuocDTO> loadChiTietHoaDon(Connection conn, String maHoaDon)
    throws SQLException {
    String sql =
      "SELECT MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia FROM CTHDThuoc WHERE MaHoaDon = ?";
    java.util.ArrayList<CTHDThuocDTO> result = new java.util.ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, maHoaDon);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          CTHDThuocDTO ct = new CTHDThuocDTO();
          ct.setMaCTHDThuoc(rs.getString("MaCTHDThuoc"));
          ct.setMaHoaDon(rs.getString("MaHoaDon"));
          ct.setMaThuoc(rs.getString("MaThuoc"));
          ct.setSoLuong(rs.getInt("SoLuong"));
          ct.setDonGia(rs.getDouble("DonGia"));
          result.add(ct);
        }
      }
    }
    return result;
  }

  private boolean truKhoTrongTransaction(
    Connection conn,
    String maThuoc,
    int soLuong
  ) throws SQLException {
    String sql =
      "UPDATE Thuoc SET SoLuongTon = SoLuongTon - ? WHERE MaThuoc = ? AND Active = 1 AND SoLuongTon >= ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, soLuong);
      ps.setString(2, maThuoc);
      ps.setInt(3, soLuong);
      return ps.executeUpdate() > 0;
    }
  }

  private boolean xuatKhoTheoLoFefoTrongTransaction(
    Connection conn,
    String maHoaDon,
    String maCTHDThuoc,
    String maThuoc,
    int soLuongCanXuat
  ) throws SQLException {
    ArrayList<LotFefoRow> lots = loadLotsForFefo(conn, maThuoc);
    int tongConTheXuat = 0;
    for (LotFefoRow lot : lots) {
      tongConTheXuat += lot.soLuongConLai;
    }
    if (tongConTheXuat < soLuongCanXuat) {
      return false;
    }

    int soLuongConLaiCanXuat = soLuongCanXuat;
    for (LotFefoRow lot : lots) {
      if (soLuongConLaiCanXuat <= 0) {
        break;
      }
      if (lot.soLuongConLai <= 0) {
        continue;
      }

      int soLuongXuatTuLo = Math.min(soLuongConLaiCanXuat, lot.soLuongConLai);
      if (!giamSoLuongConLaiLo(conn, lot.maCTPN, soLuongXuatTuLo)) {
        return false;
      }
      if (
        !insertXuatTheoLoHistory(
          conn,
          maHoaDon,
          maCTHDThuoc,
          lot.maCTPN,
          maThuoc,
          lot.soLo,
          lot.hanSuDung,
          soLuongXuatTuLo
        )
      ) {
        return false;
      }

      soLuongConLaiCanXuat -= soLuongXuatTuLo;
    }

    if (soLuongConLaiCanXuat > 0) {
      return false;
    }

    return truKhoTrongTransaction(conn, maThuoc, soLuongCanXuat);
  }

  private ArrayList<LotFefoRow> loadLotsForFefo(Connection conn, String maThuoc)
    throws SQLException {
    String sql =
      "SELECT ctpn.MaCTPN, ctpn.SoLo, ctpn.HanSuDung, ctpn.SoLuongConLai " +
      "FROM ChiTietPhieuNhap ctpn " +
      "JOIN PhieuNhap pn ON pn.MaPhieuNhap = ctpn.MaPhieuNhap " +
      "WHERE ctpn.MaThuoc = ? " +
      "  AND ctpn.SoLuongConLai > 0 " +
      "  AND (ctpn.HanSuDung IS NULL OR DATE(ctpn.HanSuDung) > CURDATE()) " +
      "  AND UPPER(TRIM(COALESCE(pn.TrangThai, ''))) IN ('DA_NHAP', 'DA_NHAP_KHO') " +
      "ORDER BY (ctpn.HanSuDung IS NULL) ASC, ctpn.HanSuDung ASC, pn.NgayNhap ASC " +
      "FOR UPDATE";

    ArrayList<LotFefoRow> lots = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, maThuoc);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          LotFefoRow row = new LotFefoRow();
          row.maCTPN = rs.getString("MaCTPN");
          row.soLo = rs.getString("SoLo");
          row.hanSuDung = rs.getObject("HanSuDung", LocalDate.class);
          row.soLuongConLai = rs.getInt("SoLuongConLai");
          lots.add(row);
        }
      }
    }
    return lots;
  }

  private boolean giamSoLuongConLaiLo(
    Connection conn,
    String maCTPN,
    int soLuongXuat
  ) throws SQLException {
    String sql =
      "UPDATE ChiTietPhieuNhap SET SoLuongConLai = SoLuongConLai - ? WHERE MaCTPN = ? AND SoLuongConLai >= ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, soLuongXuat);
      ps.setString(2, maCTPN);
      ps.setInt(3, soLuongXuat);
      return ps.executeUpdate() > 0;
    }
  }

  private boolean insertXuatTheoLoHistory(
    Connection conn,
    String maHoaDon,
    String maCTHDThuoc,
    String maCTPN,
    String maThuoc,
    String soLo,
    LocalDate hanSuDung,
    int soLuongXuat
  ) throws SQLException {
    String sql =
      "INSERT INTO XuatThuocTheoLo (MaHoaDon, MaCTHDThuoc, MaCTPN, MaThuoc, SoLo, HanSuDung, SoLuongXuat, NgayXuat) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, maHoaDon);
      ps.setString(2, maCTHDThuoc);
      ps.setString(3, maCTPN);
      ps.setString(4, maThuoc);
      ps.setString(5, soLo);
      ps.setObject(6, hanSuDung);
      ps.setInt(7, soLuongXuat);
      return ps.executeUpdate() > 0;
    }
  }

  private boolean updateTrangThaiLayThuoc(
    Connection conn,
    String maHoaDon,
    String trangThaiLayThuoc
  ) throws SQLException {
    String sql =
      "UPDATE HoaDonThuoc SET TrangThaiLayThuoc = ? WHERE MaHoaDon = ? AND Active = 1";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(
        1,
        StatusNormalizer.normalizePickupStatus(trangThaiLayThuoc)
      );
      ps.setString(2, maHoaDon);
      return ps.executeUpdate() > 0;
    }
  }

  private boolean isTrangThaiDaThanhToan(String trangThai) {
    return "DA_THANH_TOAN".equals(
      StatusNormalizer.normalizePaymentStatus(trangThai)
    );
  }

  private boolean isTrangThaiLayThuocChoLay(String trangThai) {
    return "CHO_LAY".equals(StatusNormalizer.normalizePickupStatus(trangThai));
  }

  private boolean isSoDienThoaiHopLe(String soDienThoai) {
    if (soDienThoai == null) {
      return false;
    }
    String soDaChuanHoa = soDienThoai.trim();
    return soDaChuanHoa.matches("^(0|\\+84)[0-9]{9,10}$");
  }

  // Lấy hóa đơn theo trạng thái lấy thuốc
  public java.util.List<HoaDonThuocDTO> getByPickupStatus(
    String trangThaiLayThuoc
  ) {
    java.util.List<HoaDonThuocDTO> result = new java.util.ArrayList<>();
    String statusNormalized = StatusNormalizer.normalizePickupStatus(
      trangThaiLayThuoc
    );
    for (HoaDonThuocDTO hd : hoaDonThuocDAO.getAll()) {
      if (
        statusNormalized.equals(
          StatusNormalizer.normalizePickupStatus(hd.getTrangThaiLayThuoc())
        )
      ) {
        result.add(hd);
      }
    }
    return result;
  }

  public List<XuatThuocTheoLoDTO> getXuatTheoLoByMaHoaDon(String maHoaDon) {
    if (isBlank(maHoaDon)) {
      return new java.util.ArrayList<>();
    }
    return hoaDonThuocDAO.getXuatTheoLoByMaHoaDon(maHoaDon.trim());
  }

  private boolean validateHoaDonInput(
    HoaDonThuocDTO hoaDon,
    boolean requireMaHoaDon
  ) {
    if (hoaDon == null) {
      System.err.println("Hóa đơn không được null");
      return false;
    }
    if (requireMaHoaDon && isBlank(hoaDon.getMaHoaDon())) {
      System.err.println("Mã hóa đơn không được rỗng");
      return false;
    }
    if (isBlank(hoaDon.getTenBenhNhan())) {
      System.err.println("Tên bệnh nhân không được để trống");
      return false;
    }
    if (!isSoDienThoaiHopLe(hoaDon.getSdtBenhNhan())) {
      System.err.println("Số điện thoại bệnh nhân không hợp lệ");
      return false;
    }
    if (hoaDon.getTongTien() < 0) {
      System.err.println("Tổng tiền không được âm");
      return false;
    }
    return true;
  }

  private java.util.List<HoaDonThuocDTO> filterByPaymentStatus(String status) {
    java.util.List<HoaDonThuocDTO> result = new java.util.ArrayList<>();
    for (HoaDonThuocDTO hd : hoaDonThuocDAO.getAll()) {
      if (
        status.equals(
          StatusNormalizer.normalizePaymentStatus(hd.getTrangThaiThanhToan())
        )
      ) {
        result.add(hd);
      }
    }
    return result;
  }

  private HoaDonThuocDTO getHoaDonOrNull(String maHoaDon) {
    if (isBlank(maHoaDon)) {
      return null;
    }
    return hoaDonThuocDAO.getById(maHoaDon);
  }

  private String generateNextId(
    Connection conn,
    String tableName,
    String columnName,
    String prefix,
    int padLength
  ) throws SQLException {
    int current = getCurrentNumericPart(conn, tableName, columnName, prefix);
    return prefix + String.format("%0" + padLength + "d", current + 1);
  }

  private int getCurrentNumericPart(
    Connection conn,
    String tableName,
    String columnName,
    String prefix
  ) throws SQLException {
    String sql =
      "SELECT " +
      columnName +
      " FROM " +
      tableName +
      " ORDER BY " +
      columnName +
      " DESC LIMIT 1";
    try (
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery()
    ) {
      if (rs.next()) {
        String last = rs.getString(1);
        if (last != null && last.startsWith(prefix)) {
          String num = last.substring(prefix.length());
          return Integer.parseInt(num);
        }
      }
    } catch (NumberFormatException ignored) {}
    return 0;
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private static class LotFefoRow {

    private String maCTPN;
    private String soLo;
    private LocalDate hanSuDung;
    private int soLuongConLai;
  }
}
