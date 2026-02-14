package phongkham.BUS;

import phongkham.DTO.PhieuNhapDTO;
import phongkham.dao.PhieuNhapDAO;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PhieuNhapBUS {
    private PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();

    //lấy tất cả phiếu nhập
    public ArrayList<PhieuNhapDTO> list(){
        return phieuNhapDAO.getAllPhieuNhap();
    }

    //thêm phiếu nhập
    public boolean addPhieuNhap(PhieuNhapDTO pn){
        if(pn == null){
            System.err.println("Phiếu nhập không được để trống");
            return false;
        }
        if(pn.getMaPhieuNhap() == null || pn.getMaPhieuNhap().trim().isEmpty() || pn.getMaNhaCungCap() == null || pn.getMaNhaCungCap().trim().isEmpty()){
            System.err.println("Mã phiếu nhập và mã nhà cung cấp không được để trống");
            return false;
        }
        if(pn.getNgayNhap() == null || pn.getNgayNhap().trim().isEmpty()){
            System.err.println("Ngày nhập không được để trống");
            return false;
        }
        if(pn.getNguoiGiao() == null || pn.getNguoiGiao().trim().isEmpty()){
            System.err.println("Người giao không được để trống");
            return false;
        }
        if(pn.getTongTienNhap() < 0){
            System.err.println("Tổng tiền nhập không được âm");
            return false;
        }
        return phieuNhapDAO.insertPhieuNhap(pn);
    }

    //cập nhật phiếu nhập
    public boolean updatePN(PhieuNhapDTO pn){
        if(pn == null){
            System.err.println("Phiếu nhập không được để trống");
            return false;
        }
        if(pn.getMaPhieuNhap() == null || pn.getMaPhieuNhap().trim().isEmpty() || pn.getMaNhaCungCap() == null || pn.getMaNhaCungCap().trim().isEmpty()){
            System.err.println("Mã phiếu nhập và mã nhà cung cấp không được để trống");
            return false;
        }
        if(pn.getNgayNhap() == null || pn.getNgayNhap().trim().isEmpty()){
            System.err.println("Ngày nhập không được để trống");
            return false;
        }
        if(pn.getNguoiGiao() == null || pn.getNguoiGiao().trim().isEmpty()){
            System.err.println("Người giao không được để trống");
            return false;
        }
        if(pn.getTongTienNhap() < 0){
            System.err.println("Tổng tiền nhập không được âm");
            return false;
        }
        return phieuNhapDAO.updatePhieuNhap(pn);
    }

    //xóa phiếu nhập theo mã
    public boolean deletePN(String MaPN){
        return phieuNhapDAO.deletePhieuNhap(MaPN);
    }

    //tìm kiếm theo mã phiếu nhập
    public PhieuNhapDTO searchByID(String MaPN){
        return phieuNhapDAO.getById(MaPN);
    }

    //tìm kiếm theo khoảng ngày
    public ArrayList<PhieuNhapDTO> searchByDate(LocalDateTime startDate, LocalDateTime endDate){
        return phieuNhapDAO.getByDate(startDate, endDate);
    }

    //tìm theo mã nhà cung cấp
    public ArrayList<PhieuNhapDTO> searchByNCC(String MaNCC){
        return phieuNhapDAO.getByMaNCC(MaNCC);
    }

    //tìm theo người giao
    public ArrayList<PhieuNhapDTO> searchByNguoiGiao(String NguoiGiao){
        return phieuNhapDAO.getByNguoiGiao(NguoiGiao);
    }

    //xóa phiếu nhập theo mã nhà cung cấp
    public boolean deleteByMaNCC(String MaNCC){
        return phieuNhapDAO.deleteByNCC(MaNCC);
    }

    //ktra nhà cung cấp có phiếu nhập chưa
    public boolean checkNCC(String MaNCC){
        return phieuNhapDAO.hasPhieuNhap(MaNCC);
    }

}
