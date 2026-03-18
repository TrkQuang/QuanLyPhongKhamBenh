package phongkham.BUS;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.dao.CTPhieuNhapDAO;
import phongkham.dao.PhieuNhapDAO;
import phongkham.db.DBConnection;

public class CTPhieuNhapBUS {

  private PhieuNhapDAO phieuNhapDAO;
  private CTPhieuNhapDAO ctDAO;

  public CTPhieuNhapBUS() {
    ctDAO = new CTPhieuNhapDAO();
    phieuNhapDAO = new PhieuNhapDAO();
  }

  // ================= LOAD =================
  public ArrayList<CTPhieuNhapDTO> getByMaPhieuNhap(String maPN) {
    if (maPN == null || maPN.trim().isEmpty()) return new ArrayList<>();
    return ctDAO.getByMaPhieuNhap(maPN);
  }

  // ================= THÊM =================
  public boolean insert(
    String maCTPN,
    String maPN,
    String maThuoc,
    int soLuong,
    BigDecimal donGia,
    LocalDateTime hanSuDung
  ) {
    PhieuNhapDTO pn = phieuNhapDAO.getById(maPN);

    if (pn == null) return false;

    // chỉ cho thêm khi CHUA_DUYET
    if (!isTrangThaiChuaDuyet(pn.getTrangThai())) return false;

    CTPhieuNhapDTO ct = new CTPhieuNhapDTO();

    ct.setMaCTPN(maCTPN);
    ct.setMaPhieuNhap(maPN);
    ct.setMaThuoc(maThuoc);
    ct.setSoLuong(soLuong);
    ct.setDonGiaNhap(donGia);
    ct.setHanSuDung(hanSuDung);

    if (!validate(ct)) return false;

    boolean result = ctDAO.Insert(ct);

    if (result) capNhatTongTien(maPN);

    return result;
  }

  // ================= SỬA =================
  public boolean update(String maCTPN, int soLuong, BigDecimal donGia) {
    CTPhieuNhapDTO ct = ctDAO.Search(maCTPN);

    if (ct == null) return false;

    PhieuNhapDTO pn = phieuNhapDAO.getById(ct.getMaPhieuNhap());

    // ❌ Chỉ cho sửa khi CHO_DUYET
    if (!isTrangThaiChuaDuyet(pn.getTrangThai())) return false;

    ct.setSoLuong(soLuong);
    ct.setDonGiaNhap(donGia);

    if (!validate(ct)) return false;

    boolean result = ctDAO.Update(ct);

    if (result) capNhatTongTien(ct.getMaPhieuNhap());

    return result;
  }

  // ================= XÓA =================
  public boolean delete(String maCTPN) {
    CTPhieuNhapDTO ct = ctDAO.Search(maCTPN);

    if (ct == null) return false;

    PhieuNhapDTO pn = phieuNhapDAO.getById(ct.getMaPhieuNhap());

    // ❌ Chỉ cho xóa khi CHO_DUYET
    if (!isTrangThaiChuaDuyet(pn.getTrangThai())) return false;

    boolean result = ctDAO.Delete(maCTPN);

    if (result) capNhatTongTien(ct.getMaPhieuNhap());

    return result;
  }

  // ================= TÍNH TỔNG =================
  public BigDecimal tinhTongTien(String maPN) {
    BigDecimal tong = BigDecimal.ZERO;

    for (CTPhieuNhapDTO ct : getByMaPhieuNhap(maPN)) {
      BigDecimal thanhTien = ct
        .getDonGiaNhap()
        .multiply(BigDecimal.valueOf(ct.getSoLuongNhap()));

      tong = tong.add(thanhTien);
    }

    return tong;
  }

  // ================= CẬP NHẬT TỔNG TIỀN VỀ PHIẾU =================
  private void capNhatTongTien(String maPN) {
    BigDecimal tong = tinhTongTien(maPN);
    phieuNhapDAO.updateTongTien(maPN, tong);
  }

  // ================= NHẬP KHO =================
  public boolean xacNhanNhapKho(String maPN) {
    return xacNhanNhapKhoWithSimulation(maPN, false);
  }

  public boolean xacNhanNhapKhoWithSimulation(
    String maPN,
    boolean testFailAfterFirstLine
  ) {
    try {
      PhieuNhapDTO pn = phieuNhapDAO.getById(maPN);

      if (pn == null) return false;

      if (
        StatusNormalizer.DA_NHAP.equals(
          StatusNormalizer.normalizePhieuNhapStatus(pn.getTrangThai())
        )
      ) return false;

      if (
        !StatusNormalizer.DA_DUYET.equals(
          StatusNormalizer.normalizePhieuNhapStatus(pn.getTrangThai())
        )
      ) return false;

      ArrayList<CTPhieuNhapDTO> list = getByMaPhieuNhap(maPN);
      if (list == null || list.isEmpty()) {
        return false;
      }

      try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);
        try {
          int daXuLy = 0;
          for (CTPhieuNhapDTO ct : list) {
            if (
              !congTonKhoTrongTransaction(
                conn,
                ct.getMaThuoc(),
                ct.getSoLuongNhap()
              )
            ) {
              conn.rollback();
              return false;
            }

            daXuLy++;
            if (testFailAfterFirstLine && daXuLy == 1) {
              throw new SQLException("TEST_SIMULATION_IMPORT_ROLLBACK");
            }
          }

          if (
            !capNhatTrangThaiPhieuNhapTrongTransaction(
              conn,
              maPN,
              StatusNormalizer.DA_NHAP
            )
          ) {
            conn.rollback();
            return false;
          }

          conn.commit();
          return true;
        } catch (Exception ex) {
          conn.rollback();
          return false;
        } finally {
          conn.setAutoCommit(true);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean congTonKhoTrongTransaction(
    Connection conn,
    String maThuoc,
    int soLuongThem
  ) throws SQLException {
    String sql =
      "UPDATE Thuoc SET SoLuongTon = SoLuongTon + ? WHERE MaThuoc = ? AND Active = 1";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, soLuongThem);
      ps.setString(2, maThuoc);
      return ps.executeUpdate() > 0;
    }
  }

  private boolean capNhatTrangThaiPhieuNhapTrongTransaction(
    Connection conn,
    String maPN,
    String trangThai
  ) throws SQLException {
    String sql = "UPDATE PhieuNhap SET TrangThai = ? WHERE MaPhieuNhap = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, StatusNormalizer.normalizePhieuNhapStatus(trangThai));
      ps.setString(2, maPN);
      return ps.executeUpdate() > 0;
    }
  }

  // ================= VALIDATE =================
  private boolean validate(CTPhieuNhapDTO ct) {
    if (ct == null) return false;

    if (
      ct.getMaPhieuNhap() == null || ct.getMaPhieuNhap().trim().isEmpty()
    ) return false;

    if (
      ct.getMaThuoc() == null || ct.getMaThuoc().trim().isEmpty()
    ) return false;

    if (ct.getSoLuongNhap() <= 0) return false;

    if (
      ct.getDonGiaNhap() == null ||
      ct.getDonGiaNhap().compareTo(BigDecimal.ZERO) <= 0
    ) return false;

    // Không cho nhập lô thuốc đã hết hạn.
    if (
      ct.getHanSuDung() != null &&
      ct.getHanSuDung().isBefore(LocalDateTime.now())
    ) return false;

    return true;
  }

  private boolean isTrangThaiChuaDuyet(String trangThai) {
    return "CHO_DUYET".equals(
      StatusNormalizer.normalizePhieuNhapStatus(trangThai)
    );
  }
}
