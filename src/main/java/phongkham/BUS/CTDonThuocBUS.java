package phongkham.BUS;

import java.util.ArrayList;
import phongkham.dao.CTDonThuocDAO;
import phongkham.DTO.CTDonThuocDTO;

public class CTDonThuocBUS {
  private CTDonThuocDAO dao;

    public CTDonThuocBUS() {
        dao = new CTDonThuocDAO();
    }

    // Lấy toàn bộ danh sách
    public ArrayList<CTDonThuocDTO> getAll() {
        return dao.getAll();
    }

    // Thêm chi tiết đơn thuốc
    public boolean insert(CTDonThuocDTO ct) {

        if (ct.gettMaCTDonThuoc().trim().isEmpty() ||
            ct.getMaDonThuoc().trim().isEmpty() ||
            ct.getMaThuoc().trim().isEmpty()) {

            System.out.println("Không được để trống dữ liệu!");
            return false;
        }

        if (ct.getSoluong() <= 0) {
            System.out.println("Số lượng phải lớn hơn 0!");
            return false;
        }

        if (dao.existsMaCTDonThuoc(ct.gettMaCTDonThuoc())) {
            System.out.println("Mã chi tiết đã tồn tại!");
            return false;
        }

        return dao.insertCTDonThuoc(ct);
    }

    // Cập nhật
    public boolean update(CTDonThuocDTO ct) {

        if (!dao.existsMaCTDonThuoc(ct.gettMaCTDonThuoc())) {
            System.out.println("Không tìm thấy mã chi tiết để cập nhật!");
            return false;
        }

        if (ct.getSoluong() <= 0) {
            System.out.println("Số lượng phải lớn hơn 0!");
            return false;
        }

        return dao.updateCTDonThuoc(ct);
    }

    // Xóa
    public boolean delete(String maCT) {

        if (!dao.existsMaCTDonThuoc(maCT)) {
            System.out.println("Mã chi tiết không tồn tại!");
            return false;
        }

        return dao.deleteMaCTDonThuoc(maCT);
    }

    // Lọc theo mã đơn thuốc
    public ArrayList<CTDonThuocDTO> getByMaDonThuoc(String maDonThuoc) {
        return dao.getByMaDonThuoc(maDonThuoc);
    }

    // Tìm kiếm theo mã thuốc
    public ArrayList<CTDonThuocDTO> searchByMaThuoc(String maThuoc) {

        ArrayList<CTDonThuocDTO> result = new ArrayList<>();

        for (CTDonThuocDTO ct : dao.getAll()) {
            if (ct.getMaThuoc().toLowerCase()
                  .contains(maThuoc.toLowerCase())) {
                result.add(ct);
            }
        }

        return result;
    }
}
