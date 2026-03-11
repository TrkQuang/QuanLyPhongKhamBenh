package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.HoaDonKhamDTO;
import phongkham.db.DBConnection;

public class HoaDonKhamDAO {

  private HoaDonKhamDTO mapResultSet(ResultSet rs) throws SQLException {
    return new HoaDonKhamDTO(
      rs.getString("MaHDKham"),
      rs.getString("MaHoSo"),
      rs.getString("MaGoi"),
      rs.getObject("NgayThanhToan", LocalDateTime.class),
      rs.getBigDecimal("TongTien"),
      rs.getString("HinhThucThanhToan"),
      rs.getString("TrangThai")
    );
  }

  public ArrayList<HoaDonKhamDTO> getAll() {
    ArrayList<HoaDonKhamDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM HoaDonKham";
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

  public boolean insert(HoaDonKhamDTO hd) {
    String sql =
      "INSERT INTO HoaDonKham(MaHDKham, MaHoSo, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, hd.getMaHDKham());
      ps.setString(2, hd.getMaHoSo());
      ps.setString(3, hd.getMaGoi());
      ps.setObject(4, hd.getNgayThanhToan());
      ps.setBigDecimal(5, hd.getTongTien());
      ps.setString(6, hd.getHinhThucThanhToan());
      ps.setString(7, hd.getTrangThai());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean delete(String maHDKham) {
    String sql = "DELETE FROM HoaDonKham WHERE MaHDKham = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maHDKham);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean update(HoaDonKhamDTO hd) {
    String sql =
      "UPDATE HoaDonKham SET MaHoSo = ?, MaGoi= ?, NgayThanhToan = ?, TongTien = ?, HinhThucThanhToan = ?, TrangThai = ? WHERE MaHDKham = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, hd.getMaHoSo());
      ps.setString(2, hd.getMaGoi());
      ps.setObject(3, hd.getNgayThanhToan());
      ps.setBigDecimal(4, hd.getTongTien());
      ps.setString(5, hd.getHinhThucThanhToan());
      ps.setString(6, hd.getTrangThai());
      ps.setString(7, hd.getMaHDKham());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public HoaDonKhamDTO search(String maHDKham) {
    String sql = "SELECT * FROM HoaDonKham WHERE MaHDKham = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maHDKham);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return mapResultSet(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // Lấy hóa đơn khám theo trạng thái (CHO_THANH_TOAN hoặc DA_THANH_TOAN)
  public ArrayList<HoaDonKhamDTO> getByTrangThai(String trangThai) {
    ArrayList<HoaDonKhamDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM HoaDonKham WHERE TrangThai = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, trangThai);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  // Lấy hóa đơn khám theo mã hồ sơ bệnh án
  public ArrayList<HoaDonKhamDTO> getByMaHoSo(String maHoSo) {
    ArrayList<HoaDonKhamDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM HoaDonKham WHERE MaHoSo = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maHoSo);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  // Cập nhật trạng thái thanh toán
  public boolean updateTrangThai(String maHDKham, String trangThaiMoi) {
    String sql = "UPDATE HoaDonKham SET TrangThai = ? WHERE MaHDKham = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, trangThaiMoi);
      ps.setString(2, maHDKham);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public ArrayList<HoaDonKhamDTO> filterByDate(
    LocalDateTime from,
    LocalDateTime to
  ) {
    ArrayList<HoaDonKhamDTO> list = new ArrayList<>();
    String sql =
      "SELECT * FROM HoaDonKham WHERE NgayThanhToan BETWEEN ? AND ? ";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setObject(1, from);
      ps.setObject(2, to);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return list;
  }
}
