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

  public ArrayList<HoaDonKhamDTO> getAll() {
    ArrayList<HoaDonKhamDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM HoaDonKham";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        LocalDateTime localDateTime = rs.getObject(
          "NgayThanhToan",
          LocalDateTime.class
        );
        HoaDonKhamDTO hd = new HoaDonKhamDTO(
          rs.getString("MaHDKham"),
          rs.getString("MaHoSo"),
          rs.getString("MaGoi"),
          localDateTime,
          rs.getBigDecimal("TongTien"),
          rs.getString("HinhThucThanhToan"),
          rs.getString("TrangThai")
        );

        list.add(hd);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public boolean Insert(HoaDonKhamDTO hd) {
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

  public boolean Delete(String MaHDKham) {
    String sql = "DELETE FROM HoaDonKham WHERE MaHDKham = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, MaHDKham);

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean Update(HoaDonKhamDTO hd) {
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

  public HoaDonKhamDTO Search(String MaHDKham) {
    String sql = "SELECT * FROM HoaDonKham WHERE MaHDKham = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, MaHDKham);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        LocalDateTime localDateTime = rs.getObject(
          "NgayThanhToan",
          LocalDateTime.class
        );
        return new HoaDonKhamDTO(
          rs.getString("MaHDKham"),
          rs.getString("MaHoSo"),
          rs.getString("MaGoi"),
          localDateTime,
          rs.getBigDecimal("TongTien"),
          rs.getString("HinhThucThanhToan"),
          rs.getString("TrangThai")
        );
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
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        LocalDateTime localDateTime = rs.getObject(
          "NgayThanhToan",
          LocalDateTime.class
        );
        HoaDonKhamDTO hd = new HoaDonKhamDTO(
          rs.getString("MaHDKham"),
          rs.getString("MaHoSo"),
          rs.getString("MaGoi"),
          localDateTime,
          rs.getBigDecimal("TongTien"),
          rs.getString("HinhThucThanhToan"),
          rs.getString("TrangThai")
        );
        list.add(hd);
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
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        LocalDateTime localDateTime = rs.getObject(
          "NgayThanhToan",
          LocalDateTime.class
        );
        HoaDonKhamDTO hd = new HoaDonKhamDTO(
          rs.getString("MaHDKham"),
          rs.getString("MaHoSo"),
          rs.getString("MaGoi"),
          localDateTime,
          rs.getBigDecimal("TongTien"),
          rs.getString("HinhThucThanhToan"),
          rs.getString("TrangThai")
        );
        list.add(hd);
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
}
