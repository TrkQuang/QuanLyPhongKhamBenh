package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import phongkham.DTO.NhaCungCapDTO;
import phongkham.db.DBConnection;

public class NhaCungCapDAO {

  private NhaCungCapDTO mapResultSet(ResultSet rs) throws SQLException {
    NhaCungCapDTO ncc = new NhaCungCapDTO();
    ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
    ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
    ncc.setDiaChi(rs.getString("DiaChi"));
    ncc.setSDT(rs.getString("SDT"));
    ncc.setActive(rs.getBoolean("Active"));
    return ncc;
  }

  private ArrayList<NhaCungCapDTO> executeQuery(String sql, Object... params) {
    ArrayList<NhaCungCapDTO> ds = new ArrayList<>();
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ds.add(mapResultSet(rs));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
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
      System.err.println("Lỗi update nhà cung cấp: " + e.getMessage());
    }
    return false;
  }

  public ArrayList<NhaCungCapDTO> getAllNhaCungCap() {
    return executeQuery("SELECT * FROM NhaCungCap");
  }

  //thêm mới
  public boolean insertNhaCungCap(NhaCungCapDTO ncc) {
    return executeUpdate(
      "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SDT, Active) VALUES (?,?,?,?,?)",
      ncc.getMaNhaCungCap(),
      ncc.getTenNhaCungCap(),
      ncc.getDiaChi(),
      ncc.getSDT(),
      ncc.isActive()
    );
  }

  //cập nhật
  public boolean updateNhaCungCap(NhaCungCapDTO ncc) {
    return executeUpdate(
      "UPDATE NhaCungCap SET TenNhaCungCap=?, DiaChi=?, SDT=?, Active=? WHERE MaNhaCungCap=?",
      ncc.getTenNhaCungCap(),
      ncc.getDiaChi(),
      ncc.getSDT(),
      ncc.isActive(),
      ncc.getMaNhaCungCap()
    );
  }

  //xóa theo mã
  public boolean deleteNhaCungCap(String MaNhaCungCap) {
    return executeUpdate(
      "DELETE FROM NhaCungCap WHERE MaNhaCungCap=?",
      MaNhaCungCap
    );
  }

  //lấy theo mã
  public NhaCungCapDTO getById(String MaNhaCungCap) {
    ArrayList<NhaCungCapDTO> list = executeQuery(
      "SELECT * FROM NhaCungCap WHERE MaNhaCungCap=?",
      MaNhaCungCap
    );
    return list.isEmpty() ? null : list.get(0);
  }

  //tìm theo tên
  public ArrayList<NhaCungCapDTO> searchByName(String name) {
    return executeQuery(
      "SELECT * FROM NhaCungCap WHERE TenNhaCungCap LIKE ?",
      "%" + name + "%"
    );
  }

  //tìm theo địa chỉ
  public ArrayList<NhaCungCapDTO> searchByAddress(String diaChi) {
    return executeQuery(
      "SELECT * FROM NhaCungCap WHERE DiaChi LIKE ?",
      "%" + diaChi + "%"
    );
  }

  //tìm theo sdt
  public ArrayList<NhaCungCapDTO> searchByPhone(String sdt) {
    return executeQuery(
      "SELECT * FROM NhaCungCap WHERE SDT LIKE ?",
      "%" + sdt + "%"
    );
  }

  public ArrayList<NhaCungCapDTO> getByActive(boolean active) {
    return executeQuery("SELECT * FROM NhaCungCap WHERE Active = ?", active);
  }

  public boolean updateTrangThaiHopTac(String maNhaCungCap, boolean active) {
    return executeUpdate(
      "UPDATE NhaCungCap SET Active = ? WHERE MaNhaCungCap = ?",
      active,
      maNhaCungCap
    );
  }
}
