package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import phongkham.DTO.NhaCungCapDTO;
import phongkham.db.DBConnection;

public class NhaCungCapDAO {

  // ===== LẤY TẤT CẢ =====
  public ArrayList<NhaCungCapDTO> getAllNhaCungCap() {
    ArrayList<NhaCungCapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
    ) {
      while (rs.next()) {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
        ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSDT(rs.getString("SDT"));
        ncc.setActive(rs.getBoolean("Active"));
        list.add(ncc);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi lấy danh sách nhà cung cấp");
      e.printStackTrace();
    }
    return list;
  }

  // ===== THÊM =====
  public boolean insertNhaCungCap(NhaCungCapDTO ncc) {
    String sql = "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SDT, Active) VALUES (?,?,?,?,?)";

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
      System.out.println("Lỗi thêm nhà cung cấp");
      e.printStackTrace();
    }
    return false;
  }

  // ===== CẬP NHẬT =====
  public boolean updateNhaCungCap(NhaCungCapDTO ncc) {
    String sql = "UPDATE NhaCungCap SET TenNhaCungCap=?, DiaChi=?, SDT=?, Active=? WHERE MaNhaCungCap=?";

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
      System.out.println("Lỗi cập nhật nhà cung cấp");
      e.printStackTrace();
    }
    return false;
  }

  // ===== XOÁ =====
  public boolean deleteNhaCungCap(String maNhaCungCap) {
    String sql = "DELETE FROM NhaCungCap WHERE MaNhaCungCap=?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maNhaCungCap);
      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi xoá nhà cung cấp");
      e.printStackTrace();
    }
    return false;
  }

  // ===== LẤY THEO MÃ =====
  public NhaCungCapDTO getById(String maNhaCungCap) {
    String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap=?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maNhaCungCap);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
        ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSDT(rs.getString("SDT"));
        ncc.setActive(rs.getBoolean("Active"));
        return ncc;
      }

    } catch (SQLException e) {
      System.out.println("Lỗi lấy nhà cung cấp theo mã");
      e.printStackTrace();
    }
    return null;
  }

  // ===== TÌM THEO TÊN =====
  public ArrayList<NhaCungCapDTO> searchByName(String name) {
    ArrayList<NhaCungCapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap WHERE TenNhaCungCap LIKE ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, "%" + name + "%");
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
        ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSDT(rs.getString("SDT"));
        ncc.setActive(rs.getBoolean("Active"));
        list.add(ncc);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi tìm nhà cung cấp theo tên");
      e.printStackTrace();
    }
    return list;
  }

  // ===== TÌM THEO ĐỊA CHỈ =====
  public ArrayList<NhaCungCapDTO> searchByAddress(String diaChi) {
    ArrayList<NhaCungCapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap WHERE DiaChi LIKE ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, "%" + diaChi + "%");
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
        ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSDT(rs.getString("SDT"));
        ncc.setActive(rs.getBoolean("Active"));
        list.add(ncc);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi tìm nhà cung cấp theo địa chỉ");
      e.printStackTrace();
    }
    return list;
  }

  // ===== TÌM THEO SĐT =====
  public ArrayList<NhaCungCapDTO> searchByPhone(String sdt) {
    ArrayList<NhaCungCapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap WHERE SDT LIKE ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, "%" + sdt + "%");
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
        ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSDT(rs.getString("SDT"));
        ncc.setActive(rs.getBoolean("Active"));
        list.add(ncc);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi tìm nhà cung cấp theo số điện thoại");
      e.printStackTrace();
    }
    return list;
  }

  // ===== LẤY THEO TRẠNG THÁI =====
  public ArrayList<NhaCungCapDTO> getByActive(boolean active) {
    ArrayList<NhaCungCapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM NhaCungCap WHERE Active = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setBoolean(1, active);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
        ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSDT(rs.getString("SDT"));
        ncc.setActive(rs.getBoolean("Active"));
        list.add(ncc);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi lấy nhà cung cấp theo trạng thái");
      e.printStackTrace();
    }
    return list;
  }

  // ===== CẬP NHẬT TRẠNG THÁI =====
  public boolean updateTrangThaiHopTac(String maNhaCungCap, boolean active) {
    String sql = "UPDATE NhaCungCap SET Active = ? WHERE MaNhaCungCap = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setBoolean(1, active);
      ps.setString(2, maNhaCungCap);

      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi cập nhật trạng thái hợp tác");
      e.printStackTrace();
    }
    return false;
  }
}