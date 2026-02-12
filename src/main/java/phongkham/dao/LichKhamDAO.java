package phongkham.dao;

import java.sql.*;
import java.util.ArrayList;
import phongkham.DTO.LichKhamDTO;
import phongkham.db.DBConnection;

public class LichKhamDAO {

  // ================= LẤY TẤT CẢ LỊCH KHÁM =================
  public ArrayList<LichKhamDTO> getAll() {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM LichKham ORDER BY ThoiGianBatDau DESC";

    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql);
    ) {
      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  // ================= THÊM LỊCH KHÁM =================
  public boolean insert(LichKhamDTO lk) {
    String sql = "INSERT INTO LichKham VALUES(?,?,?,?,?,?,?)";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, lk.getMaLichKham());
      ps.setString(2, lk.getMaGoi());
      ps.setString(3, lk.getMaBacSi());
      ps.setString(4, lk.getThoiGianBatDau());
      ps.setString(5, lk.getThoiGianKetThuc());
      ps.setString(6, lk.getTrangThai());
      ps.setString(7, lk.getMaDinhDanhTam());

      return ps.executeUpdate() > 0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // ================= XÓA LỊCH KHÁM =================
  public boolean delete(String maLichKham) {
    String sql = "DELETE FROM LichKham WHERE MaLichKham=?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, maLichKham);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // ================= CẬP NHẬT LỊCH KHÁM =================
  public boolean update(LichKhamDTO lk) {
    String sql =
      "UPDATE LichKham SET MaGoi=?, MaBacSi=?, ThoiGianBatDau=?, ThoiGianKetThuc=?, TrangThai=?, MaDinhDanhTam=? WHERE MaLichKham=?";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, lk.getMaGoi());
      ps.setString(2, lk.getMaBacSi());
      ps.setString(3, lk.getThoiGianBatDau());
      ps.setString(4, lk.getThoiGianKetThuc());
      ps.setString(5, lk.getTrangThai());
      ps.setString(6, lk.getMaDinhDanhTam());
      ps.setString(7, lk.getMaLichKham());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // ================= CẬP NHẬT TRẠNG THÁI =================
  public boolean updateTrangThai(String maLichKham, String trangThai) {
    String sql = "UPDATE LichKham SET TrangThai=? WHERE MaLichKham=?";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, trangThai);
      ps.setString(2, maLichKham);

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // ================= TÌM THEO MÃ =================
  public LichKhamDTO getById(String maLichKham) {
    String sql = "SELECT * FROM LichKham WHERE MaLichKham=?";
    LichKhamDTO lk = null;

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, maLichKham);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return lk;
  }

  // ================= TÌM THEO BÁC SĨ =================
  public ArrayList<LichKhamDTO> getByMaBacSi(String maBacSi) {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM LichKham WHERE MaBacSi=? ORDER BY ThoiGianBatDau DESC";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, maBacSi);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  // ================= TÌM THEO GÓI DỊCH VỤ =================
  public ArrayList<LichKhamDTO> getByMaGoi(String maGoi) {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM LichKham WHERE MaGoi=? ORDER BY ThoiGianBatDau DESC";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, maGoi);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  // ================= TÌM THEO MÃ ĐỊNH DANH =================
  public ArrayList<LichKhamDTO> getByMaDinhDanhTam(String MaDinhDanhTam) {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM LichKham WHERE MaDinhDanhTam=? ORDER BY ThoiGianBatDau DESC";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, MaDinhDanhTam);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  // ================= TÌM THEO TRẠNG THÁI =================
  public ArrayList<LichKhamDTO> getByTrangThai(String trangThai) {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM LichKham WHERE TrangThai=? ORDER BY ThoiGianBatDau DESC";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, trangThai);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  // ================= TÌM THEO NGÀY =================
  public ArrayList<LichKhamDTO> getByNgay(String ngay) {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM LichKham WHERE DATE(ThoiGianBatDau)=? ORDER BY ThoiGianBatDau";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, ngay);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  // ================= TÌM THEO BÁC SĨ VÀ NGÀY =================
  public ArrayList<LichKhamDTO> getByBacSiAndNgay(String maBacSi, String ngay) {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM LichKham WHERE MaBacSi=? AND DATE(ThoiGianBatDau)=? ORDER BY ThoiGianBatDau";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, maBacSi);
      ps.setString(2, ngay);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  // ================= KIỂM TRA TRÙNG LỊCH BÁC SĨ =================
  public boolean checkTrungLich(
    String maBacSi,
    String thoiGianBatDau,
    String thoiGianKetThuc
  ) {
    String sql =
      "SELECT COUNT(*) as count FROM LichKham WHERE MaBacSi=? AND TrangThai != 'Đã hủy' " +
      "AND ((ThoiGianBatDau <= ? AND ThoiGianKetThuc > ?) " +
      "OR (ThoiGianBatDau < ? AND ThoiGianKetThuc >= ?) " +
      "OR (ThoiGianBatDau >= ? AND ThoiGianKetThuc <= ?))";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, maBacSi);
      ps.setString(2, thoiGianBatDau);
      ps.setString(3, thoiGianBatDau);
      ps.setString(4, thoiGianKetThuc);
      ps.setString(5, thoiGianKetThuc);
      ps.setString(6, thoiGianBatDau);
      ps.setString(7, thoiGianKetThuc);

      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        int count = rs.getInt("count");
        rs.close();
        return count > 0;
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // ================= KIỂM TRA TRÙNG LỊCH KHI CẬP NHẬT =================
  public boolean checkTrungLichWhenUpdate(
    String maLichKham,
    String maBacSi,
    String thoiGianBatDau,
    String thoiGianKetThuc
  ) {
    String sql =
      "SELECT COUNT(*) as count FROM LichKham WHERE MaBacSi=? AND MaLichKham != ? AND TrangThai != 'Đã hủy' " +
      "AND ((ThoiGianBatDau <= ? AND ThoiGianKetThuc > ?) " +
      "OR (ThoiGianBatDau < ? AND ThoiGianKetThuc >= ?) " +
      "OR (ThoiGianBatDau >= ? AND ThoiGianKetThuc <= ?))";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, maBacSi);
      ps.setString(2, maLichKham);
      ps.setString(3, thoiGianBatDau);
      ps.setString(4, thoiGianBatDau);
      ps.setString(5, thoiGianKetThuc);
      ps.setString(6, thoiGianKetThuc);
      ps.setString(7, thoiGianBatDau);
      ps.setString(8, thoiGianKetThuc);

      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        int count = rs.getInt("count");
        rs.close();
        return count > 0;
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // ================= TÌM THEO KHOẢNG THỜI GIAN =================
  public ArrayList<LichKhamDTO> getByKhoangThoiGian(
    String tuNgay,
    String denNgay
  ) {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM LichKham WHERE DATE(ThoiGianBatDau) BETWEEN ? AND ? ORDER BY ThoiGianBatDau";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, tuNgay);
      ps.setString(2, denNgay);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  // ================= ĐẾM LỊCH KHÁM THEO TRẠNG THÁI =================
  public int countByTrangThai(String trangThai) {
    String sql = "SELECT COUNT(*) as count FROM LichKham WHERE TrangThai=?";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, trangThai);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        int count = rs.getInt("count");
        rs.close();
        return count;
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  // ================= TÌM KIẾM THEO NHIỀU TIÊU CHÍ =================
  public ArrayList<LichKhamDTO> search(String keyword) {
    ArrayList<LichKhamDTO> ds = new ArrayList<>();
    String sql =
      "SELECT * FROM LichKham WHERE MaLichKham LIKE ? OR MaBacSi LIKE ? OR MaGoi LIKE ? OR MaDinhDanhTam LIKE ? OR TrangThai LIKE ? ORDER BY ThoiGianBatDau DESC";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      String searchPattern = "%" + keyword + "%";
      ps.setString(1, searchPattern);
      ps.setString(2, searchPattern);
      ps.setString(3, searchPattern);
      ps.setString(4, searchPattern);
      ps.setString(5, searchPattern);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        LichKhamDTO lk = new LichKhamDTO();
        lk.setMaLichKham(rs.getString("MaLichKham"));
        lk.setMaGoi(rs.getString("MaGoi"));
        lk.setMaBacSi(rs.getString("MaBacSi"));
        lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
        lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
        lk.setTrangThai(rs.getString("TrangThai"));
        lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
        ds.add(lk);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }
}
