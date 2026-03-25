package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import phongkham.BUS.PhanQuyenChiTietBUS;
import phongkham.DTO.RolesDTO;
import phongkham.Utils.Session;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;
import phongkham.gui.main.MainFrame;

public class QuanLyPhanQuyenPanel extends BasePanel {

  private final PhanQuyenChiTietBUS phanQuyenBUS = new PhanQuyenChiTietBUS();

  private JComboBox<RoleOption> cbRole;
  private JPanel permissionContainer;
  private javax.swing.JButton btnTaiLai;
  private javax.swing.JButton btnLuu;
  private JLabel lblLoading;
  private javax.swing.Timer loadingTimer;
  private int loadingTick = 0;

  private final List<RolesDTO> dsRole = new ArrayList<>();
  private final List<PermissionItem> danhMucQuyen = new ArrayList<>();
  private final Map<Integer, Set<String>> mapRoleQuyen = new HashMap<>();
  private final Map<Integer, Set<String>> mapRoleQuyenTam = new HashMap<>();

  private boolean dangNapDuLieu = false;
  private boolean dangTaiNen = false;
  private boolean coQuyenXem = true;
  private boolean coQuyenCapNhat = true;

  @Override
  protected void init() {
    cbRole = new JComboBox<>();
    UIUtils.fixedSize(cbRole, 320, 34);
    cbRole.addActionListener(e -> {
      if (dangNapDuLieu) {
        return;
      }
      renderDanhSachQuyenTheoRoleDangChon();
    });

    JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    topLeft.setOpaque(false);
    topLeft.add(new JLabel("Vai trò"));
    topLeft.add(cbRole);

    JPanel topActions = UIUtils.row(
      UIUtils.primaryButton("Lưu thay đổi"),
      UIUtils.ghostButton("Tải lại")
    );
    btnLuu = (javax.swing.JButton) topActions.getComponent(0);
    btnLuu.setEnabled(false);
    btnTaiLai = (javax.swing.JButton) topActions.getComponent(1);
    lblLoading = new JLabel(" ");
    UIUtils.fixedSize(lblLoading, 120, 34);
    topActions.add(lblLoading);

    btnLuu.addActionListener(e -> luuTatCaThayDoiPhanQuyen());
    btnTaiLai.addActionListener(e -> taiDuLieuAsync());

    JPanel top = new JPanel(new BorderLayout(8, 0));
    top.setOpaque(false);
    top.add(topLeft, BorderLayout.WEST);
    top.add(topActions, BorderLayout.EAST);

    permissionContainer = new JPanel();
    permissionContainer.setLayout(
      new javax.swing.BoxLayout(
        permissionContainer,
        javax.swing.BoxLayout.Y_AXIS
      )
    );
    permissionContainer.setOpaque(false);

    add(UIUtils.createSection("Chọn vai trò", top), BorderLayout.NORTH);
    add(
      UIUtils.createSection(
        "Phân quyền chi tiết theo từng panel (tick trực tiếp từng tính năng)",
        new JScrollPane(permissionContainer)
      ),
      BorderLayout.CENTER
    );

    apDungPhanQuyenHanhDong();

    taiDuLieuAsync();
  }

  private void taiDuLieuAsync() {
    if (dangTaiNen) {
      return;
    }
    if (!coQuyenXem) {
      return;
    }

    final int selectedRoleId = getRoleDangChon();
    capNhatTrangThaiDangTai(true);

    new SwingWorker<LoadResult, Void>() {
      @Override
      protected LoadResult doInBackground() {
        return taiDuLieuNoiBo();
      }

      @Override
      protected void done() {
        try {
          LoadResult result = get();
          if (!result.schemaHopLe) {
            DialogHelper.warn(
              QuanLyPhanQuyenPanel.this,
              "Chưa tìm thấy schema phân quyền chi tiết. Vui lòng chạy SQL migrate_rbac_chitiet_2026-03-24.sql"
            );
            return;
          }

          dsRole.clear();
          dsRole.addAll(result.roles);

          mapRoleQuyen.clear();
          mapRoleQuyen.putAll(result.roleQuyen);

          mapRoleQuyenTam.clear();
          mapRoleQuyenTam.putAll(cloneMapRoleQuyen(result.roleQuyen));

          napComboboxRole(selectedRoleId);
          renderDanhSachQuyenTheoRoleDangChon();
          capNhatTrangThaiNutLuu();
        } catch (Exception ex) {
          DialogHelper.error(
            QuanLyPhanQuyenPanel.this,
            "Tải dữ liệu phân quyền thất bại: " + safe(ex.getMessage())
          );
        } finally {
          capNhatTrangThaiDangTai(false);
        }
      }
    }
      .execute();
  }

