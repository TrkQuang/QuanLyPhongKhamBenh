package phongkham.BUS;

import phongkham.DTO.NhaCungCapDTO;
import phongkham.dao.NhaCungCapDAO;
import java.util.ArrayList;

public class NhaCungCapBUS {
    private NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

    //Lấy tất cả nhà cung cấp
    public ArrayList<NhaCungCapDTO> list(){
        return nhaCungCapDAO.getAllNhaCungCap();
    }

    //thêm nhà cung cấp
    public boolean addNCC(NhaCungCapDTO ncc){
        if(ncc == null){
            System.err.println("Nhà cung cấp không được để trống");
            return false;
        }
        if(ncc.getMaNhaCungCap() == null || ncc.getMaNhaCungCap().trim().isEmpty()){
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
        if(ncc.getMaNhaCungCap() == null || ncc.getMaNhaCungCap().trim().isEmpty()){
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

    //xóa nhà cung cấp theo mã
    public boolean deleteNCC(String MaNhaCungCap){
        return nhaCungCapDAO.deleteNhaCungCap(MaNhaCungCap);
    }

    //tìm nhà cung cấp theo mã
    public NhaCungCapDTO layTheoMa(String MaNhaCungCap){
        return nhaCungCapDAO.getById(MaNhaCungCap);
    }

    //tìm theo tên
    public ArrayList <NhaCungCapDTO> timTheoTen(String TenNhaCungCap){
        return nhaCungCapDAO.searchByName(TenNhaCungCap);
    }

    //tìm theo địa chỉ
    public ArrayList<NhaCungCapDTO> timTheoDiaChi(String DiaChi){
        return nhaCungCapDAO.searchByAddress(DiaChi);
    }

    //tìm theo sdt
    public ArrayList<NhaCungCapDTO> timTheoSDT(String SDT){
        return nhaCungCapDAO.searchByPhone(SDT);
    }
}
 