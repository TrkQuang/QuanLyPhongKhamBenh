package phongkham.BUS;

import phongkham.DTO.ThuocDTO;
import phongkham.dao.ThuocDAO;
import java.util.ArrayList;

public class ThuocBUS {
    private ThuocDAO thuocDAO = new ThuocDAO();

        //lấy tất cả nhà cung cấp
        public ArrayList<ThuocDTO> list(){
            return thuocDAO.getAllThuoc();
        }
        
        //thêm thuốc mới
        public boolean addThuoc(ThuocDTO t){
            if(t == null){
                System.err.println("Thuốc không được để trống");
                return false;
            }
            if(t.getMaThuoc() == null || t.getMaThuoc().trim().isEmpty()){
                System.err.println("Mã thuốc không được để trống");
                return false;
            }
            if(t.getTenThuoc() == null || t.getTenThuoc().trim().isEmpty()){
                System.err.println("Tên thuốc không được để trống");
                return false;
            }
            if(t.getHoatChat() == null || t.getHoatChat().trim().isEmpty()){
                System.err.println("Hoạt chất không được để trống");
                return false;
            }
            if(t.getDonViTinh() == null || t.getDonViTinh().trim().isEmpty()){
                System.err.println("Đơn vị tính không được để trống");
                return false;
            }
            if(t.getDonGiaBan() <=0){
                System.err.println("Đơn giá bán không được âm");
                return false;
            }
            if(t.getSoLuongTon() <=0){
                System.err.println("Số lượng tồn không được âm");
                return false;
            }
            return thuocDAO.insertThuoc(t);
        }

        //cập nhật thuốc
        public boolean UpdateThuoc(ThuocDTO t){
            if(t == null){
                System.err.println("Thuốc không được để trống");
                return false;
            }
            if(t.getMaThuoc() == null || t.getMaThuoc().trim().isEmpty()){
                System.err.println("Mã thuốc không được để trống");
                return false;
            }
            if(t.getTenThuoc() == null || t.getTenThuoc().trim().isEmpty()){
                System.err.println("Tên thuốc không được để trống");
                return false;
            }
            if(t.getHoatChat() == null || t.getHoatChat().trim().isEmpty()){
                System.err.println("Hoạt chất không được để trống");
                return false;
            }
            if(t.getDonViTinh() == null || t.getDonViTinh().trim().isEmpty()){
                System.err.println("Đơn vị tính không được để trống");
                return false;
            }
            if(t.getDonGiaBan() <=0){
                System.err.println("Đơn giá bán không được âm");
                return false;
            }
            if(t.getSoLuongTon() <=0){
                System.err.println("Số lượng tồn không được âm");
                return false;
            }
            return thuocDAO.updateThuoc(t);
        }

        //xóa theo mã
        public boolean deleteByMa(String maThuoc){
            return thuocDAO.deleteThuoc(maThuoc);
        }

        //tìm theo mã
        public ThuocDTO getByMa(String maThuoc){
            return thuocDAO.searchById(maThuoc);
        }

        //tìm theo tên
        public ArrayList<ThuocDTO> timTheoTen(String tenThuoc){
            return thuocDAO.searchByTenThuoc(tenThuoc);
        }

        //tìm theo hoạt chất
        public ArrayList<ThuocDTO> timTheoHoatChat(String HoatChat){
            return thuocDAO.searchByHoatChat(HoatChat);
        }

        //tìm theo đơn giá bán
        public ArrayList<ThuocDTO> timTheoDonGiaBan(Float donGiaBan){
            return thuocDAO.searchByGiaBan(donGiaBan);
        }

        //tính số lượng tồn
        public int tinhSLTon(String maThuoc){
            return thuocDAO.getSoLuongTon(maThuoc);
        }
}
