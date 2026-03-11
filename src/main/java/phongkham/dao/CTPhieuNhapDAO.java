package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.db.DBConnection;

public class CTPhieuNhapDAO {

  private CTPhieuNhapDTO mapResultSet(ResultSet rs) throws SQLException {
    return new CTPhieuNhapDTO(
      rs.getString("MaCTPN"),
      rs.getString("MaPhieuNhap"),
      rs.getString("MaThuoc"),
      rs.getInt("SoLuongNhap"),
      rs.getBigDecimal("DonGiaNhap"),
      rs.getObject("HanSuDung", LocalDateTime.class)
    );
  }

  public ArrayList<CTPhieuNhapDTO> getAll() {
    ArrayList<CTPhieuNhapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM ChiTietPhieuNhap";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery()
    ) {
      while (rs.next()) list.add(mapResultSet(rs));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public boolean insert(CTPhieuNhapDTO ctpn) {
    String sqp =
      "INSERT INTO ChiTietPhieuNhap(MaCTPN, MaPhieuNhap, MaThuoc, SoLuongNhap, DonGiaNhap, HanSuDung) VALUES (?, ?, ?, ?, ?, ?)";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sqp);
    ) {
      ps.setString(1, ctpn.getMaCTPN());
      ps.setString(2, ctpn.getMaPhieuNhap());
      ps.setString(3, ctpn.getMaThuoc());
      ps.setInt(4, ctpn.getSoLuongNhap());
      ps.setBigDecimal(5, ctpn.getDonGiaNhap());
      if (ctpn.getHanSuDung() != null) {
        ps.setObject(6, ctpn.getHanSuDung());
      } else {
        ps.setNull(6, java.sql.Types.DATE);
      }
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean delete(String maCTPN) {
    String sqp = "DELETE FROM ChiTietPhieuNhap WHERE MaCTPN = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sqp)
    ) {
      ps.setString(1, maCTPN);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean update(CTPhieuNhapDTO ctpn) {
    String sql =
      "UPDATE ChiTietPhieuNhap SET MaPhieuNhap = ?, MaThuoc = ?, SoLuongNhap = ?, DonGiaNhap = ?, HanSuDung = ? WHERE MaCTPN = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, ctpn.getMaPhieuNhap());
      ps.setString(2, ctpn.getMaThuoc());
      ps.setInt(3, ctpn.getSoLuongNhap());
      ps.setBigDecimal(4, ctpn.getDonGiaNhap());
      ps.setObject(5, ctpn.getHanSuDung());
      ps.setString(6, ctpn.getMaCTPN());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public CTPhieuNhapDTO search(String maCTPN) {
    String sql = "SELECT * FROM ChiTietPhieuNhap WHERE MaCTPN = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maCTPN);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return mapResultSet(rs);
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
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapResultSet(rs));
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
}
