# Kịch bản thuyết trình chi tiết

## Slide 1 - Mở bài

- Chào hội đồng, giới thiệu đề tài Quản Lý Phòng Khám Bệnh.
- Nhấn mạnh điểm khác biệt: phân quyền RBAC động và luồng phòng khám + nhà thuốc end-to-end.

## Slide 2 - Agenda

- Đi nhanh qua roadmap để người nghe nắm thứ tự nội dung.
- Cam kết cuối buổi sẽ có demo và phần phản biện kỹ thuật.

## Slide 3 - Bối cảnh

- Nêu bài toán thực tế: quản lý rời rạc gây sai sót lịch hẹn, bệnh án và tồn kho.
- Đặt mục tiêu: số hóa đầy đủ chu trình và kiểm soát quyền truy cập.

## Slide 4 - Phạm vi

- Giải thích 4 nhóm người dùng và chức năng cốt lõi của từng nhóm.
- Chốt ý: đây là hệ thống đa vai trò, không phải app đơn chức năng.

## Slide 5 - Kiến trúc

- Mô tả chuỗi GUI -> BUS -> DAO -> DB bằng ví dụ thao tác tạo lịch khám.
- Nhấn mạnh lợi ích tách tầng: dễ bảo trì, dễ mở rộng, ít bug dây chuyền.

## Slide 6 - Cấu trúc package

- Trình bày tổ chức mã nguồn theo domain.
- Nêu cách chia việc nhóm theo package giúp phát triển song song.

## Slide 7 - RBAC động

- Trình bày luồng: login -> nạp quyền -> session -> kiểm tra route/menu.
- Chốt giá trị: đổi quyền trực tiếp trên dữ liệu, không phải sửa code giao diện.

## Slide 8 - Bản đồ quyền

- Nêu các quyền tiêu biểu của Doctor, Pharmacy, Admin.
- Giải thích route đặc biệt cần mode ADMIN.

## Slide 9 - Luồng Guest

- Kể thành câu chuyện người dùng từ chọn gói đến in phiếu đặt lịch.
- Nhấn mạnh bộ lọc bác sĩ theo khoa và lịch làm đã duyệt.

## Slide 10 - Luồng Doctor

- Đi theo trình tự: xác nhận lịch khám -> lập bệnh án -> kê đơn.
- Nêu các rule ngăn thao tác sai trạng thái.

## Slide 11 - Luồng Pharmacy

- Nhấn mạnh nhập kho theo lô và hạn dùng.
- Nêu điểm mạnh truy vết xuất kho bằng CSV khi cần kiểm toán.

## Slide 12 - Luồng Admin

- Dashboard để ra quyết định vận hành.
- Màn hình phân quyền để thay đổi tổ chức quyền nhanh chóng.

## Slide 13 - Thiết kế dữ liệu

- Chia thành nhóm bảng RBAC, khám bệnh, kho và hóa đơn.
- Nêu liên kết dữ liệu xuyên suốt từ lịch khám đến hóa đơn.

## Slide 14 - Ràng buộc dữ liệu

- Trình bày các UNIQUE/CHECK quan trọng.
- Chốt ý: ràng buộc DB + BUS là hai lớp bảo vệ dữ liệu.

## Slide 15 - Điểm nhấn kỹ thuật

- HikariCP, PreparedStatement, FlatLaf, PDF/CSV export.
- Tập trung vào lý do chọn công nghệ thay vì chỉ liệt kê.

## Slide 16 - Đánh giá khách quan

- Nói trước điểm mạnh.
- Sau đó thẳng thắn hạn chế và cách xử lý trong roadmap.

## Slide 17 - Demo plan

- Chạy đúng timeline 15-20 phút.
- Mỗi phân đoạn 1 mục tiêu rõ ràng để hội đồng dễ theo dõi.

## Slide 18 - Phản biện

- Trả lời ngắn, chắc, bám code và kiến trúc đã trình bày.
- Luôn gắn câu trả lời với giá trị vận hành thực tế.

## Slide 19 - Phân công

- Tóm tắt vai trò nhóm: dữ liệu, nghiệp vụ, giao diện, kiểm thử, tài liệu.
- Khẳng định quy trình phối hợp.

## Slide 20 - Kết luận

- Chốt 3 ý: hoàn thiện nghiệp vụ, kiến trúc rõ, có khả năng nâng cấp.
- Kết thúc bằng lời cảm ơn và mời câu hỏi.

## Mẹo trình bày để hay hơn

1. Mỗi slide chỉ nói 40-60 giây, không đọc chữ trên màn hình.
2. Khi nói về RBAC, mở nhanh màn hình phân quyền để tạo ấn tượng.
3. Khi nói về kho, nhấn vào từ khóa so lo, han su dung, truy vet.
4. Ở phần hạn chế, nói giải pháp đi kèm để thể hiện tư duy kỹ thuật.
5. Kết bài lặp lại giá trị: linh hoạt quyền, bám nghiệp vụ, sẵn sàng mở rộng.
