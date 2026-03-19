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

  private ArrayList<HoaDonKhamDTO> executeQuery(String sql, Object... params) {
    ArrayList<HoaDonKhamDTO> list = new ArrayList<>();
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSet(rs));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  private boolean executeUpdate(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public ArrayList<HoaDonKhamDTO> getAll() {
    return executeQuery("SELECT * FROM HoaDonKham");
  }

  public boolean Insert(HoaDonKhamDTO hd) {
    return executeUpdate(
      "INSERT INTO HoaDonKham(MaHDKham, MaHoSo, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)",
      hd.getMaHDKham(),
      hd.getMaHoSo(),
      hd.getMaGoi(),
      hd.getNgayThanhToan(),
      hd.getTongTien(),
      hd.getHinhThucThanhToan(),
      hd.getTrangThai()
    );
  }

  public boolean Delete(String MaHDKham) {
    return executeUpdate("DELETE FROM HoaDonKham WHERE MaHDKham = ?", MaHDKham);
  }



  public HoaDonKhamDTO Search(String MaHDKham) {
    ArrayList<HoaDonKhamDTO> list = executeQuery(
      "SELECT * FROM HoaDonKham WHERE MaHDKham = ?",
      MaHDKham
    );
    return list.isEmpty() ? null : list.get(0);
  }

  // Lấy hóa đơn khám theo trạng thái (CHO_THANH_TOAN hoặc DA_THANH_TOAN)
  public ArrayList<HoaDonKhamDTO> getByTrangThai(String trangThai) {
    return executeQuery(
      "SELECT * FROM HoaDonKham WHERE TrangThai = ?",
      trangThai
    );
  }

  // Lấy hóa đơn khám theo mã hồ sơ bệnh án
  public ArrayList<HoaDonKhamDTO> getByMaHoSo(String maHoSo) {
    return executeQuery("SELECT * FROM HoaDonKham WHERE MaHoSo = ?", maHoSo);
  }

  // Cập nhật trạng thái thanh toán
  public boolean updateTrangThai(String maHDKham, String trangThaiMoi) {
    return executeUpdate(
      "UPDATE HoaDonKham SET TrangThai = ? WHERE MaHDKham = ?",
      trangThaiMoi,
      maHDKham
    );
  }

  public ArrayList<HoaDonKhamDTO> filterByDate(
    LocalDateTime from,
    LocalDateTime to
  ) {
    return executeQuery(
      "SELECT * FROM HoaDonKham WHERE NgayThanhToan BETWEEN ? AND ?",
      from,
      to
    );
  }
}
