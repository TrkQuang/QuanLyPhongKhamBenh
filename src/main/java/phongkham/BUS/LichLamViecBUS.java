package phongkham.BUS;

import phongkham.DTO.LichLamViecDTO;
import phongkham.dao.LichLamViecDAO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LichLamViecBUS {
    private LichLamViecDAO dao = new LichLamViecDAO();
    private static final List<String> VALID_CA = Arrays.asList("Sang", "Chieu", "Toi");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ArrayList<LichLamViecDTO> getAll() {
        return dao.getAll();
    }

    public boolean add(LichLamViecDTO llv) {
        if (!validate(llv, false))
            return false;
        boolean result = dao.insert(llv);
        if (result)
            System.out.println("Them lich lam viec: " + llv.getMaLichLam());
        return result;
    }

    public boolean update(LichLamViecDTO llv) {
        if (!validate(llv, true))
            return false;
        boolean result = dao.update(llv);
        if (result)
            System.out.println("Cap nhat lich lam viec: " + llv.getMaLichLam());
        return result;
    }

    public boolean delete(String maLichLam) {
        if (isEmpty(maLichLam) || getById(maLichLam) == null) {
            System.out.println("Lich lam viec khong ton tai");
            return false;
        }
        boolean result = dao.delete(maLichLam);
        if (result)
            System.out.println("Xoa lich lam viec: " + maLichLam);
        return result;
    }

    public LichLamViecDTO getById(String maLichLam) {
        ArrayList<LichLamViecDTO> list = dao.getAll();
        for (LichLamViecDTO llv : list) {
            if (llv.getMaLichLam().equals(maLichLam))
                return llv;
        }
        return null;
    }

    public ArrayList<LichLamViecDTO> getByBacSi(String maBacSi) {
        return isEmpty(maBacSi) ? new ArrayList<>() : dao.getByBacSi(maBacSi);
    }

    public ArrayList<LichLamViecDTO> getByNgay(String ngay) {
        ArrayList<LichLamViecDTO> result = new ArrayList<>();
        if (!isEmpty(ngay)) {
            for (LichLamViecDTO llv : dao.getAll()) {
                if (llv.getNgayLam().equals(ngay))
                    result.add(llv);
            }
        }
        return result;
    }

    public ArrayList<LichLamViecDTO> getByCa(String ca) {
        ArrayList<LichLamViecDTO> result = new ArrayList<>();
        if (!isEmpty(ca)) {
            for (LichLamViecDTO llv : dao.getAll()) {
                if (llv.getCaLam().equals(ca))
                    result.add(llv);
            }
        }
        return result;
    }

    public ArrayList<LichLamViecDTO> getByBacSiAndNgay(String maBacSi, String ngay) {
        ArrayList<LichLamViecDTO> result = new ArrayList<>();
        if (!isEmpty(maBacSi) && !isEmpty(ngay)) {
            for (LichLamViecDTO llv : dao.getByBacSi(maBacSi)) {
                if (llv.getNgayLam().equals(ngay))
                    result.add(llv);
            }
        }
        return result;
    }

    public ArrayList<LichLamViecDTO> getSapToi(String maBacSi) {
        String today = LocalDate.now().format(DATE_FORMAT);
        ArrayList<LichLamViecDTO> result = new ArrayList<>();
        for (LichLamViecDTO llv : getByBacSi(maBacSi)) {
            if (llv.getNgayLam().compareTo(today) >= 0)
                result.add(llv);
        }
        return result;
    }

    public ArrayList<LichLamViecDTO> getQuaKhu(String maBacSi) {
        String today = LocalDate.now().format(DATE_FORMAT);
        ArrayList<LichLamViecDTO> result = new ArrayList<>();
        for (LichLamViecDTO llv : getByBacSi(maBacSi)) {
            if (llv.getNgayLam().compareTo(today) < 0)
                result.add(llv);
        }
        return result;
    }

    public boolean checkConflict(String maBacSi, String ngay, String ca) {
        if (isEmpty(maBacSi) || isEmpty(ngay) || isEmpty(ca)) {
            return false;
        }
        ArrayList<LichLamViecDTO> list = getByBacSiAndNgay(maBacSi, ngay);
        for (LichLamViecDTO llv : list) {
            if (llv.getCaLam().equals(ca)) {
                System.out.println(
                        "Xung dot: Bac si " + maBacSi + " da co lich lam viec trong ca " + ca + " ngay " + ngay);
                return true;
            }
        }
        return false;
    }

    public int countAll() {
        return dao.getAll().size();
    }

    public int countByBacSi(String maBacSi) {
        return getByBacSi(maBacSi).size();
    }

    public boolean exists(String maLichLam) {
        return getById(maLichLam) != null;
    }

    public String generateMaLichLam() {
        ArrayList<LichLamViecDTO> list = dao.getAll();
        int maxId = 0;
        for (LichLamViecDTO llv : list) {
            try {
                if (llv.getMaLichLam().startsWith("LLV")) {
                    int id = Integer.parseInt(llv.getMaLichLam().substring(3));
                    maxId = Math.max(maxId, id);
                }
            } catch (Exception e) {
            }
        }
        return String.format("LLV%03d", maxId + 1);
    }

    public List<String> getValidCa() {
        return VALID_CA;
    }

    public String thongKe() {
        int sang = getByCa("Sang").size();
        int chieu = getByCa("Chieu").size();
        int toi = getByCa("Toi").size();
        return String.format("Tong: %d (Sang: %d, Chieu: %d, Toi: %d)",
                sang + chieu + toi, sang, chieu, toi);
    }

    private boolean validate(LichLamViecDTO llv, boolean isUpdate) {
        if (llv == null || isEmpty(llv.getMaLichLam()) || isEmpty(llv.getMaBacSi())) {
            System.out.println("Thong tin khong day du");
            return false;
        }
        if (isEmpty(llv.getNgayLam()) || !isValidDate(llv.getNgayLam())) {
            System.out.println("Ngay khong hop le (yyyy-MM-dd)");
            return false;
        }
        if (isEmpty(llv.getCaLam()) || !VALID_CA.contains(llv.getCaLam())) {
            System.out.println("Ca khong hop le (Sang/Chieu/Toi)");
            return false;
        }
        if (!isUpdate && exists(llv.getMaLichLam())) {
            System.out.println("Ma da ton tai");
            return false;
        }
        if (isUpdate && !exists(llv.getMaLichLam())) {
            System.out.println("Ma khong ton tai");
            return false;
        }
        return true;
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMAT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}