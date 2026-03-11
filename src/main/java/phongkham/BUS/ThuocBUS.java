package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.ThuocDTO;
import phongkham.dao.ThuocDAO;

public class ThuocBUS {

  private ThuocDAO thuocDAO = new ThuocDAO();

  // Validate các trường bắt buộc của thuốc
  private String validate(ThuocDTO t) {
    if (t == null) return "Thuốc không được để trống";
    if (
      t.getTenThuoc() == null || t.getTenThuoc().trim().isEmpty()
    ) return "Tên thuốc không được để trống";
    if (
      t.getHoatChat() == null || t.getHoatChat().trim().isEmpty()
    ) return "Hoạt chất không được để trống";
    if (
      t.getDonViTinh() == null || t.getDonViTinh().trim().isEmpty()
    ) return "Đơn vị tính không được để trống";
    if (t.getDonGiaBan() <= 0) return "Đơn giá bán phải lớn hơn 0";
    if (t.getSoLuongTon() < 0) return "Số lượng tồn không được âm";
    return null;
  }

  // Lấy tất cả thuốc
  public ArrayList<ThuocDTO> list() {
    return thuocDAO.getAllThuoc();
  }

  // Thêm thuốc mới
  public boolean addThuoc(ThuocDTO t) {
    String error = validate(t);
    if (error != null) {
      System.err.println(error);
      return false;
    }
    return thuocDAO.insertThuoc(t);
  }

  // Cập nhật thuốc
  public boolean updateThuoc(ThuocDTO t) {
    if (
      t != null && (t.getMaThuoc() == null || t.getMaThuoc().trim().isEmpty())
    ) {
      System.err.println("Mã thuốc không được để trống");
      return false;
    }
    String error = validate(t);
    if (error != null) {
      System.err.println(error);
      return false;
    }
    return thuocDAO.updateThuoc(t);
  }

  //xóa theo mã
  public boolean deleteByMa(String maThuoc) {
    return thuocDAO.deleteThuoc(maThuoc);
  }

  //tìm theo mã
  public ThuocDTO getByMa(String maThuoc) {
    return thuocDAO.searchById(maThuoc);
  }

  //tìm theo tên
  public ArrayList<ThuocDTO> timTheoTen(String tenThuoc) {
    return thuocDAO.searchByTenThuoc(tenThuoc);
  }

  //tìm theo hoạt chất
  public ArrayList<ThuocDTO> timTheoHoatChat(String HoatChat) {
    return thuocDAO.searchByHoatChat(HoatChat);
  }

  //tìm theo đơn giá bán
  public ArrayList<ThuocDTO> timTheoDonGiaBan(Float donGiaBan) {
    return thuocDAO.searchByGiaBan(donGiaBan);
  }

  //tính số lượng tồn
  public int tinhSLTon(String maThuoc) {
    return thuocDAO.getSoLuongTon(maThuoc);
  }

  // Trừ số lượng tồn kho (dùng khi giao thuốc)
  public boolean truSoLuongTon(String maThuoc, int soLuongTru) {
    if (soLuongTru <= 0) {
      System.err.println("Số lượng trừ phải lớn hơn 0");
      return false;
    }

    // Kiểm tra tồn kho trước khi trừ
    ThuocDTO thuoc = thuocDAO.searchById(maThuoc);
    if (thuoc == null) {
      System.err.println("Không tìm thấy thuốc");
      return false;
    }

    if (thuoc.getSoLuongTon() < soLuongTru) {
      System.err.println(
        "Không đủ tồn kho. Tồn: " +
          thuoc.getSoLuongTon() +
          ", cần: " +
          soLuongTru
      );
      return false;
    }

    return thuocDAO.truSoLuongTon(maThuoc, soLuongTru);
  }

  // Lấy danh sách thuốc còn tồn
  public ArrayList<ThuocDTO> getThuocConTon() {
    return thuocDAO.getThuocConTon();
  }
}
