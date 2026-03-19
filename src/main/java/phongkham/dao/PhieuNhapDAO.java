package phongkham.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.db.DBConnection;

public class PhieuNhapDAO {

  // ===== LẤY TẤT CẢ =====
  public ArrayList<PhieuNhapDTO> getAll() {
    ArrayList<PhieuNhapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM PhieuNhap ORDER BY NgayNhap DESC";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
    ) {
      while (rs.next()) {
        PhieuNhapDTO pn = new PhieuNhapDTO();
        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
        pn.setMaNCC(rs.getString("MaNCC"));
        pn.setNgayNhap(rs.getDate("NgayNhap"));
        pn.setNguoiGiao(rs.getString("NguoiGiao"));
        pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
        pn.setTrangThai(rs.getString("TrangThai"));
        list.add(pn);
      }
    } catch (SQLException e) {
      System.out.println("Lỗi lấy danh sách phiếu nhập");
      e.printStackTrace();
    }
    return list;
  }

  // ===== LẤY THEO ID =====
  public PhieuNhapDTO getById(String maPhieuNhap) {
    String sql = "SELECT * FROM PhieuNhap WHERE MaPhieuNhap = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maPhieuNhap);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        PhieuNhapDTO pn = new PhieuNhapDTO();
        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
        pn.setMaNCC(rs.getString("MaNCC"));
        pn.setNgayNhap(rs.getDate("NgayNhap"));
        pn.setNguoiGiao(rs.getString("NguoiGiao"));
        pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
        pn.setTrangThai(rs.getString("TrangThai"));
        return pn;
      }

    } catch (SQLException e) {
      System.out.println("Lỗi lấy phiếu nhập theo mã");
      e.printStackTrace();
    }
    return null;
  }

  // ===== THÊM =====
  public boolean insert(PhieuNhapDTO pn) {
    String sql = "INSERT INTO PhieuNhap (MaPhieuNhap, MaNCC, NgayNhap, NguoiGiao, TongTienNhap, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";

    String trangThai = StatusNormalizer.normalizePhieuNhapStatus(
      pn.getTrangThai() != null ? pn.getTrangThai() : StatusNormalizer.CHO_DUYET
    );

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, pn.getMaPhieuNhap());
      ps.setString(2, pn.getMaNCC());
      ps.setDate(3, pn.getNgayNhap());
      ps.setString(4, pn.getNguoiGiao());
      ps.setFloat(5, pn.getTongTienNhap());
      ps.setString(6, trangThai);

      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi thêm phiếu nhập");
      e.printStackTrace();
    }
    return false;
  }

  // ===== CẬP NHẬT =====
  public boolean update(PhieuNhapDTO pn) {
    String sql = "UPDATE PhieuNhap SET MaNCC=?, NgayNhap=?, NguoiGiao=?, TongTienNhap=?, TrangThai=? WHERE MaPhieuNhap=?";

    String trangThai = StatusNormalizer.normalizePhieuNhapStatus(pn.getTrangThai());

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, pn.getMaNCC());
      ps.setDate(2, pn.getNgayNhap());
      ps.setString(3, pn.getNguoiGiao());
      ps.setFloat(4, pn.getTongTienNhap());
      ps.setString(5, trangThai);
      ps.setString(6, pn.getMaPhieuNhap());

      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi cập nhật phiếu nhập");
      e.printStackTrace();
    }
    return false;
  }

  // ===== XOÁ =====
  public boolean delete(String maPhieuNhap) {
    String sql = "DELETE FROM PhieuNhap WHERE MaPhieuNhap=?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maPhieuNhap);
      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi xoá phiếu nhập");
      e.printStackTrace();
    }
    return false;
  }

  // ===== CẬP NHẬT TRẠNG THÁI =====
  public boolean capNhatTrangThai(String maPN, String trangThaiMoi) {
    String sql = "UPDATE PhieuNhap SET TrangThai = ? WHERE MaPhieuNhap = ?";

    String trangThai = StatusNormalizer.normalizePhieuNhapStatus(trangThaiMoi);

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, trangThai);
      ps.setString(2, maPN);

      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi cập nhật trạng thái");
      e.printStackTrace();
    }
    return false;
  }

  // ===== TÌM THEO NHÀ CUNG CẤP =====
  public ArrayList<PhieuNhapDTO> getByMaNCC(String maNCC) {
    ArrayList<PhieuNhapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM PhieuNhap WHERE MaNCC = ? ORDER BY NgayNhap DESC";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maNCC);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        PhieuNhapDTO pn = new PhieuNhapDTO();
        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
        pn.setMaNCC(rs.getString("MaNCC"));
        pn.setNgayNhap(rs.getDate("NgayNhap"));
        pn.setNguoiGiao(rs.getString("NguoiGiao"));
        pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
        pn.setTrangThai(rs.getString("TrangThai"));
        list.add(pn);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi tìm theo nhà cung cấp");
      e.printStackTrace();
    }
    return list;
  }

  // ===== TÌM KIẾM =====
  public ArrayList<PhieuNhapDTO> search(String keyword) {
    ArrayList<PhieuNhapDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM PhieuNhap WHERE MaPhieuNhap LIKE ? OR NguoiGiao LIKE ? ORDER BY NgayNhap DESC";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, "%" + keyword + "%");
      ps.setString(2, "%" + keyword + "%");

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        PhieuNhapDTO pn = new PhieuNhapDTO();
        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
        pn.setMaNCC(rs.getString("MaNCC"));
        pn.setNgayNhap(rs.getDate("NgayNhap"));
        pn.setNguoiGiao(rs.getString("NguoiGiao"));
        pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
        pn.setTrangThai(rs.getString("TrangThai"));
        list.add(pn);
      }

    } catch (SQLException e) {
      System.out.println("Lỗi tìm kiếm phiếu nhập");
      e.printStackTrace();
    }
    return list;
  }

  // ===== CẬP NHẬT TỔNG TIỀN =====
  public boolean updateTongTien(String maPN, java.math.BigDecimal tongTien) {
    String sql = "UPDATE PhieuNhap SET TongTienNhap = ? WHERE MaPhieuNhap = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setBigDecimal(1, tongTien);
      ps.setString(2, maPN);

      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      System.out.println("Lỗi cập nhật tổng tiền");
      e.printStackTrace();
    }
    return false;
  }