  private LoadResult taiDuLieuNoiBo() {
    if (!phanQuyenBUS.daCoBangPhanQuyenChiTiet()) {
      return LoadResult.schemaKhongHopLe();
    }

    damBaoQuyenNenBatBuoc();
    khoiTaoDanhMucQuyenCoBan();
    boSungQuyenConThieuTuDatabase();

    ArrayList<RolesDTO> roles = phanQuyenBUS.layTatCaVaiTro();
    Map<Integer, Set<String>> roleQuyen = phanQuyenBUS.layMapQuyenTheoRole();
    return LoadResult.hopLe(roles, roleQuyen);
  }

  private void damBaoQuyenNenBatBuoc() {
    phanQuyenBUS.damBaoQuyenChiTiet(
      "DUYETLICHLAM",
      "XEM",
      "DUYETLICHLAM_XEM",
      "Admin - Duyệt lịch làm bác sĩ"
    );
  }

  private void capNhatTrangThaiDangTai(boolean dangTai) {
    dangNapDuLieu = dangTai;
    dangTaiNen = dangTai;

    if (cbRole != null) {
      cbRole.setEnabled(!dangTai && coQuyenXem);
    }
    if (btnTaiLai != null) {
      btnTaiLai.setEnabled(!dangTai && coQuyenXem);
      btnTaiLai.setText("Tải lại");
    }
    if (btnLuu != null) {
      btnLuu.setEnabled(!dangTai && coQuyenCapNhat && coThayDoiChuaLuu());
    }

    if (lblLoading != null) {
      lblLoading.setVisible(dangTai);
      if (dangTai) {
        batDauHieuUngLoading();
      } else {
        dungHieuUngLoading();
      }
    }
  }

  private void batDauHieuUngLoading() {
    loadingTick = 0;
    if (lblLoading != null) {
      lblLoading.setText("LOADING");
    }
    if (loadingTimer == null) {
      loadingTimer = new javax.swing.Timer(350, e -> {
        loadingTick = (loadingTick + 1) % 4;
        if (lblLoading != null) {
          lblLoading.setText("LOADING" + ".".repeat(loadingTick));
        }
      });
    }
    loadingTimer.start();
  }

  private void dungHieuUngLoading() {
    if (loadingTimer != null) {
      loadingTimer.stop();
    }
    if (lblLoading != null) {
      lblLoading.setText(" ");
    }
  }

  private int getRoleDangChon() {
    RoleOption option = (RoleOption) cbRole.getSelectedItem();
    return option == null ? -1 : option.roleId;
  }

  private void napComboboxRole(int selectedRoleId) {
    cbRole.removeAllItems();
    int viTriCanChon = -1;
    int index = 0;

    for (RolesDTO role : dsRole) {
      int roleId = parseRoleId(role.getSTT());
      if (roleId <= 0) {
        continue;
      }

      cbRole.addItem(new RoleOption(roleId, safe(role.getTenVaiTro())));
      if (roleId == selectedRoleId) {
        viTriCanChon = index;
      }
      index++;
    }

    if (viTriCanChon >= 0) {
      cbRole.setSelectedIndex(viTriCanChon);
    } else if (cbRole.getItemCount() > 0) {
      cbRole.setSelectedIndex(0);
    }
  }

  private void renderDanhSachQuyenTheoRoleDangChon() {
    RoleOption role = (RoleOption) cbRole.getSelectedItem();
    permissionContainer.removeAll();

    if (role == null) {
      permissionContainer.revalidate();
      permissionContainer.repaint();
      return;
    }

    Set<String> tapQuyen = mapRoleQuyenTam.getOrDefault(
      role.roleId,
      new HashSet<>()
    );

    Map<String, List<PermissionItem>> grouped = new LinkedHashMap<>();
    for (PermissionItem item : danhMucQuyen) {
      grouped.computeIfAbsent(item.panelName, k -> new ArrayList<>()).add(item);
    }

    if (grouped.isEmpty()) {
      permissionContainer.add(
        new JLabel("Chưa có danh mục quyền chi tiết trong database.")
      );
      permissionContainer.revalidate();
      permissionContainer.repaint();
      return;
    }

    List<Map.Entry<String, List<PermissionItem>>> orderedGroups =
      new ArrayList<>(grouped.entrySet());
    orderedGroups.sort((a, b) -> {
      int pa = thuTuNhomTheoVaiTro(a.getKey());
      int pb = thuTuNhomTheoVaiTro(b.getKey());
      if (pa != pb) {
        return Integer.compare(pa, pb);
      }
      return safe(a.getKey()).compareToIgnoreCase(safe(b.getKey()));
    });

    for (Map.Entry<String, List<PermissionItem>> entry : orderedGroups) {
      List<JCheckBox> danhSachCheckbox = new ArrayList<>();
      JCheckBox cbChonTatCa = new JCheckBox("Chọn tất cả");
      cbChonTatCa.setOpaque(false);
      cbChonTatCa.setEnabled(coQuyenCapNhat);

      JPanel grid = new JPanel(new GridLayout(0, 3, 12, 8));
      grid.setOpaque(false);

      for (PermissionItem item : entry.getValue()) {
        JCheckBox cb = new JCheckBox(item.displayName);
        cb.setOpaque(false);
        cb.setSelected(tapQuyen.contains(item.maQuyen));
        cb.setToolTipText(item.maQuyen);
        cb.setEnabled(coQuyenCapNhat);
        cb.addActionListener(e -> {
          if (dangNapDuLieu) {
            return;
          }
          capNhatQuyenTam(role.roleId, item.maQuyen, cb.isSelected());
          capNhatTrangThaiChonTatCa(cbChonTatCa, danhSachCheckbox);
          capNhatTrangThaiNutLuu();
        });
        grid.add(cb);
        danhSachCheckbox.add(cb);
      }

      capNhatTrangThaiChonTatCa(cbChonTatCa, danhSachCheckbox);
      cbChonTatCa.addActionListener(e ->
        capNhatChonTatCaTrongNhom(
          role.roleId,
          entry.getValue(),
          danhSachCheckbox,
          cbChonTatCa
        )
      );

      permissionContainer.add(
        UIUtils.createSection(entry.getKey(), grid, UIUtils.row(cbChonTatCa))
      );
      permissionContainer.add(javax.swing.Box.createVerticalStrut(8));
    }

    permissionContainer.revalidate();
    permissionContainer.repaint();
  }

