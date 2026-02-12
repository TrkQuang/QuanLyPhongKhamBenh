package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.LichKhamDTO;
import phongkham.dao.LichKhamDAO;

public class LichKhamBUS {

  private LichKhamDAO lichKhamDAO = new LichKhamDAO();

  // ================= LẤY TẤT CẢ =================
  public ArrayList<LichKhamDTO> getAll() {
    return lichKhamDAO.getAll();
  }

  // ================= THÊM =================
  public String insert(LichKhamDTO lk) {
    if (
      lk.getMaLichKham().trim().isEmpty() ||
      lk.getMaBacSi().trim().isEmpty() ||
      lk.getThoiGianBatDau().trim().isEmpty()
    ) {
      return "Không được để trống dữ liệu";
    }

    if (lichKhamDAO.getById(lk.getMaLichKham()) != null) {
      return "Mã lịch khám đã tồn tại";
    }

    if (lk.getThoiGianBatDau().compareTo(lk.getThoiGianKetThuc()) >= 0) {
      return "Thời gian kết thúc phải lớn hơn thời gian bắt đầu";
    }

    boolean result = lichKhamDAO.insert(lk);

    if (result) return "Thêm thành công";

    return "Thêm thất bại";
  }

  // ================= XÓA =================
  public String delete(String maLichKham) {
    if (lichKhamDAO.getById(maLichKham) == null) {
      return "Lịch khám không tồn tại";
    }

    boolean result = lichKhamDAO.delete(maLichKham);

    if (result) return "Xóa thành công";

    return "Xóa thất bại";
  }

  // ================= TÌM =================
  public LichKhamDTO getById(String ma) {
    return lichKhamDAO.getById(ma);
  }
}
