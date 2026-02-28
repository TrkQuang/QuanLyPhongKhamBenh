package phongkham.BUS;

import phongkham.DTO.HoaDonKhamDTO;
import phongkham.dao.HoaDonKhamDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HoaDonKhamBUS {

    private HoaDonKhamDAO hdDAO = new HoaDonKhamDAO();

    public ArrayList<HoaDonKhamDTO> getAll() {
        return hdDAO.getAll();
    }

    public HoaDonKhamDTO search(String key) {
        return hdDAO.Search(key);
    }

    public ArrayList<HoaDonKhamDTO> filterByDate(LocalDate from, LocalDate to) {
    LocalDateTime f = from.atStartOfDay();
    LocalDateTime t = to.atTime(23, 59, 59);
    return hdDAO.filterByDate(f, t);
    }


    public ArrayList<HoaDonKhamDTO> searchAndFilter(String key, LocalDate from, LocalDate to) {
        ArrayList<HoaDonKhamDTO> list = new ArrayList<>();

        for (HoaDonKhamDTO hd : hdDAO.getAll()) {
            boolean match = true;

            if (key != null && !key.isEmpty()) {
                match &= String.valueOf(hd.getMaHDKham()).contains(key)
                        || String.valueOf(hd.getMaHoSo()).contains(key)
                        || String.valueOf(hd.getMaGoi()).contains(key);
            }

            if (from != null && to != null && hd.getNgayThanhToan() != null) {
                LocalDateTime f = from.atStartOfDay();
                LocalDateTime t = to.atTime(23, 59, 59);

                match &= !hd.getNgayThanhToan().isBefore(f)
                            && !hd.getNgayThanhToan().isAfter(t);
}

            if (match) list.add(hd);
        }
        return list;
    }
}