  private void capNhatQuyenTam(int roleId, String maQuyen, boolean duocCap) {
    if (!coQuyenCapNhat) {
      DialogHelper.warn(this, "Ban khong co quyen cap nhat phan quyen.");
      return;
    }

    Set<String> tap = mapRoleQuyenTam.computeIfAbsent(roleId, k ->
      new HashSet<>()
    );
    if (duocCap) {
      tap.add(maQuyen);
    } else {
      tap.remove(maQuyen);
    }
  }

  private void capNhatChonTatCaTrongNhom(
    int roleId,
    List<PermissionItem> dsTrongNhom,
    List<JCheckBox> dsCheckbox,
    JCheckBox cbChonTatCa
  ) {
    if (dangNapDuLieu) {
      return;
    }
    if (!coQuyenCapNhat) {
      cbChonTatCa.setSelected(!cbChonTatCa.isSelected());
      DialogHelper.warn(this, "Bạn không có quyền cập nhật phân quyền.");
      return;
    }

    boolean duocCap = cbChonTatCa.isSelected();
    Set<String> tapMoi = new HashSet<>(
      mapRoleQuyenTam.getOrDefault(roleId, new HashSet<>())
    );

    for (PermissionItem item : dsTrongNhom) {
      if (duocCap) {
        tapMoi.add(item.maQuyen);
      } else {
        tapMoi.remove(item.maQuyen);
      }
    }

    mapRoleQuyenTam.put(roleId, tapMoi);

    dangNapDuLieu = true;
    try {
      for (JCheckBox cb : dsCheckbox) {
        cb.setSelected(duocCap);
      }
    } finally {
      dangNapDuLieu = false;
    }
    capNhatTrangThaiChonTatCa(cbChonTatCa, dsCheckbox);
    capNhatTrangThaiNutLuu();
  }

  private void luuTatCaThayDoiPhanQuyen() {
    if (!coQuyenCapNhat) {
      DialogHelper.warn(this, "Bạn không có quyền cập nhật phân quyền.");
      return;
    }

    List<Integer> dsRoleCanLuu = new ArrayList<>();
    for (Map.Entry<Integer, Set<String>> entry : mapRoleQuyenTam.entrySet()) {
      int roleId = entry.getKey();
      Set<String> tapTam =
        entry.getValue() == null
          ? new HashSet<>()
          : new HashSet<>(entry.getValue());
      Set<String> tapGoc = mapRoleQuyen.getOrDefault(roleId, new HashSet<>());
      if (!tapTam.equals(tapGoc)) {
        dsRoleCanLuu.add(roleId);
      }
    }

    if (dsRoleCanLuu.isEmpty()) {
      DialogHelper.info(this, "Không có thay đổi nào để lưu.");
      return;
    }

    List<Integer> dsRoleLoi = new ArrayList<>();
    for (Integer roleId : dsRoleCanLuu) {
      Set<String> tapMoi = new HashSet<>(
        mapRoleQuyenTam.getOrDefault(roleId, new HashSet<>())
      );

      boolean ok = phanQuyenBUS.capNhatToanBoQuyenRole(
        roleId,
        new ArrayList<>(tapMoi)
      );
      if (!ok) {
        dsRoleLoi.add(roleId);
      }
    }

    if (!dsRoleLoi.isEmpty()) {
      DialogHelper.error(
        this,
        "Lưu phân quyền thất bại cho role: " + dsRoleLoi
      );
      return;
    }

    Session.refreshPermissions();
    lamMoiDieuHuongTheoQuyenMoi();

    DialogHelper.info(this, "Đã lưu phân quyền thành công.");
    taiDuLieuAsync();
  }

  private void lamMoiDieuHuongTheoQuyenMoi() {
    java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
    if (window instanceof MainFrame) {
      ((MainFrame) window).refreshAllPanelsAfterPermissionSave();
    }
  }

