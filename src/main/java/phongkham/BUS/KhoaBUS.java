package phongkham.BUS;

import phongkham.DTO.KhoaDTO;
import phongkham.dao.KhoaDAO;
import java.util.ArrayList;
public class KhoaBUS {
    KhoaDAO dao = new KhoaDAO();
    public ArrayList<KhoaDTO> getAll() {
        ArrayList<KhoaDTO> list = new ArrayList<>();
        list = dao.getAll();
        return list;
    }
}
