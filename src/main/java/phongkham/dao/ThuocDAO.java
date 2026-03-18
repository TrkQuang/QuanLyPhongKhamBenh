package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.DTO.ThuocDTO;
import phongkham.db.DBConnection;

public class ThuocDAO {

  // Tự động sinh mã thuốc
  public String generateMaThuoc() {
    String sql = "SELECT MaThuoc FROM Thuoc ORDER BY MaThuoc DESC LIMIT 1";
    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql)
    ) {
      if (rs.next()) {
        String lastMa = rs.getString("MaThuoc");
        // Mã có dạng DT01, DT02, DT03...
        if (lastMa != null && lastMa.startsWith("T")) {
          int num = Integer.parseInt(lastMa.substring(2));
          return String.format("T%02d", num + 1);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi sinh mã thuốc: " + e.getMessage());
    }
    // Nếu chưa có thuốc nào, bắt đầu từ DT10
    return "T10";
  }

  //lấy tất cả thuốc
  public ArrayList<ThuocDTO> getAllThuoc() {
    ArrayList<ThuocDTO> ds = new ArrayList<>();

    String sql = "SELECT * FROM Thuoc WHERE Active = 1";
    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
    ) {
      while (rs.next()) {
        ThuocDTO t = new ThuocDTO();
        t.setMaThuoc(rs.getString("MaThuoc"));
        t.setTenThuoc(rs.getString("TenThuoc"));
        t.setHoatChat(rs.getString("HoatChat"));
        t.setDonViTinh(rs.getString("DonViTinh"));
        t.setDonGiaBan(rs.getFloat("DonGiaBan"));
        t.setSoLuongTon(rs.getInt("SoLuongTon"));

        ds.add(t);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  //thêm thuốc mới
  public boolean insertThuoc(ThuocDTO t) {
    // Tự động sinh mã nếu chưa có
    if (t.getMaThuoc() == null || t.getMaThuoc().trim().isEmpty()) {
      t.setMaThuoc(generateMaThuoc());
    }

    String sql =
      "INSERT INTO Thuoc (MaThuoc, TenThuoc, HoatChat, DonViTinh, DonGiaBan, SoLuongTon, Active) VALUES (?,?,?,?,?,?,1)";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, t.getMaThuoc());
      ps.setString(2, t.getTenThuoc());
      ps.setString(3, t.getHoatChat());
      ps.setString(4, t.getDonViTinh());
      ps.setFloat(5, t.getDonGiaBan());
      ps.setInt(6, t.getSoLuongTon());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  //cập nhật thuốc
  public boolean updateThuoc(ThuocDTO t) {
    String sql =
      "UPDATE Thuoc SET TenThuoc=?, HoatChat=?, DonViTinh=?, DonGiaBan=?, SoLuongTon=? WHERE MaThuoc=?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, t.getTenThuoc());
      ps.setString(2, t.getHoatChat());
      ps.setString(3, t.getDonViTinh());
      ps.setFloat(4, t.getDonGiaBan());
      ps.setInt(5, t.getSoLuongTon());
      ps.setString(6, t.getMaThuoc());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  //xóa theo mã
  public boolean deleteThuoc(String MaThuoc) {
    String sql = "UPDATE Thuoc SET Active = 0 WHERE MaThuoc =?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, MaThuoc);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean updateSoLuong(String maThuoc, int soLuongThem) {
    String sql =
      "UPDATE Thuoc SET SoLuongTon = SoLuongTon + ? WHERE MaThuoc = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setInt(1, soLuongThem);
      ps.setString(2, maThuoc);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  //tìm theo mã
  public ThuocDTO searchById(String maThuoc) {
    String sql = "SELECT * FROM Thuoc WHERE MaThuoc=?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maThuoc);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          ThuocDTO t = new ThuocDTO();
          t.setMaThuoc(rs.getString("MaThuoc"));
          t.setTenThuoc(rs.getString("TenThuoc"));
          t.setHoatChat(rs.getString("HoatChat"));
          t.setDonViTinh(rs.getString("DonViTinh"));
          t.setDonGiaBan(rs.getFloat("DonGiaBan"));
          t.setSoLuongTon(rs.getInt("SoLuongTon"));
          return t;
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi tìm thuốc theo mã: " + e.getMessage());
    }
    return null;
  }

  //tìm theo tên thuốc
  public ArrayList<ThuocDTO> searchByTenThuoc(String TenThuoc) {
    ArrayList<ThuocDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM Thuoc WHERE TenThuoc LIKE ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, "%" + TenThuoc + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ThuocDTO t = new ThuocDTO();
          t.setMaThuoc(rs.getString("MaThuoc"));
          t.setTenThuoc(rs.getString("TenThuoc"));
          t.setHoatChat(rs.getString("HoatChat"));
          t.setDonViTinh(rs.getString("DonViTinh"));
          t.setDonGiaBan(rs.getFloat("DonGiaBan"));
          t.setSoLuongTon(rs.getInt("SoLuongTon"));
          ds.add(t);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi tìm thuốc theo tên thuốc" + e.getMessage());
    }
    return ds;
  }

  //tìm theo hoạt chất
  public ArrayList<ThuocDTO> searchByHoatChat(String hoatChat) {
    ArrayList<ThuocDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM Thuoc WHERE HoatChat LIKE ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, "%" + hoatChat + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ThuocDTO t = new ThuocDTO();
          t.setMaThuoc(rs.getString("MaThuoc"));
          t.setTenThuoc(rs.getString("TenThuoc"));
          t.setHoatChat(rs.getString("HoatChat"));
          t.setDonViTinh(rs.getString("DonViTinh"));
          t.setDonGiaBan(rs.getFloat("DonGiaBan"));
          t.setSoLuongTon(rs.getInt("SoLuongTon"));
          ds.add(t);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi tìm thuốc theo hoạt chất" + e.getMessage());
    }
    return ds;
  }

  // Trừ số lượng tồn kho (cho giao thuốc)
  public boolean truSoLuongTon(String maThuoc, int soLuongTru) {
    String sql =
      "UPDATE Thuoc SET SoLuongTon = SoLuongTon - ? WHERE MaThuoc = ? AND SoLuongTon >= ? AND Active = 1";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setInt(1, soLuongTru);
      ps.setString(2, maThuoc);
      ps.setInt(3, soLuongTru);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Lỗi trừ số lượng tồn: " + e.getMessage());
      return false;
    }
  }

  // Lấy thuốc còn tồn kho (SoLuongTon > 0)
  public ArrayList<ThuocDTO> getThuocConTon() {
    ArrayList<ThuocDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM Thuoc WHERE SoLuongTon > 0 AND Active = 1 ORDER BY TenThuoc";
    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql)
    ) {
      while (rs.next()) {
        ThuocDTO t = new ThuocDTO();
        t.setMaThuoc(rs.getString("MaThuoc"));
        t.setTenThuoc(rs.getString("TenThuoc"));
        t.setHoatChat(rs.getString("HoatChat"));
        t.setDonViTinh(rs.getString("DonViTinh"));
        t.setDonGiaBan(rs.getFloat("DonGiaBan"));
        t.setSoLuongTon(rs.getInt("SoLuongTon"));
        ds.add(t);
      }
    } catch (SQLException e) {
      System.err.println("Lỗi lấy thuốc còn tồn: " + e.getMessage());
    }
    return ds;
  }

  //tìm theo đơn giá bán
  public ArrayList<ThuocDTO> searchByGiaBan(Float donGiaBan) {
    ArrayList<ThuocDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM Thuoc WHERE DonGiaBan = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setFloat(1, donGiaBan);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ThuocDTO t = new ThuocDTO();
          t.setMaThuoc(rs.getString("MaThuoc"));
          t.setTenThuoc(rs.getString("TenThuoc"));
          t.setHoatChat(rs.getString("HoatChat"));
          t.setDonViTinh(rs.getString("DonViTinh"));
          t.setDonGiaBan(rs.getFloat("DonGiaBan"));
          t.setSoLuongTon(rs.getInt("SoLuongTon"));
          ds.add(t);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi tìm thuốc theo đơn giá bán" + e.getMessage());
    }
    return ds;
  }

  public int getSoLuongTon(String maThuoc) {
    String sql = "SELECT * FROM Thuoc WHERE MaThuoc = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, maThuoc);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        ThuocDTO t = new ThuocDTO();
        t.setMaThuoc(rs.getString("MaThuoc"));
        t.setTenThuoc(rs.getString("TenThuoc"));
        t.setHoatChat(rs.getString("HoatChat"));
        t.setDonViTinh(rs.getString("DonViTinh"));
        t.setDonGiaBan(rs.getFloat("DonGiaBan"));
        t.setSoLuongTon(rs.getInt("SoLuongTon"));
        return t.getSoLuongTon();
      }
    } catch (SQLException e) {
      System.out.println("ERROR GetSoLuongTon" + e.getMessage());
    }
    return 0;
  }
}