// ===== LẤY THEO KHOẢNG NGÀY =====
public ArrayList<PhieuNhapDTO> getByDate(LocalDateTime startDate, LocalDateTime endDate) {
  ArrayList<PhieuNhapDTO> list = new ArrayList<>();
  String sql = "SELECT * FROM PhieuNhap WHERE NgayNhap BETWEEN ? AND ? ORDER BY NgayNhap DESC";

  try (
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
  ) {
    ps.setTimestamp(1, Timestamp.valueOf(startDate));
    ps.setTimestamp(2, Timestamp.valueOf(endDate));

    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
      PhieuNhapDTO pn = new PhieuNhapDTO();
      pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
      pn.setMaNCC(rs.getString("MaNCC"));
      pn.setNgayNhap(rs.getDate("NgayNhap"));
      pn.setNguoiGiao(rs.getString("NguoiGiao"));
      pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
      pn.setTrangThai(rs.getString("TrangThai"));
      list.add(pn);
    }

  } catch (SQLException e) {
    System.out.println("Lỗi tìm phiếu nhập theo ngày");
    e.printStackTrace();
  }
  return list;
}

// ===== LẤY THEO NGƯỜI GIAO =====
public ArrayList<PhieuNhapDTO> getByNguoiGiao(String nguoiGiao) {
  ArrayList<PhieuNhapDTO> list = new ArrayList<>();
  String sql = "SELECT * FROM PhieuNhap WHERE NguoiGiao LIKE ? ORDER BY NgayNhap DESC";

  try (
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
  ) {
    ps.setString(1, "%" + nguoiGiao + "%");

    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
      PhieuNhapDTO pn = new PhieuNhapDTO();
      pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
      pn.setMaNCC(rs.getString("MaNCC"));
      pn.setNgayNhap(rs.getDate("NgayNhap"));
      pn.setNguoiGiao(rs.getString("NguoiGiao"));
      pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
      pn.setTrangThai(rs.getString("TrangThai"));
      list.add(pn);
    }

  } catch (SQLException e) {
    System.out.println("Lỗi tìm phiếu nhập theo người giao");
    e.printStackTrace();
  }
  return list;
}

// ===== LẤY THEO TRẠNG THÁI =====
public ArrayList<PhieuNhapDTO> getByTrangThai(String trangThai) {
  ArrayList<PhieuNhapDTO> list = new ArrayList<>();
  String sql = "SELECT * FROM PhieuNhap WHERE TrangThai = ? ORDER BY NgayNhap DESC";

  String trangThaiChuan = StatusNormalizer.normalizePhieuNhapStatus(trangThai);

  try (
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
  ) {
    ps.setString(1, trangThaiChuan);

    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
      PhieuNhapDTO pn = new PhieuNhapDTO();
      pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
      pn.setMaNCC(rs.getString("MaNCC"));
      pn.setNgayNhap(rs.getDate("NgayNhap"));
      pn.setNguoiGiao(rs.getString("NguoiGiao"));
      pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
      pn.setTrangThai(rs.getString("TrangThai"));
      list.add(pn);
    }

  } catch (SQLException e) {
    System.out.println("Lỗi tìm phiếu nhập theo trạng thái");
    e.printStackTrace();
  }
  return list;
}

// ===== XOÁ THEO NHÀ CUNG CẤP =====
public boolean deleteByNCC(String maNCC) {
  String sql = "DELETE FROM PhieuNhap WHERE MaNCC = ?";

  try (
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
  ) {
    ps.setString(1, maNCC);
    return ps.executeUpdate() > 0;

  } catch (SQLException e) {
    System.out.println("Lỗi xoá phiếu nhập theo nhà cung cấp");
    e.printStackTrace();
  }
  return false;
}

// ===== KIỂM TRA CÓ PHIẾU NHẬP KHÔNG =====
public boolean hasPhieuNhap(String maNCC) {
  String sql = "SELECT COUNT(*) FROM PhieuNhap WHERE MaNCC = ?";

  try (
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
  ) {
    ps.setString(1, maNCC);

    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      return rs.getInt(1) > 0;
    }

  } catch (SQLException e) {
    System.out.println("Lỗi kiểm tra phiếu nhập");
    e.printStackTrace();
  }
  return false;
}

// ===== TỔNG TIỀN THEO TRẠNG THÁI =====
public double getTongTienByTrangThai(String trangThai) {
  String sql = "SELECT COALESCE(SUM(TongTienNhap), 0) FROM PhieuNhap WHERE TrangThai = ?";

  String trangThaiChuan = StatusNormalizer.normalizePhieuNhapStatus(trangThai);

  try (
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
  ) {
    ps.setString(1, trangThaiChuan);

    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      return rs.getDouble(1);
    }

  } catch (SQLException e) {
    System.out.println("Lỗi tính tổng tiền theo trạng thái");
    e.printStackTrace();
  }
  return 0;
}
}