  private void capNhatTrangThaiNutLuu() {
    if (btnLuu == null) {
      return;
    }
    btnLuu.setEnabled(!dangTaiNen && coQuyenCapNhat && coThayDoiChuaLuu());
  }

  private boolean coThayDoiChuaLuu() {
    for (Map.Entry<Integer, Set<String>> entry : mapRoleQuyenTam.entrySet()) {
      int roleId = entry.getKey();
      Set<String> tapTam =
        entry.getValue() == null
          ? new HashSet<>()
          : new HashSet<>(entry.getValue());
      Set<String> tapGoc = mapRoleQuyen.getOrDefault(roleId, new HashSet<>());
      if (!tapTam.equals(tapGoc)) {
        return true;
      }
    }

    for (Map.Entry<Integer, Set<String>> entry : mapRoleQuyen.entrySet()) {
      int roleId = entry.getKey();
      Set<String> tapGoc =
        entry.getValue() == null
          ? new HashSet<>()
          : new HashSet<>(entry.getValue());
      Set<String> tapTam = mapRoleQuyenTam.getOrDefault(
        roleId,
        new HashSet<>()
      );
      if (!tapGoc.equals(tapTam)) {
        return true;
      }
    }

    return false;
  }

  private Map<Integer, Set<String>> cloneMapRoleQuyen(
    Map<Integer, Set<String>> source
  ) {
    Map<Integer, Set<String>> clone = new HashMap<>();
    if (source == null) {
      return clone;
    }
    for (Map.Entry<Integer, Set<String>> entry : source.entrySet()) {
      clone.put(entry.getKey(), new HashSet<>(entry.getValue()));
    }
    return clone;
  }

  private void capNhatTrangThaiChonTatCa(
    JCheckBox cbChonTatCa,
    List<JCheckBox> dsCheckbox
  ) {
    if (cbChonTatCa == null || dsCheckbox == null || dsCheckbox.isEmpty()) {
      return;
    }

    boolean allChecked = true;
    for (JCheckBox cb : dsCheckbox) {
      if (!cb.isSelected()) {
        allChecked = false;
        break;
      }
    }

    dangNapDuLieu = true;
    try {
      cbChonTatCa.setSelected(allChecked);
    } finally {
      dangNapDuLieu = false;
    }
  }

  private void boSungQuyenConThieuTuDatabase() {
    Set<String> daCo = new HashSet<>();
    for (PermissionItem item : danhMucQuyen) {
      daCo.add(item.maQuyen);
    }

    ArrayList<String> dsMaQuyenDb = phanQuyenBUS.layTatCaMaQuyenChiTiet();
    for (String maQuyenRaw : dsMaQuyenDb) {
      String maQuyen = safe(maQuyenRaw).trim().toUpperCase();
      if (maQuyen.isEmpty() || daCo.contains(maQuyen)) {
        continue;
      }

      PermissionItem parsed = parseFromMaQuyen(maQuyen);
      if (parsed == null) {
        continue;
      }

      danhMucQuyen.add(parsed);
      daCo.add(maQuyen);
    }
  }

  private PermissionItem parseFromMaQuyen(String maQuyen) {
    int idx = maQuyen.indexOf('_');
    if (idx <= 0 || idx >= maQuyen.length() - 1) {
      return null;
    }

    String module = maQuyen.substring(0, idx);
    if (!laModuleDuocHoTro(module)) {
      return null;
    }
    String action = maQuyen.substring(idx + 1);
    return new PermissionItem(
      module,
      action,
      maQuyen,
      panelNameFromModule(module),
      actionToDisplay(action)
    );
  }

  private String actionToDisplay(String action) {
    if ("XEM".equals(action)) return "Xem";
    if ("THEM".equals(action)) return "Thêm";
    if ("SUA".equals(action)) return "Sửa";
    if ("XOA".equals(action)) return "Xóa";
    if ("RESET_MAT_KHAU".equals(action)) return "Reset mật khẩu";
    if ("KICH_HOAT_VO_HIEU_HOA".equals(action)) return "Kích hoạt/Vô hiệu hóa";
    if ("XEM_CHI_TIET".equals(action)) return "Xem chi tiết";
    if ("CAP_NHAT".equals(action)) return "Cập nhật";
    if ("XAC_NHAN_NHAP_KHO".equals(action)) return "Xác nhận nhập kho";
    if ("XEM_LO_HSD".equals(action)) return "Xem lô/HSD";
    if ("XAC_NHAN_THANH_TOAN".equals(action)) return "Xác nhận thanh toán";
    if ("XAC_NHAN_GIAO_THUOC".equals(action)) return "Xác nhận giao thuốc";
    if ("XEM_XUAT_THEO_LO".equals(action)) return "Xem xuất theo lô";
    if ("DUYET".equals(action)) return "Duyệt";
    if ("TU_CHOI".equals(action)) return "Từ chối";
    if ("HUY".equals(action)) return "Hủy";
    if ("DOI_MAT_KHAU".equals(action)) return "Đổi mật khẩu";
    if ("DAT_LICH".equals(action)) return "Đặt lịch";
    if ("MUA_THUOC".equals(action)) return "Mua thuốc";
    if ("TRA_CUU_HO_SO".equals(action)) return "Tra cứu hồ sơ";
    return action.replace('_', ' ');
  }

