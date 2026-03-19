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

  // ===== TỰ ĐỘNG SINH MÃ =====
  public String generateMaThuoc() {
    String sql = "SELECT MaThuoc FROM Thuoc ORDER BY MaThuoc DESC LIMIT 1";

    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
    ) {
      if (rs.next()) {
        String lastMa = rs.getString("MaThuoc");

        if (lastMa != null && lastMa.startsWith("T")) {
          int num = Integer.parseInt(lastMa.substring(1));
          return String.format("T%02d", num + 1);
        }
      }

    } catch (SQLException e) {
      System.out.println("Lỗi sinh mã thuốc");
      e.printStackTrace();
    }

    return "T01";
  }

  // ===== LẤY TẤT CẢ =====
  public ArrayList<ThuocDTO> getAllThuoc() {
    ArrayList<ThuocDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM Thuoc WHERE Active = 1";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
    ) {
      while (rs.next()) {
        ThuocDTO t = new ThuocDTO();
        t.setMaThuoc(rs.getString("MaThuoc"));
        t.setTenThuoc(rs.getString("TenThuoc"));
        t.setHoatChat(rs.getString("HoatChat"));
        t.setDonViTinh(rs.getString("DonViTinh"));
        t.setDonGiaBan(rs.getFloat("DonGiaBan"));
        t.setSoLuongTon(rs.getInt("SoLuongTon"));
        list.add(t);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi lấy danh sách thuốc");
      e.printStackTrace();
    }
    return list;
  }

  // ===== THÊM =====
  public boolean insertThuoc(ThuocDTO t) {
    if (t.getMaThuoc() == null || t.getMaThuoc().trim().isEmpty()) {
      t.setMaThuoc(generateMaThuoc());
    }

    String sql = "INSERT INTO Thuoc (MaThuoc, TenThuoc, HoatChat, DonViTinh, DonGiaBan, SoLuongTon, Active) VALUES (?,?,?,?,?,?,1)";

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
      System.out.println("Lỗi thêm thuốc");
      e.printStackTrace();
    }
    return false;
  }

  // ===== CẬP NHẬT =====
  public boolean updateThuoc(ThuocDTO t) {
    String sql = "UPDATE Thuoc SET TenThuoc=?, HoatChat=?, DonViTinh=?, DonGiaBan=?, SoLuongTon=? WHERE MaThuoc=?";

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
      System.out.println("Lỗi cập nhật thuốc");
      e.printStackTrace();
    }
    return false;
  }

  // ===== XOÁ MỀM =====
  public boolean deleteThuoc(String maThuoc) {
    String sql = "UPDATE Thuoc SET Active = 0 WHERE MaThuoc = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maThuoc);
      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi xoá thuốc");
      e.printStackTrace();
    }
    return false;
  }

  // ===== CỘNG SỐ LƯỢNG =====
  public boolean updateSoLuong(String maThuoc, int soLuongThem) {
    String sql = "UPDATE Thuoc SET SoLuongTon = SoLuongTon + ? WHERE MaThuoc = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setInt(1, soLuongThem);
      ps.setString(2, maThuoc);
      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi cập nhật số lượng");
      e.printStackTrace();
    }
    return false;
  }

  // ===== TÌM THEO MÃ =====
  public ThuocDTO searchById(String maThuoc) {
    String sql = "SELECT * FROM Thuoc WHERE MaThuoc=?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
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
        return t;
      }

    } catch (SQLException e) {
      System.out.println("Lỗi tìm thuốc theo mã");
      e.printStackTrace();
    }
    return null;
  }

  // ===== TÌM THEO TÊN =====
  public ArrayList<ThuocDTO> searchByTenThuoc(String tenThuoc) {
    ArrayList<ThuocDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM Thuoc WHERE TenThuoc LIKE ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, "%" + tenThuoc + "%");
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        ThuocDTO t = new ThuocDTO();
        t.setMaThuoc(rs.getString("MaThuoc"));
        t.setTenThuoc(rs.getString("TenThuoc"));
        t.setHoatChat(rs.getString("HoatChat"));
        t.setDonViTinh(rs.getString("DonViTinh"));
        t.setDonGiaBan(rs.getFloat("DonGiaBan"));
        t.setSoLuongTon(rs.getInt("SoLuongTon"));
        list.add(t);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi tìm thuốc theo tên");
      e.printStackTrace();
    }
    return list;
  }

  // ===== TRỪ SỐ LƯỢNG =====
  public boolean truSoLuongTon(String maThuoc, int soLuongTru) {
    String sql = "UPDATE Thuoc SET SoLuongTon = SoLuongTon - ? WHERE MaThuoc = ? AND SoLuongTon >= ? AND Active = 1";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setInt(1, soLuongTru);
      ps.setString(2, maThuoc);
      ps.setInt(3, soLuongTru);

      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi trừ số lượng tồn");
      e.printStackTrace();
    }
    return false;
  }

  // ===== LẤY THUỐC CÒN TỒN =====
  public ArrayList<ThuocDTO> getThuocConTon() {
    ArrayList<ThuocDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM Thuoc WHERE SoLuongTon > 0 AND Active = 1 ORDER BY TenThuoc";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
    ) {
      while (rs.next()) {
        ThuocDTO t = new ThuocDTO();
        t.setMaThuoc(rs.getString("MaThuoc"));
        t.setTenThuoc(rs.getString("TenThuoc"));
        t.setHoatChat(rs.getString("HoatChat"));
        t.setDonViTinh(rs.getString("DonViTinh"));
        t.setDonGiaBan(rs.getFloat("DonGiaBan"));
        t.setSoLuongTon(rs.getInt("SoLuongTon"));
        list.add(t);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi lấy thuốc còn tồn");
      e.printStackTrace();
    }
    return list;
  }

  // ===== LẤY SỐ LƯỢNG TỒN =====
  public int getSoLuongTon(String maThuoc) {
    String sql = "SELECT SoLuongTon FROM Thuoc WHERE MaThuoc = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maThuoc);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        return rs.getInt("SoLuongTon");
      }

    } catch (SQLException e) {
      System.out.println("Lỗi lấy số lượng tồn");
      e.printStackTrace();
    }
    return 0;
  }

  // ===== TÌM THEO HOẠT CHẤT =====
