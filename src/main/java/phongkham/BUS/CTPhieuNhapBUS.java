package phongkham.BUS;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.DTO.LoThuocNhapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.dao.CTPhieuNhapDAO;
import phongkham.dao.PhieuNhapDAO;
import phongkham.db.DBConnection;

public class CTPhieuNhapBUS {

  private PhieuNhapDAO phieuNhapDAO;
  private CTPhieuNhapDAO ctDAO;
  private ThuocBUS thuocBUS;

  public CTPhieuNhapBUS() {
    ctDAO = new CTPhieuNhapDAO();
    phieuNhapDAO = new PhieuNhapDAO();
    thuocBUS = new ThuocBUS();
  }

  // ================= LOAD =================
  public ArrayList<CTPhieuNhapDTO> getByMaPhieuNhap(String maPN) {
    if (maPN == null || maPN.trim().isEmpty()) return new ArrayList<>();
    return ctDAO.getByMaPhieuNhap(maPN);
  }

  public ArrayList<LoThuocNhapDTO> getLotOverview(
    String keyword,
    String maNCC,
    String hsdFilter
  ) {
    ArrayList<LoThuocNhapDTO> raw = ctDAO.getLotOverview(keyword, maNCC);
    ArrayList<LoThuocNhapDTO> filtered = new ArrayList<>();

    String filter = hsdFilter == null ? "TAT_CA" : hsdFilter.trim();
    LocalDate today = LocalDate.now();
    LocalDate warning30 = today.plusDays(30);
    LocalDate warning60 = today.plusDays(60);
    LocalDate warning90 = today.plusDays(90);

    for (LoThuocNhapDTO row : raw) {
      LocalDate hsd = row.getHanSuDung();
      if ("CON_HAN".equals(filter)) {
        if (hsd != null && hsd.isBefore(today)) {
          continue;
        }
      } else if ("HET_HAN".equals(filter)) {
        if (hsd == null || !hsd.isBefore(today)) {
          continue;
        }
      } else if ("SAP_HET_HAN_30".equals(filter)) {
        if (hsd == null || hsd.isBefore(today) || hsd.isAfter(warning30)) {
          continue;
        }
      } else if ("SAP_HET_HAN_60".equals(filter)) {
        if (hsd == null || hsd.isBefore(today) || hsd.isAfter(warning60)) {
          continue;
        }
      } else if ("SAP_HET_HAN_90".equals(filter)) {
        if (hsd == null || hsd.isBefore(today) || hsd.isAfter(warning90)) {
          continue;
        }
      }
      filtered.add(row);
    }

    return filtered;
  }

  public ArrayList<LoThuocNhapDTO> getAllLotsForMonitoring() {
    return ctDAO.getLotOverview("", "");
  }

  public int createDisposalForExpiredLots(String lyDo, String nguoiThucHien) {
    String reason =
      lyDo == null || lyDo.trim().isEmpty() ? "HET_HAN" : lyDo.trim();
    String actor =
      nguoiThucHien == null || nguoiThucHien.trim().isEmpty()
        ? "SYSTEM"
        : nguoiThucHien.trim();

    int disposedRows = 0;
    String sqlSelect =
      "SELECT ctpn.MaCTPN, ctpn.MaPhieuNhap, ctpn.MaThuoc, ctpn.SoLo, ctpn.HanSuDung, ctpn.SoLuongConLai " +
      "FROM ChiTietPhieuNhap ctpn " +
      "JOIN PhieuNhap pn ON pn.MaPhieuNhap = ctpn.MaPhieuNhap " +
      "WHERE ctpn.SoLuongConLai > 0 " +
      "  AND ctpn.HanSuDung IS NOT NULL " +
      "  AND DATE(ctpn.HanSuDung) <= CURDATE() " +
      "  AND UPPER(TRIM(COALESCE(pn.TrangThai, ''))) IN ('DA_NHAP', 'DA_NHAP_KHO') " +
      "FOR UPDATE";
    String sqlUpdate =
      "UPDATE ChiTietPhieuNhap SET SoLuongConLai = 0 WHERE MaCTPN = ? AND SoLuongConLai > 0";
    String sqlInsertHistory =
      "INSERT INTO TieuHuyLoThuoc (MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongTieuHuy, HanSuDung, NgayTieuHuy, LyDo, NguoiThucHien) " +
      "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?)";

    try (Connection conn = DBConnection.getConnection()) {
      conn.setAutoCommit(false);
      try (
        PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
        PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
        PreparedStatement psInsert = conn.prepareStatement(sqlInsertHistory)
      ) {
        java.sql.ResultSet rs = psSelect.executeQuery();
        while (rs.next()) {
          String maCTPN = rs.getString("MaCTPN");
          String maPN = rs.getString("MaPhieuNhap");
          String maThuoc = rs.getString("MaThuoc");
          String soLo = rs.getString("SoLo");
          LocalDateTime hanSuDung = rs.getObject(
            "HanSuDung",
            LocalDateTime.class
          );
          int soLuongTieuHuy = rs.getInt("SoLuongConLai");

          psUpdate.setString(1, maCTPN);
          int updated = psUpdate.executeUpdate();
          if (updated <= 0) {
            continue;
          }

          psInsert.setString(1, maCTPN);
          psInsert.setString(2, maPN);
          psInsert.setString(3, maThuoc);
          psInsert.setString(4, soLo);
          psInsert.setInt(5, soLuongTieuHuy);
          psInsert.setObject(6, hanSuDung);
          psInsert.setString(7, reason);
          psInsert.setString(8, actor);
          psInsert.executeUpdate();

          disposedRows++;
        }

        conn.commit();
      } catch (Exception ex) {
        conn.rollback();
        throw ex;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return -1;
    }

    if (disposedRows > 0) {
      thuocBUS.dongBoTonKhoTheoHanSuDungToanBo();
    }
    return disposedRows;
  }

  // ================= THÊM =================
  public boolean insert(
    String maCTPN,
    String maPN,
    String maThuoc,
    String soLo,
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
    ct.setSoLo(soLo);
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

      String status = StatusNormalizer.normalizePhieuNhapStatus(
        pn.getTrangThai()
      );
      if (
        !StatusNormalizer.CHO_DUYET.equals(status) &&
        !StatusNormalizer.DA_DUYET.equals(status)
      ) {
        return false;
      }

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

    if (ct.getSoLo() == null || ct.getSoLo().trim().isEmpty()) return false;

    if (ct.getSoLuongNhap() <= 0) return false;

    if (
      ct.getDonGiaNhap() == null ||
      ct.getDonGiaNhap().compareTo(BigDecimal.ZERO) <= 0
    ) return false;

    // Không cho nhập lô đã quá hạn (cho phép HSD = hôm nay).
    if (
      ct.getHanSuDung() != null &&
      ct.getHanSuDung().toLocalDate().isBefore(LocalDate.now())
    ) return false;

    return true;
  }

  private boolean isTrangThaiChuaDuyet(String trangThai) {
    return "CHO_DUYET".equals(
      StatusNormalizer.normalizePhieuNhapStatus(trangThai)
    );
  }
}
