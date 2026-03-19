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

  private ThuocDTO mapResultSetToThuoc(ResultSet rs) throws SQLException {
    ThuocDTO t = new ThuocDTO();
    t.setMaThuoc(rs.getString("MaThuoc"));
    t.setTenThuoc(rs.getString("TenThuoc"));
    t.setHoatChat(rs.getString("HoatChat"));
    t.setDonViTinh(rs.getString("DonViTinh"));
    t.setDonGiaBan(rs.getFloat("DonGiaBan"));
    t.setSoLuongTon(rs.getInt("SoLuongTon"));
    return t;
  }

  private ArrayList<ThuocDTO> executeListQuery(String sql, Object... params) {
    ArrayList<ThuocDTO> ds = new ArrayList<>();
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ds.add(mapResultSetToThuoc(rs));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  private ThuocDTO executeSingleQuery(String sql, Object... params) {
    ArrayList<ThuocDTO> ds = executeListQuery(sql, params);
    return ds.isEmpty() ? null : ds.get(0);
  }

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
    return executeListQuery("SELECT * FROM Thuoc WHERE Active = 1");
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
    return executeSingleQuery("SELECT * FROM Thuoc WHERE MaThuoc=?", maThuoc);
  }

  //tìm theo tên thuốc
  public ArrayList<ThuocDTO> searchByTenThuoc(String TenThuoc) {
    return executeListQuery(
      "SELECT * FROM Thuoc WHERE TenThuoc LIKE ?",
      "%" + TenThuoc + "%"
    );
  }

  //tìm theo hoạt chất
  public ArrayList<ThuocDTO> searchByHoatChat(String hoatChat) {
    return executeListQuery(
      "SELECT * FROM Thuoc WHERE HoatChat LIKE ?",
      "%" + hoatChat + "%"
    );
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
    return executeListQuery(
      "SELECT * FROM Thuoc WHERE SoLuongTon > 0 AND Active = 1 ORDER BY TenThuoc"
    );
  }

  //tìm theo đơn giá bán
  public ArrayList<ThuocDTO> searchByGiaBan(Float donGiaBan) {
    return executeListQuery(
      "SELECT * FROM Thuoc WHERE DonGiaBan = ?",
      donGiaBan
    );
  }

  public int getSoLuongTon(String maThuoc) {
    ThuocDTO thuoc = executeSingleQuery(
      "SELECT * FROM Thuoc WHERE MaThuoc = ?",
      maThuoc
    );
    return thuoc == null ? 0 : thuoc.getSoLuongTon();
  }
}