public ArrayList<ThuocDTO> searchByHoatChat(String hoatChat) {
  ArrayList<ThuocDTO> list = new ArrayList<>();
  String sql = "SELECT * FROM Thuoc WHERE HoatChat LIKE ? AND Active = 1";

  try (
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
  ) {
    ps.setString(1, "%" + hoatChat + "%");
    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
      ThuocDTO t = new ThuocDTO();
      t.setMaThuoc(rs.getString("MaThuoc"));
      t.setTenThuoc(rs.getString("TenThuoc"));
      t.setHoatChat(rs.getString("HoatChat"));
      t.setDonViTinh(rs.getString("DonViTinh"));
      t.setDonGiaBan(rs.getFloat("DonGiaBan"));
      t.setSoLuongTon(rs.getInt("SoLuongTon"));
      list.add(t);
    }

  } catch (SQLException e) {
    System.out.println("Lỗi tìm thuốc theo hoạt chất");
    e.printStackTrace();
  }
  return list;
}

// ===== TÌM THEO GIÁ BÁN =====
public ArrayList<ThuocDTO> searchByGiaBan(Float donGiaBan) {
  ArrayList<ThuocDTO> list = new ArrayList<>();
  String sql = "SELECT * FROM Thuoc WHERE DonGiaBan = ? AND Active = 1";

  try (
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
  ) {
    ps.setFloat(1, donGiaBan);
    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
      ThuocDTO t = new ThuocDTO();
      t.setMaThuoc(rs.getString("MaThuoc"));
      t.setTenThuoc(rs.getString("TenThuoc"));
      t.setHoatChat(rs.getString("HoatChat"));
      t.setDonViTinh(rs.getString("DonViTinh"));
      t.setDonGiaBan(rs.getFloat("DonGiaBan"));
      t.setSoLuongTon(rs.getInt("SoLuongTon"));
      list.add(t);
    }

  } catch (SQLException e) {
    System.out.println("Lỗi tìm thuốc theo giá bán");
    e.printStackTrace();
  }
  return list;
}
}