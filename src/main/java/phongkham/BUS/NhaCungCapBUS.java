package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.NhaCungCapDTO;
import phongkham.dao.NhaCungCapDAO;

public class NhaCungCapBUS {

  private NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

  // Lấy tất cả nhà cung cấp
  public ArrayList<NhaCungCapDTO> list() {
    return nhaCungCapDAO.getAllNhaCungCap();
  }

  // Thêm nhà cung cấp
  public boolean addNCC(NhaCungCapDTO ncc) {
    String error = validate(ncc);
    if (error != null) {
      System.err.println(error);
      return false;
    }
    return nhaCungCapDAO.insertNhaCungCap(ncc);
  }

  // Cập nhật nhà cung cấp
  public boolean updateNCC(NhaCungCapDTO ncc) {
    String error = validate(ncc);
    if (error != null) {
      System.err.println(error);
      return false;
    }
    return nhaCungCapDAO.updateNhaCungCap(ncc);
  }

  // Xóa nhà cung cấp theo mã
  public boolean deleteNCC(String maNhaCungCap) {
    return nhaCungCapDAO.deleteNhaCungCap(maNhaCungCap);
  }

  // Tìm nhà cung cấp theo mã
  public NhaCungCapDTO layTheoMa(String maNhaCungCap) {
    return nhaCungCapDAO.getById(maNhaCungCap);
  }

  // Tìm theo tên
  public ArrayList<NhaCungCapDTO> timTheoTen(String tenNhaCungCap) {
    return nhaCungCapDAO.searchByName(tenNhaCungCap);
  }

  // Tìm theo địa chỉ
  public ArrayList<NhaCungCapDTO> timTheoDiaChi(String diaChi) {
    return nhaCungCapDAO.searchByAddress(diaChi);
  }

  // Tìm theo số điện thoại
  public ArrayList<NhaCungCapDTO> timTheoSDT(String sdt) {
    return nhaCungCapDAO.searchByPhone(sdt);
  }

  private String validate(NhaCungCapDTO ncc) {
    if (ncc == null) return "Nhà cung cấp không được để trống";
    if (
      ncc.getMaNhaCungCap() == null || ncc.getMaNhaCungCap().trim().isEmpty()
    ) return "Mã nhà cung cấp không được để trống";
    if (
      ncc.getTenNhaCungCap() == null || ncc.getTenNhaCungCap().trim().isEmpty()
    ) return "Tên nhà cung cấp không được để trống";
    if (
      ncc.getDiaChi() == null || ncc.getDiaChi().trim().isEmpty()
    ) return "Địa chỉ không được để trống";
    if (
      ncc.getSDT() == null ||
      !ncc.getSDT().trim().matches("^(03|05|07|08|09)\\d{8}$")
    ) return "Số điện thoại không hợp lệ";
    return null;
  }
}
