package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.db.DBConnection;

public class HoSoBenhAnDAO {

  //=============METHOD CHUNG===================
  //✅METHOD DÙNG CHUNG ( TẠO DTO TỪ RESULTSET)
  private HoSoBenhAnDTO mapResultSet(ResultSet rs) throws SQLException {
    HoSoBenhAnDTO hsba = new HoSoBenhAnDTO();
    hsba.setMaHoSo(rs.getString("MaHoSo"));
    hsba.setMaLichKham(rs.getString("MaLichKham"));
    hsba.setHoTen(rs.getString("HoTen"));
    hsba.setSoDienThoai(rs.getString("SoDienThoai"));
    hsba.setCCCD(rs.getString("CCCD"));
    hsba.setNgaySinh(rs.getDate("NgaySinh"));
    hsba.setGioiTinh(rs.getString("GioiTinh"));
    hsba.setDiaChi(rs.getString("DiaChi"));
    hsba.setNgayKham(rs.getDate("NgayKham"));
    hsba.setTrieuChung(rs.getString("TrieuChung"));
    hsba.setChanDoan(rs.getString("ChanDoan"));
    hsba.setKetLuan(rs.getString("KetLuan"));
    hsba.setLoiDan(rs.getString("LoiDan"));
    hsba.setMaBacSi(rs.getString("MaBacSi"));
    hsba.setTrangThai(rs.getString("TrangThai"));
    return hsba;
  }

