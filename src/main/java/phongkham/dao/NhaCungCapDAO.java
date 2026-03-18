package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.DTO.NhaCungCapDTO;
import phongkham.db.DBConnection;

public class NhaCungCapDAO {

  public ArrayList<NhaCungCapDTO> getAllNhaCungCap() {
    ArrayList<NhaCungCapDTO> ds = new ArrayList<>();

    String sql = "SELECT * FROM NhaCungCap";
    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
    ) {
      while (rs.next()) {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
        ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSDT(rs.getString("SDT"));
        ncc.setActive(rs.getBoolean("Active"));

        ds.add(ncc);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  //thêm mới
  public boolean insertNhaCungCap(NhaCungCapDTO ncc) {
    String sql =
      "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SDT, Active) VALUES (?,?,?,?,?)";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, ncc.getMaNhaCungCap());
      ps.setString(2, ncc.getTenNhaCungCap());
      ps.setString(3, ncc.getDiaChi());
      ps.setString(4, ncc.getSDT());
      ps.setBoolean(5, ncc.isActive());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Lỗi thêm nhà cung cấp mới: " + e.getMessage());
    }
    return false;
  }

  //cập nhật
  public boolean updateNhaCungCap(NhaCungCapDTO ncc) {
    String sql =
      "UPDATE NhaCungCap SET TenNhaCungCap=?, DiaChi=?, SDT=?, Active=? WHERE MaNhaCungCap=?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, ncc.getTenNhaCungCap());
      ps.setString(2, ncc.getDiaChi());
      ps.setString(3, ncc.getSDT());
      ps.setBoolean(4, ncc.isActive());
      ps.setString(5, ncc.getMaNhaCungCap());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Lỗi cập nhật nhà cung cấp: " + e.getMessage());
    }
    return false;
  }

  //xóa theo mã
  public boolean deleteNhaCungCap(String MaNhaCungCap) {
    String sql = "DELETE FROM NhaCungCap WHERE MaNhaCungCap=?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, MaNhaCungCap);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Lỗi xóa nhà cung cấp theo mã: " + e.getMessage());
    }
    return false;
  }

  //lấy theo mã
  public NhaCungCapDTO getById(String MaNhaCungCap) {
    String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap=?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, MaNhaCungCap);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          NhaCungCapDTO ncc = new NhaCungCapDTO();
          ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
          ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
          ncc.setDiaChi(rs.getString("DiaChi"));
          ncc.setSDT(rs.getString("SDT"));
          ncc.setActive(rs.getBoolean("Active"));
          return ncc;
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi tìm nhà cung cấp theo mã: " + e.getMessage());
    }
    return null;
  }

  //tìm theo tên
  public ArrayList<NhaCungCapDTO> searchByName(String name) {
    ArrayList<NhaCungCapDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap WHERE TenNhaCungCap LIKE ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, "%" + name + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          NhaCungCapDTO ncc = new NhaCungCapDTO();
          ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
          ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
          ncc.setDiaChi(rs.getString("DiaChi"));
          ncc.setSDT(rs.getString("SDT"));
          ncc.setActive(rs.getBoolean("Active"));
          ds.add(ncc);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi tìm nhà cung cấp theo tên: " + e.getMessage());
    }
    return ds;
  }

  //tìm theo địa chỉ
  public ArrayList<NhaCungCapDTO> searchByAddress(String diaChi) {
    ArrayList<NhaCungCapDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap WHERE DiaChi LIKE ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, "%" + diaChi + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          NhaCungCapDTO ncc = new NhaCungCapDTO();
          ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
          ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
          ncc.setDiaChi(rs.getString("DiaChi"));
          ncc.setSDT(rs.getString("SDT"));
          ncc.setActive(rs.getBoolean("Active"));
          ds.add(ncc);
        }
      }
    } catch (SQLException e) {
      System.err.println(
        "Lỗi tìm nhà cung cấp theo địa chỉ: " + e.getMessage()
      );
    }
    return ds;
  }

  //tìm theo sdt
  public ArrayList<NhaCungCapDTO> searchByPhone(String sdt) {
    ArrayList<NhaCungCapDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap WHERE SDT LIKE ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, "%" + sdt + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          NhaCungCapDTO ncc = new NhaCungCapDTO();
          ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
          ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
          ncc.setDiaChi(rs.getString("DiaChi"));
          ncc.setSDT(rs.getString("SDT"));
          ncc.setActive(rs.getBoolean("Active"));
          ds.add(ncc);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi tìm nhà cung cấp theo SDT: " + e.getMessage());
    }
    return ds;
  }

  public ArrayList<NhaCungCapDTO> getByActive(boolean active) {
    ArrayList<NhaCungCapDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap WHERE Active = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setBoolean(1, active);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          NhaCungCapDTO ncc = new NhaCungCapDTO();
          ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
          ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
          ncc.setDiaChi(rs.getString("DiaChi"));
          ncc.setSDT(rs.getString("SDT"));
          ncc.setActive(rs.getBoolean("Active"));
          ds.add(ncc);
        }
      }
    } catch (SQLException e) {
      System.err.println(
        "Lỗi lấy nhà cung cấp theo trạng thái hợp tác: " + e.getMessage()
      );
    }
    return ds;
  }

  public boolean updateTrangThaiHopTac(String maNhaCungCap, boolean active) {
    String sql = "UPDATE NhaCungCap SET Active = ? WHERE MaNhaCungCap = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setBoolean(1, active);
      ps.setString(2, maNhaCungCap);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println(
        "Lỗi cập nhật trạng thái hợp tác nhà cung cấp: " + e.getMessage()
      );
    }
    return false;
  }
}
