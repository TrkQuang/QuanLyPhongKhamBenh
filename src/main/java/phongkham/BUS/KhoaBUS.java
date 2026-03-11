package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.KhoaDTO;
import phongkham.dao.KhoaDAO;

public class KhoaBUS {

  private KhoaDAO dao = new KhoaDAO();

  // Lấy danh sách tất cả khoa
  public ArrayList<KhoaDTO> getAll() {
    return dao.getAll();
  }

  // Thêm khoa mới
  public boolean add(KhoaDTO khoa) {
    if (khoa == null || khoa.getTenKhoa().trim().isEmpty()) return false;
    if (khoa.getMaKhoa().trim().isEmpty()) return false;
    if (getById(khoa.getMaKhoa()) != null) return false;
    return dao.insertKhoa(khoa);
  }

  // Cập nhật thông tin khoa
  public boolean update(KhoaDTO khoa) {
    if (khoa == null || khoa.getTenKhoa().trim().isEmpty()) return false;
    if (khoa.getMaKhoa().trim().isEmpty()) return false;
    if (getById(khoa.getMaKhoa()) == null) return false;
    return dao.updateKhoa(khoa);
  }

  // Xoá khoa theo mã
  public boolean delete(String maKhoa) {
    if (maKhoa == null || maKhoa.trim().isEmpty()) return false;
    if (getById(maKhoa) == null) return false;
    return dao.deleteKhoa(maKhoa);
  }

  // Tìm khoa theo mã
  public KhoaDTO getById(String maKhoa) {
    ArrayList<KhoaDTO> list = dao.getAll();
    for (KhoaDTO khoa : list) {
      if (khoa.getMaKhoa().equals(maKhoa)) {
        return khoa;
      }
    }
    return null;
  }

  // Tìm khoa theo tên (chứa từ khoá)
  public ArrayList<KhoaDTO> searchByName(String keyword) {
    ArrayList<KhoaDTO> result = new ArrayList<>();
    ArrayList<KhoaDTO> list = dao.getAll();
    String search = keyword.toLowerCase();

    for (KhoaDTO khoa : list) {
      if (khoa.getTenKhoa().toLowerCase().contains(search)) {
        result.add(khoa);
      }
    }
    return result;
  }

  // Đếm tổng số khoa
  public int countAll() {
    return dao.getAll().size();
  }

  // Kiểm tra khoa có tồn tại không
  public boolean exists(String maKhoa) {
    return getById(maKhoa) != null;
  }
}