  //✅METHOD DÙNG CHUNG (EXECUTE QUERY TRẢ VỀ LIST)
  private ArrayList<HoSoBenhAnDTO> executeQuery(String sql, Object... params) {
    ArrayList<HoSoBenhAnDTO> list = new ArrayList<>();
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      //set param
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSet(rs));
        }
      }
    } catch (SQLException e) {
      System.out.println("Lỗi Query: " + e.getMessage());
    }
    return list;
  }

  //METHOD DÙNG CHUNG ( EXECUTE UPDATE )
  private boolean executeUpdate(String sql, Object... params) {
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.out.println("Lỗi update: " + e.getMessage());
    }
    return false;
  }

  //====================CRUD OPERATIONS==================
  public ArrayList<HoSoBenhAnDTO> getAll() {
    String sql = "SELECT * FROM HoSoBenhAn ORDER BY NgayKham DESC";
    return executeQuery(sql);
  }

  public HoSoBenhAnDTO getByMaHoSo(String maHoSo) {
    String sql = "SELECT * FROM HoSoBenhAn WHERE MaHoSo = ?";
    ArrayList<HoSoBenhAnDTO> list = executeQuery(sql, maHoSo);
    return list.isEmpty() ? null : list.get(0);
  }

  // Tra cứu theo Mã Lịch Khám
  public HoSoBenhAnDTO getByMaLichKham(String maLichKham) {
    String sql = "SELECT * FROM HoSoBenhAn WHERE MaLichKham = ?";
    ArrayList<HoSoBenhAnDTO> list = executeQuery(sql, maLichKham);
    return list.isEmpty() ? null : list.get(0);
  }

  // Tra cứu theo SĐT
  public ArrayList<HoSoBenhAnDTO> getBySoDienThoai(String sdt) {
    String sql =
      "SELECT * FROM HoSoBenhAn WHERE SoDienThoai = ? ORDER BY NgayKham DESC";
    return executeQuery(sql, sdt);
  }

  // Tra cứu theo CCCD
  public ArrayList<HoSoBenhAnDTO> getByCCCD(String cccd) {
    String sql =
      "SELECT * FROM HoSoBenhAn WHERE CCCD = ? ORDER BY NgayKham DESC";
    return executeQuery(sql, cccd);
  }

  // Tra cứu theo Họ Tên
  public ArrayList<HoSoBenhAnDTO> getByHoTen(String hoTen) {
    String sql =
      "SELECT * FROM HoSoBenhAn WHERE HoTen LIKE ? ORDER BY NgayKham DESC";
    return executeQuery(sql, "%" + hoTen + "%");
  }

  // Tra cứu theo Trạng thái
  public ArrayList<HoSoBenhAnDTO> getByTrangThai(String trangThai) {
    String sql =
      "SELECT * FROM HoSoBenhAn WHERE TrangThai = ? ORDER BY NgayKham DESC";
    return executeQuery(sql, trangThai);
  }

  // Cập nhật trạng thái
  public boolean updateTrangThai(String maHoSo, String trangThai) {
    String sql = "UPDATE HoSoBenhAn SET TrangThai = ? WHERE MaHoSo = ?";
    return executeUpdate(sql, trangThai, maHoSo);
  }

  // Cập nhật kết quả khám (bác sĩ sử dụng)
  public boolean updateKetQuaKham(
    String maHoSo,
    String trieuChung,
    String chanDoan,
    String ketLuan,
    String loiDan,
    String maBacSi
  ) {
    String sql =
      "UPDATE HoSoBenhAn SET TrieuChung=?, ChanDoan=?, KetLuan=?, LoiDan=?, MaBacSi=?, NgayKham=NOW(), TrangThai='DA_KHAM' WHERE MaHoSo=?";
    return executeUpdate(
      sql,
      trieuChung,
      chanDoan,
      ketLuan,
      loiDan,
      maBacSi,
      maHoSo
    );
  }

  public boolean insert(HoSoBenhAnDTO hs) {
    String sql =
      "INSERT INTO HoSoBenhAn (MaHoSo, MaLichKham, HoTen, SoDienThoai, CCCD, NgaySinh, GioiTinh, DiaChi, NgayKham, TrieuChung, ChanDoan, KetLuan, LoiDan, MaBacSi, TrangThai) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    return executeUpdate(
      sql,
      hs.getMaHoSo(),
      hs.getMaLichKham(),
      hs.getHoTen(),
      hs.getSoDienThoai(),
      hs.getCCCD(),
      hs.getNgaySinh(),
      hs.getGioiTinh(),
      hs.getDiaChi(),
      hs.getNgayKham(),
      hs.getTrieuChung(),
      hs.getChanDoan(),
      hs.getKetLuan(),
      hs.getLoiDan(),
      hs.getMaBacSi(),
      hs.getTrangThai()
    );
  }

  public boolean update(HoSoBenhAnDTO hs) {
    String sql =
      "UPDATE HoSoBenhAn SET MaLichKham=?, HoTen=?, SoDienThoai=?, CCCD=?, NgaySinh=?, GioiTinh=?, DiaChi=?, NgayKham=?, TrieuChung=?, ChanDoan=?, KetLuan=?, LoiDan=?, MaBacSi=?, TrangThai=? WHERE MaHoSo=?";
    return executeUpdate(
      sql,
      hs.getMaLichKham(),
      hs.getHoTen(),
      hs.getSoDienThoai(),
      hs.getCCCD(),
      hs.getNgaySinh(),
      hs.getGioiTinh(),
      hs.getDiaChi(),
      hs.getNgayKham(),
      hs.getTrieuChung(),
      hs.getChanDoan(),
      hs.getKetLuan(),
      hs.getLoiDan(),
      hs.getMaBacSi(),
      hs.getTrangThai(),
      hs.getMaHoSo()
    );
  }

  public boolean delete(String MaHoSo) {
    String sql = "DELETE FROM HoSoBenhAn WHERE MaHoSo = ?";
    return executeUpdate(sql, MaHoSo);
  }

  public ArrayList<HoSoBenhAnDTO> search(String keyword) {
    String sql =
      "SELECT * FROM HoSoBenhAn WHERE MaHoSo LIKE ? OR HoTen LIKE ? OR SoDienThoai LIKE ? OR CCCD LIKE ? ORDER BY NgayKham DESC";
    String key = "%" + keyword + "%";
    return executeQuery(sql, key, key, key, key);
  }

  public boolean exists(String MaHS) {
    String sql = "SELECT 1 FROM HoSoBenhAn WHERE MaHoSo = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, MaHS);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      System.out.println("Lỗi exists: " + e.getMessage());
    }
    return false;
  }
}
