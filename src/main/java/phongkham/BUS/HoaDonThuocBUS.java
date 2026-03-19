package phongkham.BUS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.dao.HoaDonThuocDAO;
import phongkham.db.DBConnection;

public class HoaDonThuocBUS {

  private HoaDonThuocDAO hoaDonThuocDAO = new HoaDonThuocDAO();

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

        int soDongDaXuLy = 0;
        for (CTHDThuocDTO ct : chiTiet) {
          if (!truKhoTrongTransaction(conn, ct.getMaThuoc(), ct.getSoLuong())) {
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

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