  private String panelNameFromModule(String moduleCode) {
    if ("USER".equals(moduleCode)) return "Quản lý tài khoản";
    if ("BACSI".equals(moduleCode)) return "Quản lý bác sĩ";
    if ("KHOA".equals(moduleCode)) return "Quản lý khoa";
    if ("GOIDICHVU".equals(moduleCode)) return "Quản lý gói dịch vụ";
    if ("ROLE".equals(moduleCode)) return "Quản lý vai trò";
    if ("PHANQUYEN".equals(moduleCode)) return "Phân quyền chi tiết";
    if ("THUOC".equals(moduleCode)) return "Nhà thuốc - Quản lý thuốc";
    if ("NCC".equals(moduleCode)) return "Nhà thuốc - Nhà cung cấp";
    if ("PHIEUNHAP".equals(moduleCode)) return "Nhà thuốc - Phiếu nhập";
    if ("HOADONTHUOC".equals(moduleCode)) return "Nhà thuốc - Hóa đơn thuốc";
    if ("DUYETLICHLAM".equals(moduleCode)) return "Admin - Duyệt lịch làm bác sĩ";
    if ("LICHLAMVIEC".equals(moduleCode)) {
      return "Bác sĩ - Lịch làm việc (đăng ký ca)";
    }
    if ("LICHKHAM".equals(moduleCode)) return "Bác sĩ - Lịch khám";
    if ("HOADONKHAM".equals(moduleCode)) return "Bác sĩ - Hóa đơn khám";
    if ("HOSO".equals(moduleCode)) return "Bác sĩ - Bệnh án";
    if ("DONTHUOC".equals(moduleCode)) return "Bác sĩ - Đơn thuốc";
    if ("BACSI_PROFILE".equals(moduleCode)) return "Bác sĩ - Hồ sơ cá nhân";
    if ("GUEST".equals(moduleCode)) return "Khách - Đặt lịch và mua thuốc";
    if ("DASHBOARD".equals(moduleCode)) return "Dashboard";
    return "Khác - " + moduleCode;
  }

  private boolean laModuleDuocHoTro(String moduleCode) {
    if (moduleCode == null || moduleCode.isEmpty()) {
      return false;
    }
    switch (moduleCode) {
      case "DASHBOARD":
      case "USER":
      case "BACSI":
      case "KHOA":
      case "GOIDICHVU":
      case "ROLE":
      case "PHANQUYEN":
      case "THUOC":
      case "NCC":
      case "PHIEUNHAP":
      case "HOADONTHUOC":
      case "DUYETLICHLAM":
      case "LICHLAMVIEC":
      case "LICHKHAM":
      case "HOADONKHAM":
      case "HOSO":
      case "DONTHUOC":
      case "BACSI_PROFILE":
      case "GUEST":
        return true;
      default:
        return false;
    }
  }

