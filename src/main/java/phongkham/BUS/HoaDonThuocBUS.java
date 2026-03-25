package phongkham.BUS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

  // ================= API tiếng Việt không dấu =================
  // Các hàm này giúp đọc code dễ hơn cho team nghiệp vụ.
  public boolean themHoaDonThuoc(HoaDonThuocDTO hoaDon) {
    return addHoaDonThuoc(hoaDon);
  }

  public boolean taoHoaDonTuDonThuoc(
    HoaDonThuocDTO hoaDon,
    List<CTHDThuocDTO> chiTietThuoc
  ) {
    return createInvoiceFromPrescription(hoaDon, chiTietThuoc);
  }

  public boolean coTheSuaHoaDon(String maHoaDon) {
    return canEditInvoice(maHoaDon);
  }

  public String layThongBaoKiemTraTonKhoHoaDon(String maHoaDon) {
    return getInvoiceStockValidationMessage(maHoaDon);
  }

  public boolean thayTheChiTietHoaDonTruocThanhToan(
    String maHoaDon,
    List<CTHDThuocDTO> chiTietCapNhat,
    String ghiChuHoaDon
  ) {
    return replaceInvoiceDetailsBeforePayment(
      maHoaDon,
      chiTietCapNhat,
      ghiChuHoaDon
    );
  }

  public boolean xacNhanThanhToanHoaDon(String maHoaDon) {
    return payInvoice(maHoaDon);
  }

  public boolean xacNhanGiaoThuoc(String maHoaDon) {
    return completePickup(maHoaDon);
  }

  public boolean huyHoaDonThuoc(String maHoaDon) {
    return cancelInvoice(maHoaDon);
  }

  public List<XuatThuocTheoLoDTO> layLichSuXuatTheoLoTheoHoaDon(
    String maHoaDon
  ) {
    return getXuatTheoLoByMaHoaDon(maHoaDon);
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

    if (!canEditInvoice(maHoaDon)) {
      System.err.println("Hóa đơn không hợp lệ để xác nhận thanh toán");
      return false;
    }

    if (!validateInvoiceStockBeforePayment(maHoaDon)) {
      System.err.println(
        "Tồn kho không đủ hoặc chi tiết hóa đơn không hợp lệ, cần chỉnh sửa trước khi thanh toán"
      );
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

    String paymentStatus = StatusNormalizer.normalizePaymentStatus(
      hoaDon.getTrangThaiThanhToan()
    );
    String paymentStatusAfterCancel = StatusNormalizer.DA_THANH_TOAN.equals(
      paymentStatus
    )
      ? StatusNormalizer.HOAN_HOA_DON
      : StatusNormalizer.CHUA_THANH_TOAN;
    LocalDateTime paidAtAfterCancel = StatusNormalizer.DA_THANH_TOAN.equals(
      paymentStatus
    )
      ? LocalDateTime.now()
      : null;

    return hoaDonThuocDAO.updatePaymentAndPickupStatus(
      maHoaDon,
      paymentStatusAfterCancel,
      paidAtAfterCancel,
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

    String paymentStatus = StatusNormalizer.normalizePaymentStatus(
      hoaDon.getTrangThaiThanhToan()
    );
    String pickupStatus = StatusNormalizer.normalizePickupStatus(
      hoaDon.getTrangThaiLayThuoc()
    );

    return (
      StatusNormalizer.CHUA_THANH_TOAN.equals(paymentStatus) &&
      StatusNormalizer.CHO_LAY.equals(pickupStatus)
    );
  }

  public boolean updateInvoiceDetailsBeforePayment(
    String maHoaDon,
    Map<String, Integer> quantityByDetailId,
    String ghiChuHoaDon
  ) {
    if (isBlank(maHoaDon)) {
      return false;
    }
    if (!canEditInvoice(maHoaDon)) {
      System.err.println("Chỉ được sửa chi tiết khi hóa đơn chưa thanh toán");
      return false;
    }
    if (quantityByDetailId == null || quantityByDetailId.isEmpty()) {
      System.err.println("Danh sách chi tiết cập nhật không hợp lệ");
      return false;
    }

    try (Connection conn = DBConnection.getConnection()) {
      conn.setAutoCommit(false);
      try {
        List<CTHDThuocDTO> currentDetails = loadChiTietHoaDon(conn, maHoaDon);
        if (currentDetails.isEmpty()) {
          conn.rollback();
          return false;
        }

        int soDongConLai = 0;
        for (CTHDThuocDTO detail : currentDetails) {
          Integer qty = quantityByDetailId.get(detail.getMaCTHDThuoc());
          if (qty == null || qty <= 0) {
            if (!deactivateDetail(conn, detail.getMaCTHDThuoc())) {
              conn.rollback();
              return false;
            }
            continue;
          }

          if (qty > getThuocTonKho(conn, detail.getMaThuoc())) {
            conn.rollback();
            return false;
          }

          if (
            !updateDetailQuantity(
              conn,
              detail.getMaCTHDThuoc(),
              qty,
              detail.getDonGia()
            )
          ) {
            conn.rollback();
            return false;
          }
          soDongConLai++;
        }

        if (soDongConLai <= 0) {
          conn.rollback();
          return false;
        }

        double tongTienMoi = calculateInvoiceTotalInTransaction(conn, maHoaDon);
        if (
          !updateInvoiceSummary(
            conn,
            maHoaDon,
            tongTienMoi,
            StatusNormalizer.CHUA_THANH_TOAN,
            null,
            StatusNormalizer.CHO_LAY,
            ghiChuHoaDon
          )
        ) {
          conn.rollback();
          return false;
        }

        conn.commit();
        return true;
      } catch (Exception ex) {
        conn.rollback();
        System.err.println("Lỗi cập nhật chi tiết hóa đơn: " + ex.getMessage());
        return false;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (Exception ex) {
      System.err.println(
        "Không thể mở transaction cập nhật chi tiết hóa đơn: " + ex.getMessage()
      );
      return false;
    }
  }

  public boolean replaceInvoiceDetailsBeforePayment(
    String maHoaDon,
    List<CTHDThuocDTO> updatedDetails,
    String ghiChuHoaDon
  ) {
    if (isBlank(maHoaDon)) {
      return false;
    }
    if (!canEditInvoice(maHoaDon)) {
      System.err.println("Chỉ được sửa chi tiết khi hóa đơn chưa thanh toán");
      return false;
    }
    if (updatedDetails == null || updatedDetails.isEmpty()) {
      System.err.println("Danh sách chi tiết cập nhật không hợp lệ");
      return false;
    }

    try (Connection conn = DBConnection.getConnection()) {
      conn.setAutoCommit(false);
      try {
        List<CTHDThuocDTO> currentDetails = loadChiTietHoaDon(conn, maHoaDon);
        Map<String, CTHDThuocDTO> currentById = new HashMap<>();
        for (CTHDThuocDTO detail : currentDetails) {
          currentById.put(detail.getMaCTHDThuoc(), detail);
        }

        int nextCtNumber = getCurrentNumericPart(
          conn,
          "CTHDThuoc",
          "MaCTHDThuoc",
          "CTHD"
        );
        Set<String> keptDetailIds = new HashSet<>();

        for (CTHDThuocDTO detail : updatedDetails) {
          if (detail == null || isBlank(detail.getMaThuoc())) {
            conn.rollback();
            return false;
          }
          if (detail.getSoLuong() <= 0 || detail.getDonGia() <= 0) {
            conn.rollback();
            return false;
          }

          int tonKho = getThuocTonKho(conn, detail.getMaThuoc());
          if (tonKho < detail.getSoLuong()) {
            conn.rollback();
            return false;
          }

          if (!isBlank(detail.getMaCTHDThuoc())) {
            if (!currentById.containsKey(detail.getMaCTHDThuoc())) {
              conn.rollback();
              return false;
            }
            if (!updateDetailFull(conn, detail)) {
              conn.rollback();
              return false;
            }
            keptDetailIds.add(detail.getMaCTHDThuoc());
          } else {
            nextCtNumber++;
            String maCt = String.format("CTHD%03d", nextCtNumber);
            detail.setMaCTHDThuoc(maCt);
            detail.setMaHoaDon(maHoaDon);
            if (!insertDetail(conn, detail)) {
              conn.rollback();
              return false;
            }
            keptDetailIds.add(maCt);
          }
        }

        for (CTHDThuocDTO oldDetail : currentDetails) {
          if (!keptDetailIds.contains(oldDetail.getMaCTHDThuoc())) {
            if (!deactivateDetail(conn, oldDetail.getMaCTHDThuoc())) {
              conn.rollback();
              return false;
            }
          }
        }

        double tongTienMoi = calculateInvoiceTotalInTransaction(conn, maHoaDon);
        if (tongTienMoi <= 0) {
          conn.rollback();
          return false;
        }

        if (
          !updateInvoiceSummary(
            conn,
            maHoaDon,
            tongTienMoi,
            StatusNormalizer.CHUA_THANH_TOAN,
            null,
            StatusNormalizer.CHO_LAY,
            ghiChuHoaDon
          )
        ) {
          conn.rollback();
          return false;
        }

        conn.commit();
        return true;
      } catch (Exception ex) {
        conn.rollback();
        System.err.println(
          "Lỗi thay thế chi tiết hóa đơn trước thanh toán: " + ex.getMessage()
        );
        return false;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (Exception ex) {
      System.err.println(
        "Không thể mở transaction thay thế chi tiết hóa đơn: " + ex.getMessage()
      );
      return false;
    }
  }

  public boolean validateInvoiceStockBeforePayment(String maHoaDon) {
    if (isBlank(maHoaDon)) {
      return false;
    }
    try (Connection conn = DBConnection.getConnection()) {
      List<CTHDThuocDTO> details = loadChiTietHoaDon(conn, maHoaDon);
      if (details == null || details.isEmpty()) {
        return false;
      }
      for (CTHDThuocDTO detail : details) {
        if (detail.getSoLuong() <= 0 || detail.getDonGia() <= 0) {
          return false;
        }
        int tonKho = getThuocTonKho(conn, detail.getMaThuoc());
        if (tonKho < detail.getSoLuong()) {
          return false;
        }
      }
      return true;
    } catch (Exception ex) {
      System.err.println("Lỗi kiểm tra tồn kho hóa đơn: " + ex.getMessage());
      return false;
    }
  }

  public String getInvoiceStockValidationMessage(String maHoaDon) {
    if (isBlank(maHoaDon)) {
      return "Mã hóa đơn không hợp lệ.";
    }
    try (Connection conn = DBConnection.getConnection()) {
      List<CTHDThuocDTO> details = loadChiTietHoaDon(conn, maHoaDon);
      if (details == null || details.isEmpty()) {
        return "Hóa đơn chưa có chi tiết thuốc hợp lệ.";
      }

      Map<String, String> tenThuocByMa = loadTenThuocMap(conn);
      for (CTHDThuocDTO detail : details) {
        if (detail.getSoLuong() <= 0 || detail.getDonGia() <= 0) {
          return "Chi tiết hóa đơn có số lượng hoặc đơn giá không hợp lệ.";
        }
        int tonKho = getThuocTonKho(conn, detail.getMaThuoc());
        if (tonKho < detail.getSoLuong()) {
          String tenThuoc = tenThuocByMa.getOrDefault(
            detail.getMaThuoc(),
            detail.getMaThuoc()
          );
          return (
            "Không đủ tồn kho cho thuốc " +
            tenThuoc +
            ": cần " +
            detail.getSoLuong() +
            ", còn " +
            tonKho +
            "."
          );
        }
      }
      return "";
    } catch (Exception ex) {
      return "Không kiểm tra được tồn kho hóa đơn. Vui lòng thử lại.";
    }
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
      !Session.hasPermission("HOADONTHUOC_XAC_NHAN_GIAO_THUOC")
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

        // Dùng trực tiếp bảng LoThuoc để xuất FEFO, tránh lệ thuộc bảng chi tiết nhập cũ.

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
      "SELECT MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia FROM CTHDThuoc WHERE MaHoaDon = ? AND Active = 1";
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
      if (!giamSoLuongConLaiLo(conn, lot.maLo, soLuongXuatTuLo)) {
        return false;
      }
      if (
        !insertXuatTheoLoHistory(
          conn,
          maHoaDon,
          maCTHDThuoc,
          lot.maLo,
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
      "SELECT lt.MaLo, lt.MaCTPN, lt.SoLo, lt.HanSuDung, lt.SoLuongConLai " +
      "FROM LoThuoc lt " +
      "WHERE lt.MaThuoc = ? " +
      "  AND lt.Active = 1 " +
      "  AND lt.SoLuongConLai > 0 " +
      "  AND (lt.HanSuDung IS NULL OR DATE(lt.HanSuDung) > CURDATE()) " +
      "ORDER BY (lt.HanSuDung IS NULL) ASC, lt.HanSuDung ASC, lt.NgayNhap ASC " +
      "FOR UPDATE";

    ArrayList<LotFefoRow> lots = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, maThuoc);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          LotFefoRow row = new LotFefoRow();
          row.maLo = rs.getLong("MaLo");
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
    long maLo,
    int soLuongXuat
  ) throws SQLException {
    String sql =
      "UPDATE LoThuoc " +
      "SET SoLuongConLai = SoLuongConLai - ?, " +
      "    TrangThai = CASE WHEN (SoLuongConLai - ?) <= 0 THEN 'DEPLETED' ELSE TrangThai END " +
      "WHERE MaLo = ? AND Active = 1 AND SoLuongConLai >= ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, soLuongXuat);
      ps.setInt(2, soLuongXuat);
      ps.setLong(3, maLo);
      ps.setInt(4, soLuongXuat);
      return ps.executeUpdate() > 0;
    }
  }

  private boolean insertXuatTheoLoHistory(
    Connection conn,
    String maHoaDon,
    String maCTHDThuoc,
    long maLo,
    String maCTPN,
    String maThuoc,
    String soLo,
    LocalDate hanSuDung,
    int soLuongXuat
  ) throws SQLException {
    String sql =
      "INSERT INTO LoThuocBienDong (MaLo, LoaiBienDong, SoLuong, ThoiDiem, NguonChungTuLoai, NguonChungTuMa, MaHoaDon, MaCTHDThuoc, MaCTPN, GhiChu, NguoiThucHien) " +
      "VALUES (?, 'ISSUE', ?, NOW(), 'HOA_DON_THUOC', ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, maLo);
      ps.setInt(2, soLuongXuat);
      ps.setString(3, maHoaDon);
      ps.setString(4, maHoaDon);
      ps.setString(5, maCTHDThuoc);
      ps.setString(6, maCTPN);
      ps.setString(
        7,
        "Xuat FEFO maThuoc=" + maThuoc + ", soLo=" + soLo + ", hsd=" + hanSuDung
      );
      ps.setString(8, safeUserName());
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

  private boolean deactivateDetail(Connection conn, String maCTHDThuoc)
    throws SQLException {
    String sql = "UPDATE CTHDThuoc SET Active = 0 WHERE MaCTHDThuoc = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, maCTHDThuoc);
      return ps.executeUpdate() > 0;
    }
  }

  private boolean updateDetailQuantity(
    Connection conn,
    String maCTHDThuoc,
    int soLuong,
    double donGia
  ) throws SQLException {
    String sql =
      "UPDATE CTHDThuoc SET SoLuong = ?, ThanhTien = ?, Active = 1 WHERE MaCTHDThuoc = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, soLuong);
      ps.setDouble(2, soLuong * donGia);
      ps.setString(3, maCTHDThuoc);
      return ps.executeUpdate() > 0;
    }
  }

  private boolean updateDetailFull(Connection conn, CTHDThuocDTO detail)
    throws SQLException {
    String sql =
      "UPDATE CTHDThuoc SET MaThuoc = ?, SoLuong = ?, DonGia = ?, ThanhTien = ?, GhiChu = ?, Active = 1 WHERE MaCTHDThuoc = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, detail.getMaThuoc());
      ps.setInt(2, detail.getSoLuong());
      ps.setDouble(3, detail.getDonGia());
      ps.setDouble(4, detail.getSoLuong() * detail.getDonGia());
      ps.setString(5, detail.getGhiChu());
      ps.setString(6, detail.getMaCTHDThuoc());
      return ps.executeUpdate() > 0;
    }
  }

  private boolean insertDetail(Connection conn, CTHDThuocDTO detail)
    throws SQLException {
    String sql =
      "INSERT INTO CTHDThuoc (MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, detail.getMaCTHDThuoc());
      ps.setString(2, detail.getMaHoaDon());
      ps.setString(3, detail.getMaThuoc());
      ps.setInt(4, detail.getSoLuong());
      ps.setDouble(5, detail.getDonGia());
      ps.setDouble(6, detail.getSoLuong() * detail.getDonGia());
      ps.setString(7, detail.getGhiChu());
      return ps.executeUpdate() > 0;
    }
  }

  private int getThuocTonKho(Connection conn, String maThuoc)
    throws SQLException {
    String sql =
      "SELECT SoLuongTon FROM Thuoc WHERE MaThuoc = ? AND Active = 1 LIMIT 1";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, maThuoc);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    }
    return 0;
  }

  private double calculateInvoiceTotalInTransaction(
    Connection conn,
    String maHoaDon
  ) throws SQLException {
    String sql =
      "SELECT COALESCE(SUM(ThanhTien), 0) FROM CTHDThuoc WHERE MaHoaDon = ? AND Active = 1";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, maHoaDon);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getDouble(1) : 0;
      }
    }
  }

  private boolean updateInvoiceSummary(
    Connection conn,
    String maHoaDon,
    double tongTien,
    String trangThaiThanhToan,
    LocalDateTime ngayThanhToan,
    String trangThaiLayThuoc,
    String ghiChu
  ) throws SQLException {
    String sql =
      "UPDATE HoaDonThuoc SET TongTien = ?, TrangThaiThanhToan = ?, NgayThanhToan = ?, TrangThaiLayThuoc = ?, GhiChu = ? WHERE MaHoaDon = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setDouble(1, tongTien);
      ps.setString(
        2,
        StatusNormalizer.normalizePaymentStatus(trangThaiThanhToan)
      );
      ps.setObject(3, ngayThanhToan);
      ps.setString(
        4,
        StatusNormalizer.normalizePickupStatus(trangThaiLayThuoc)
      );
      ps.setString(5, ghiChu);
      ps.setString(6, maHoaDon);
      return ps.executeUpdate() > 0;
    }
  }

  private Map<String, String> loadTenThuocMap(Connection conn)
    throws SQLException {
    Map<String, String> map = new HashMap<>();
    String sql = "SELECT MaThuoc, TenThuoc FROM Thuoc";
    try (
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery()
    ) {
      while (rs.next()) {
        map.put(rs.getString("MaThuoc"), rs.getString("TenThuoc"));
      }
    }
    return map;
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

    List<XuatThuocTheoLoDTO> result = new ArrayList<>();
    String sql =
      "SELECT bd.MaBienDong, bd.MaHoaDon, bd.MaCTHDThuoc, bd.MaCTPN, lt.MaThuoc, lt.SoLo, lt.HanSuDung, bd.SoLuong, bd.ThoiDiem " +
      "FROM LoThuocBienDong bd " +
      "JOIN LoThuoc lt ON lt.MaLo = bd.MaLo " +
      "WHERE bd.LoaiBienDong = 'ISSUE' AND bd.MaHoaDon = ? " +
      "ORDER BY bd.ThoiDiem ASC, bd.MaBienDong ASC";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maHoaDon.trim());
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          XuatThuocTheoLoDTO dto = new XuatThuocTheoLoDTO();
          dto.setMaXuatLo(rs.getLong("MaBienDong"));
          dto.setMaHoaDon(rs.getString("MaHoaDon"));
          dto.setMaCTHDThuoc(rs.getString("MaCTHDThuoc"));
          dto.setMaCTPN(rs.getString("MaCTPN"));
          dto.setMaThuoc(rs.getString("MaThuoc"));
          dto.setSoLo(rs.getString("SoLo"));
          dto.setHanSuDung(rs.getObject("HanSuDung", LocalDate.class));
          dto.setSoLuongXuat(rs.getInt("SoLuong"));
          dto.setNgayXuat(rs.getObject("ThoiDiem", LocalDateTime.class));
          result.add(dto);
        }
      }
    } catch (SQLException ex) {
      System.err.println(
        "Lỗi truy vấn lịch sử xuất theo lô mới: " + ex.getMessage()
      );
    }

    // Fallback cho dữ liệu cũ trước thời điểm cutover.
    if (result.isEmpty()) {
      return hoaDonThuocDAO.getXuatTheoLoByMaHoaDon(maHoaDon.trim());
    }
    return result;
  }

  private String safeUserName() {
    String username = Session.getCurrentUsername();
    return isBlank(username) ? "SYSTEM" : username.trim();
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

    private long maLo;
    private String maCTPN;
    private String soLo;
    private LocalDate hanSuDung;
    private int soLuongConLai;
  }
}
