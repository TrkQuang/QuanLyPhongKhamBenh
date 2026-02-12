package phongkham.BUS;

import phongkham.DTO.NhaCungCapDTO;
import phongkham.dao.NhaCungCapDAO;


public class NhaCungCapBUS {
    private NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

    //thêm nhà cung cấp
    public boolean addNCC(NhaCungCapDTO ncc){
        if(ncc == null){
            System.err.println("Nhà cung cấp không được để trống");
            return false;
        }
        if(ncc.getMaNhaCungCap() == null){
            System.err.println("Mã nhà cung cấp không được để trống");
            return false;
        }
        if(ncc.getTenNhaCungCap() == null || ncc.getTenNhaCungCap().trim().isEmpty()){
            System.err.println("Tên nhà cung cấp không được để trống");
            return false;
        }
        if(ncc.getDiaChi() == null || ncc.getDiaChi().trim().isEmpty()){
            System.err.println("Địa chỉ không được để trống");
            return false;
        }
        if(ncc.getSDT() == null || !ncc.getSDT().trim().matches("^(03|05|07|08|09)\\\\d{8}$")){
            System.err.println("Số điện thoại không hợp lệ");
            return false;
        }
        return nhaCungCapDAO.insertNhaCungCap(ncc);
    }

    //cập nhật nhà cung cấp
    public boolean updateNCC(NhaCungCapDTO ncc){
        if(ncc == null){
            System.err.println("Nhà cung cấp không được để trống");
            return false;
        }
        if(ncc.getMaNhaCungCap() == null){
            System.err.println("Mã nhà cung cấp không được để trống");
            return false;
        }
        if(ncc.getTenNhaCungCap() == null || ncc.getTenNhaCungCap().trim().isEmpty()){
            System.err.println("Tên nhà cung cấp không được để trống");
            return false;
        }
        if(ncc.getDiaChi() == null || ncc.getDiaChi().trim().isEmpty()){
            System.err.println("Địa chỉ không được để trống");
            return false;
        }
        if(ncc.getSDT() == null || !ncc.getSDT().trim().matches("^(03|05|07|08|09)\\\\d{8}$")){
            System.err.println("Số điện thoại không hợp lệ");
            return false;
        }
        return nhaCungCapDAO.updateNhaCungCap(ncc);
    }

    //xóa nhà cung cấp
    public boolean deleteNCC(String MaNhaCungCap){
        return nhaCungCapDAO.deleteNhaCungCap(MaNhaCungCap);
    }
}
 