  private void khoiTaoDanhMucQuyenCoBan() {
    danhMucQuyen.clear();

    // Quản trị
    addPerm("DASHBOARD", "XEM", "DASHBOARD_XEM", "Dashboard", "Xem dashboard");

    addPerm("USER", "XEM", "USER_XEM", "Quản lý tài khoản", "Xem danh sách");
    addPerm("USER", "THEM", "USER_THEM", "Quản lý tài khoản", "Thêm tài khoản");
    addPerm("USER", "SUA", "USER_SUA", "Quản lý tài khoản", "Sửa tài khoản");
    addPerm("USER", "XOA", "USER_XOA", "Quản lý tài khoản", "Xóa tài khoản");
    addPerm(
      "USER",
      "RESET_MAT_KHAU",
      "USER_RESET_MAT_KHAU",
      "Quản lý tài khoản",
      "Reset mật khẩu"
    );
    addPerm(
      "USER",
      "KICH_HOAT_VO_HIEU_HOA",
      "USER_KICH_HOAT_VO_HIEU_HOA",
      "Quản lý tài khoản",
      "Kích hoạt/Vô hiệu hóa"
    );

    addPerm("BACSI", "XEM", "BACSI_XEM", "Quản lý bác sĩ", "Xem danh sách");
    addPerm("BACSI", "THEM", "BACSI_THEM", "Quản lý bác sĩ", "Thêm bác sĩ");
    addPerm("BACSI", "SUA", "BACSI_SUA", "Quản lý bác sĩ", "Sửa bác sĩ");
    addPerm("BACSI", "XOA", "BACSI_XOA", "Quản lý bác sĩ", "Xóa bác sĩ");
    addPerm(
      "BACSI",
      "XEM_CHI_TIET",
      "BACSI_XEM_CHI_TIET",
      "Quản lý bác sĩ",
      "Xem chi tiết"
    );

    addPerm("KHOA", "XEM", "KHOA_XEM", "Quản lý khoa", "Xem khoa");
    addPerm("KHOA", "THEM", "KHOA_THEM", "Quản lý khoa", "Thêm khoa");
    addPerm("KHOA", "SUA", "KHOA_SUA", "Quản lý khoa", "Sửa khoa");
    addPerm("KHOA", "XOA", "KHOA_XOA", "Quản lý khoa", "Xóa khoa");

    addPerm(
      "GOIDICHVU",
      "XEM",
      "GOIDICHVU_XEM",
      "Quản lý gói dịch vụ",
      "Xem gói"
    );
    addPerm(
      "GOIDICHVU",
      "THEM",
      "GOIDICHVU_THEM",
      "Quản lý gói dịch vụ",
      "Thêm gói"
    );
    addPerm(
      "GOIDICHVU",
      "SUA",
      "GOIDICHVU_SUA",
      "Quản lý gói dịch vụ",
      "Sửa gói"
    );
    addPerm(
      "GOIDICHVU",
      "XOA",
      "GOIDICHVU_XOA",
      "Quản lý gói dịch vụ",
      "Xóa gói"
    );

    addPerm("ROLE", "XEM", "ROLE_XEM", "Quản lý vai trò", "Xem vai trò");
    addPerm("ROLE", "THEM", "ROLE_THEM", "Quản lý vai trò", "Thêm vai trò");
    addPerm("ROLE", "SUA", "ROLE_SUA", "Quản lý vai trò", "Sửa vai trò");
    addPerm("ROLE", "XOA", "ROLE_XOA", "Quản lý vai trò", "Xóa vai trò");

    addPerm(
      "PHANQUYEN",
      "XEM",
      "PHANQUYEN_XEM",
      "Phân quyền chi tiết",
      "Xem ma trận"
    );
    addPerm(
      "PHANQUYEN",
      "CAP_NHAT",
      "PHANQUYEN_CAP_NHAT",
      "Phân quyền chi tiết",
      "Cập nhật quyền"
    );

    // Nhà thuốc
    addPerm(
      "THUOC",
      "XEM",
      "THUOC_XEM",
      "Nhà thuốc - Quản lý thuốc",
      "Xem danh sách thuốc"
    );
    addPerm(
      "THUOC",
      "THEM",
      "THUOC_THEM",
      "Nhà thuốc - Quản lý thuốc",
      "Thêm thuốc"
    );
    addPerm(
      "THUOC",
      "SUA",
      "THUOC_SUA",
      "Nhà thuốc - Quản lý thuốc",
      "Sửa thông tin thuốc"
    );
    addPerm(
      "THUOC",
      "XOA",
      "THUOC_XOA",
      "Nhà thuốc - Quản lý thuốc",
      "Vô hiệu hóa bán"
    );
    addPerm(
      "THUOC",
      "KICH_HOAT",
      "THUOC_KICH_HOAT",
      "Nhà thuốc - Quản lý thuốc",
      "Kích hoạt bán lại"
    );

    addPerm(
      "NCC",
      "XEM",
      "NCC_XEM",
      "Nhà thuốc - Nhà cung cấp",
      "Xem nhà cung cấp"
    );
    addPerm(
      "NCC",
      "THEM",
      "NCC_THEM",
      "Nhà thuốc - Nhà cung cấp",
      "Thêm nhà cung cấp"
    );
    addPerm(
      "NCC",
      "SUA",
      "NCC_SUA",
      "Nhà thuốc - Nhà cung cấp",
      "Sửa nhà cung cấp"
    );
    addPerm(
      "NCC",
      "XOA",
      "NCC_XOA",
      "Nhà thuốc - Nhà cung cấp",
      "Ngưng hợp tác/Hợp tác lại"
    );

    addPerm(
      "PHIEUNHAP",
      "XEM",
      "PHIEUNHAP_XEM",
      "Nhà thuốc - Phiếu nhập",
      "Xem phiếu nhập"
    );
    addPerm(
      "PHIEUNHAP",
      "THEM",
      "PHIEUNHAP_THEM",
      "Nhà thuốc - Phiếu nhập",
      "Tạo phiếu nhập"
    );
    addPerm(
      "PHIEUNHAP",
      "SUA",
      "PHIEUNHAP_SUA",
      "Nhà thuốc - Phiếu nhập",
      "Sửa chi tiết phiếu"
    );
    addPerm(
      "PHIEUNHAP",
      "XOA",
      "PHIEUNHAP_XOA",
      "Nhà thuốc - Phiếu nhập",
      "Hủy phiếu"
    );
    addPerm(
      "PHIEUNHAP",
      "XAC_NHAN_NHAP_KHO",
      "PHIEUNHAP_XAC_NHAN_NHAP_KHO",
      "Nhà thuốc - Phiếu nhập",
      "Xác nhận nhập kho"
    );
    addPerm(
      "PHIEUNHAP",
      "XEM_LO_HSD",
      "PHIEUNHAP_XEM_LO_HSD",
      "Nhà thuốc - Phiếu nhập",
      "Theo dõi lô/HSD"
    );

    addPerm(
      "HOADONTHUOC",
      "XEM",
      "HOADONTHUOC_XEM",
      "Nhà thuốc - Hóa đơn thuốc",
      "Xem hóa đơn"
    );
    addPerm(
      "HOADONTHUOC",
      "THEM",
      "HOADONTHUOC_THEM",
      "Nhà thuốc - Hóa đơn thuốc",
      "Tạo hóa đơn từ đơn thuốc"
    );
    addPerm(
      "HOADONTHUOC",
      "SUA",
      "HOADONTHUOC_SUA",
      "Nhà thuốc - Hóa đơn thuốc",
      "Sửa chi tiết hóa đơn"
    );
    addPerm(
      "HOADONTHUOC",
      "XOA",
      "HOADONTHUOC_XOA",
      "Nhà thuốc - Hóa đơn thuốc",
      "Hủy hóa đơn"
    );
    addPerm(
      "HOADONTHUOC",
      "XAC_NHAN_THANH_TOAN",
      "HOADONTHUOC_XAC_NHAN_THANH_TOAN",
      "Nhà thuốc - Hóa đơn thuốc",
      "Xác nhận thanh toán"
    );
    addPerm(
      "HOADONTHUOC",
      "XAC_NHAN_GIAO_THUOC",
      "HOADONTHUOC_XAC_NHAN_GIAO_THUOC",
      "Nhà thuốc - Hóa đơn thuốc",
      "Xác nhận giao thuốc"
    );
    addPerm(
      "HOADONTHUOC",
      "XEM_XUAT_THEO_LO",
      "HOADONTHUOC_XEM_XUAT_THEO_LO",
      "Nhà thuốc - Hóa đơn thuốc",
      "Xem xuất theo lô"
    );

    // Bác sĩ
    addPerm(
      "LICHLAMVIEC",
      "XEM",
      "LICHLAMVIEC_XEM",
      "Bác sĩ - Lịch làm việc (đăng ký ca)",
      "Xem lịch làm việc"
    );
    addPerm(
      "LICHLAMVIEC",
      "THEM",
      "LICHLAMVIEC_THEM",
      "Bác sĩ - Lịch làm việc (đăng ký ca)",
      "Đăng ký lịch làm"
    );
    addPerm(
      "DUYETLICHLAM",
      "XEM",
      "DUYETLICHLAM_XEM",
      "Admin - Duyệt lịch làm bác sĩ",
      "Xem danh sách chờ duyệt"
    );
    addPerm(
      "LICHLAMVIEC",
      "DUYET",
      "LICHLAMVIEC_DUYET",
      "Admin - Duyệt lịch làm bác sĩ",
      "Duyệt lịch làm"
    );
    addPerm(
      "LICHLAMVIEC",
      "TU_CHOI",
      "LICHLAMVIEC_TU_CHOI",
      "Admin - Duyệt lịch làm bác sĩ",
      "Từ chối lịch làm"
    );

    addPerm(
      "LICHKHAM",
      "XEM",
      "LICHKHAM_XEM",
      "Bác sĩ - Lịch khám",
      "Xem lịch khám"
    );
    addPerm(
      "LICHKHAM",
      "SUA",
      "LICHKHAM_SUA",
      "Bác sĩ - Lịch khám",
      "Cập nhật trạng thái"
    );
    addPerm(
      "LICHKHAM",
      "HUY",
      "LICHKHAM_HUY",
      "Bác sĩ - Lịch khám",
      "Hủy lịch khám"
    );

    addPerm("HOSO", "XEM", "HOSO_XEM", "Bác sĩ - Bệnh án", "Xem bệnh án");
    addPerm("HOSO", "THEM", "HOSO_THEM", "Bác sĩ - Bệnh án", "Thêm bệnh án");
    addPerm("HOSO", "SUA", "HOSO_SUA", "Bác sĩ - Bệnh án", "Sửa bệnh án");

    addPerm(
      "DONTHUOC",
      "XEM",
      "DONTHUOC_XEM",
      "Bác sĩ - Đơn thuốc",
      "Xem đơn thuốc"
    );
    addPerm(
      "DONTHUOC",
      "THEM",
      "DONTHUOC_THEM",
      "Bác sĩ - Đơn thuốc",
      "Tạo đơn thuốc"
    );
    addPerm(
      "DONTHUOC",
      "SUA",
      "DONTHUOC_SUA",
      "Bác sĩ - Đơn thuốc",
      "Sửa đơn thuốc"
    );

    addPerm(
      "HOADONKHAM",
      "XEM",
      "HOADONKHAM_XEM",
      "Bác sĩ - Hóa đơn khám",
      "Xem hóa đơn khám"
    );
    addPerm(
      "HOADONKHAM",
      "THEM",
      "HOADONKHAM_THEM",
      "Bác sĩ - Hóa đơn khám",
      "Tạo hóa đơn khám"
    );
    addPerm(
      "HOADONKHAM",
      "SUA",
      "HOADONKHAM_SUA",
      "Bác sĩ - Hóa đơn khám",
      "Sửa hóa đơn khám"
    );
    addPerm(
      "HOADONKHAM",
      "HUY",
      "HOADONKHAM_HUY",
      "Bác sĩ - Hóa đơn khám",
      "Hủy hóa đơn khám"
    );

    addPerm(
      "BACSI_PROFILE",
      "XEM",
      "BACSI_PROFILE_XEM",
      "Bác sĩ - Hồ sơ cá nhân",
      "Xem hồ sơ"
    );
    addPerm(
      "BACSI_PROFILE",
      "DOI_MAT_KHAU",
      "BACSI_PROFILE_DOI_MAT_KHAU",
      "Bác sĩ - Hồ sơ cá nhân",
      "Đổi mật khẩu"
    );

    // Khách
    addPerm(
      "GUEST",
      "DAT_LICH",
      "GUEST_DAT_LICH",
      "Khách - Đặt lịch và mua thuốc",
      "Đặt lịch khám"
    );
    addPerm(
      "GUEST",
      "MUA_THUOC",
      "GUEST_MUA_THUOC",
      "Khách - Đặt lịch và mua thuốc",
      "Mua thuốc"
    );
    addPerm(
      "GUEST",
      "TRA_CUU_HO_SO",
      "GUEST_TRA_CUU_HO_SO",
      "Khách - Đặt lịch và mua thuốc",
      "Tra cứu hồ sơ"
    );
  }

