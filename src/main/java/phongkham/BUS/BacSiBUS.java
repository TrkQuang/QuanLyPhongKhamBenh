package phongkham.BUS;

import phongkham.dao.BacSiDAO;
import phongkham.DTO.BacSiDTO;
import java.util.ArrayList;

public class BacSiBUS {
    private BacSiDAO dao = new BacSiDAO();
    public ArrayList<BacSiDTO> getAll() {
        return dao.getAll();
    }
}
