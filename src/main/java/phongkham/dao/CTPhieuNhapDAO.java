package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.DTO.LoThuocNhapDTO;
import phongkham.db.DBConnection;

public class CTPhieuNhapDAO {

  public ArrayList<CTPhieuNhapDTO> getAll() {
    ArrayList<CTPhieuNhapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM ChiTietPhieuNhap";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        LocalDateTime localDateTime = rs.getObject(
          "HanSuDung",
          LocalDateTime.class
        );
        CTPhieuNhapDTO ctpn = new CTPhieuNhapDTO(
          rs.getString("MaCTPN"),
          rs.getString("MaPhieuNhap"),
          rs.getString("MaThuoc"),
          rs.getString("SoLo"),
          rs.getInt("SoLuongNhap"),
          rs.getInt("SoLuongConLai"),
          rs.getBigDecimal("DonGiaNhap"),
          localDateTime
        );
        list.add(ctpn);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public boolean Insert(CTPhieuNhapDTO ctpn) {
    String sqp =
      "INSERT INTO ChiTietPhieuNhap(MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongNhap, SoLuongConLai, DonGiaNhap, HanSuDung) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sqp);
    ) {
      ps.setString(1, ctpn.getMaCTPN());
      ps.setString(2, ctpn.getMaPhieuNhap());
      ps.setString(3, ctpn.getMaThuoc());
      ps.setString(4, ctpn.getSoLo());
      ps.setInt(5, ctpn.getSoLuongNhap());
      ps.setInt(6, ctpn.getSoLuongNhap());
      ps.setBigDecimal(7, ctpn.getDonGiaNhap());
      if (ctpn.getHanSuDung() != null) {
        ps.setObject(8, ctpn.getHanSuDung());
      } else {
        ps.setNull(8, java.sql.Types.DATE);
      }
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean Delete(String MaCTPN) {
    String sqp = "DELETE FROM ChiTietPhieuNhap WHERE MaCTPN = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sqp);
    ) {
      ps.setString(1, MaCTPN);

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean Update(CTPhieuNhapDTO ctpn) {
    String sql =
      "UPDATE ChiTietPhieuNhap SET MaPhieuNhap = ?, MaThuoc = ?, SoLo = ?, SoLuongNhap = ?, SoLuongConLai = ?, DonGiaNhap = ?, HanSuDung = ? WHERE MaCTPN = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, ctpn.getMaPhieuNhap());
      ps.setString(2, ctpn.getMaThuoc());
      ps.setString(3, ctpn.getSoLo());
      ps.setInt(4, ctpn.getSoLuongNhap());
      ps.setInt(5, ctpn.getSoLuongNhap());
      ps.setBigDecimal(6, ctpn.getDonGiaNhap());
      ps.setObject(7, ctpn.getHanSuDung());
      ps.setString(8, ctpn.getMaCTPN());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public CTPhieuNhapDTO Search(String MaCTPN) {
    String sql = "SELECT * FROM ChiTietPhieuNhap WHERE MaCTPN = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, MaCTPN);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        LocalDateTime localDateTime = rs.getObject(
          "HanSuDung",
          LocalDateTime.class
        );
        return new CTPhieuNhapDTO(
          rs.getString("MaCTPN"),
          rs.getString("MaPhieuNhap"),
          rs.getString("MaThuoc"),
          rs.getString("SoLo"),
          rs.getInt("SoLuongNhap"),
          rs.getInt("SoLuongConLai"),
          rs.getBigDecimal("DonGiaNhap"),
          localDateTime
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ArrayList<CTPhieuNhapDTO> getByMaPhieuNhap(String MaPhieuNhap) {
    ArrayList<CTPhieuNhapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, MaPhieuNhap);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        LocalDateTime localDateTime = rs.getObject(
          "HanSuDung",
          LocalDateTime.class
        );
        CTPhieuNhapDTO ctpn = new CTPhieuNhapDTO(
          rs.getString("MaCTPN"),
          rs.getString("MaPhieuNhap"),
          rs.getString("MaThuoc"),
          rs.getString("SoLo"),
          rs.getInt("SoLuongNhap"),
          rs.getInt("SoLuongConLai"),
          rs.getBigDecimal("DonGiaNhap"),
          localDateTime
        );
        list.add(ctpn);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public boolean deleteByMaPhieuNhap(String maPhieuNhap) {
    String sql = "DELETE FROM ChiTietPhieuNhap WHERE MaPhieuNhap=?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maPhieuNhap);
      ps.executeUpdate();
      return true;
    } catch (Exception e) {
      System.err.println("❌ Lỗi xóa chi tiết: " + e.getMessage());
      return false;
    }
  }

  public ArrayList<LoThuocNhapDTO> getLotOverview(
    String keyword,
    String maNCC
  ) {
    ArrayList<LoThuocNhapDTO> list = new ArrayList<>();
    String sql =
      "SELECT ctpn.MaCTPN, ctpn.MaPhieuNhap, pn.MaNCC, ncc.TenNhaCungCap, " +
      "       ctpn.MaThuoc, t.TenThuoc, ctpn.SoLo, ctpn.HanSuDung, " +
      "       ctpn.SoLuongNhap, ctpn.SoLuongConLai, ctpn.DonGiaNhap, pn.NgayNhap, pn.TrangThai " +
      "FROM ChiTietPhieuNhap ctpn " +
      "JOIN PhieuNhap pn ON pn.MaPhieuNhap = ctpn.MaPhieuNhap " +
      "JOIN NhaCungCap ncc ON ncc.MaNhaCungCap = pn.MaNCC " +
      "JOIN Thuoc t ON t.MaThuoc = ctpn.MaThuoc " +
      "WHERE (? = '' OR ctpn.MaPhieuNhap LIKE ? OR ctpn.MaThuoc LIKE ? OR ctpn.SoLo LIKE ? OR t.TenThuoc LIKE ? OR ncc.TenNhaCungCap LIKE ?) " +
      "  AND (? = '' OR pn.MaNCC = ?) " +
      "ORDER BY ctpn.HanSuDung ASC, pn.NgayNhap DESC";

    String kw = keyword == null ? "" : keyword.trim();
    String maNccValue = maNCC == null ? "" : maNCC.trim();

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, kw);
      ps.setString(2, "%" + kw + "%");
      ps.setString(3, "%" + kw + "%");
      ps.setString(4, "%" + kw + "%");
      ps.setString(5, "%" + kw + "%");
      ps.setString(6, "%" + kw + "%");
      ps.setString(7, maNccValue);
      ps.setString(8, maNccValue);

      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        LoThuocNhapDTO lo = new LoThuocNhapDTO();
        lo.setMaCTPN(rs.getString("MaCTPN"));
        lo.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
        lo.setMaNCC(rs.getString("MaNCC"));
        lo.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        lo.setMaThuoc(rs.getString("MaThuoc"));
        lo.setTenThuoc(rs.getString("TenThuoc"));
        lo.setSoLo(rs.getString("SoLo"));
        lo.setHanSuDung(rs.getObject("HanSuDung", LocalDate.class));
        lo.setSoLuongNhap(rs.getInt("SoLuongNhap"));
        lo.setSoLuongConLai(rs.getInt("SoLuongConLai"));
        lo.setDonGiaNhap(rs.getBigDecimal("DonGiaNhap"));
        lo.setNgayNhap(rs.getObject("NgayNhap", LocalDateTime.class));
        lo.setTrangThaiPhieuNhap(rs.getString("TrangThai"));
        list.add(lo);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return list;
  }
}