  private void addPerm(
    String moduleCode,
    String actionCode,
    String maQuyen,
    String panelName,
    String displayName
  ) {
    danhMucQuyen.add(
      new PermissionItem(
        moduleCode,
        actionCode,
        maQuyen,
        panelName,
        displayName
      )
    );
  }

  private void apDungPhanQuyenHanhDong() {
    coQuyenXem = Session.coMotTrongCacQuyen("PHANQUYEN_XEM");
    coQuyenCapNhat = Session.coMotTrongCacQuyen("PHANQUYEN_CAP_NHAT");

    if (cbRole != null) cbRole.setEnabled(coQuyenXem);
    if (btnTaiLai != null) btnTaiLai.setVisible(coQuyenXem);
    if (btnLuu != null) btnLuu.setVisible(coQuyenCapNhat);
    if (lblLoading != null) lblLoading.setVisible(false);
    if (permissionContainer != null) permissionContainer.setEnabled(coQuyenXem);
    capNhatTrangThaiNutLuu();
  }

  private int thuTuNhomTheoVaiTro(String tenNhom) {
    String key = safe(tenNhom);

    if (
      "Dashboard".equals(key) ||
      key.startsWith("Quản lý") ||
      key.startsWith("Admin -") ||
      "Phân quyền chi tiết".equals(key)
    ) {
      return 1;
    }
    if (key.startsWith("Bác sĩ -")) {
      return 2;
    }
    if (key.startsWith("Nhà thuốc -")) {
      return 3;
    }
    if (key.startsWith("Khách -")) {
      return 4;
    }
    return 5;
  }

  private static class LoadResult {

    private final boolean schemaHopLe;
    private final ArrayList<RolesDTO> roles;
    private final Map<Integer, Set<String>> roleQuyen;

    private LoadResult(
      boolean schemaHopLe,
      ArrayList<RolesDTO> roles,
      Map<Integer, Set<String>> roleQuyen
    ) {
      this.schemaHopLe = schemaHopLe;
      this.roles = roles == null ? new ArrayList<>() : roles;
      this.roleQuyen = roleQuyen == null ? new HashMap<>() : roleQuyen;
    }

    private static LoadResult schemaKhongHopLe() {
      return new LoadResult(false, null, null);
    }

    private static LoadResult hopLe(
      ArrayList<RolesDTO> roles,
      Map<Integer, Set<String>> roleQuyen
    ) {
      return new LoadResult(true, roles, roleQuyen);
    }
  }

  private int parseRoleId(String text) {
    try {
      return Integer.parseInt(text == null ? "" : text.trim());
    } catch (Exception ex) {
      return -1;
    }
  }

  private String safe(String value) {
    return value == null ? "" : value;
  }

  private static class RoleOption {

    private final int roleId;
    private final String roleName;

    private RoleOption(int roleId, String roleName) {
      this.roleId = roleId;
      this.roleName = roleName;
    }

    @Override
    public String toString() {
      return roleId + " - " + roleName;
    }
  }

  private static class PermissionItem {

    private final String moduleCode;
    private final String actionCode;
    private final String maQuyen;
    private final String panelName;
    private final String displayName;

    private PermissionItem(
      String moduleCode,
      String actionCode,
      String maQuyen,
      String panelName,
      String displayName
    ) {
      this.moduleCode = moduleCode;
      this.actionCode = actionCode;
      this.maQuyen = maQuyen;
      this.panelName = panelName;
      this.displayName = displayName;
    }
  }
}
