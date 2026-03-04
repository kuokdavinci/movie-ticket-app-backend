# Code Review - movie-ticket-app-backend

## Tổng quan
Dự án có cấu trúc rõ (controller/service/repository), đã có unit test cơ bản và JWT auth. Tuy vậy, còn một số điểm quan trọng về bảo mật, tính đúng dữ liệu và khả năng mở rộng cần cải thiện.

## Ưu tiên cao (High Priority)
1. **Lộ dữ liệu nhạy cảm (`password`) trong response/API model**
   - `UserController.register` và `UserController.getCurrentUser` trả trực tiếp entity `User`, có chứa trường `password`.
   - `User.toString()` cũng log cả password.
   - Khuyến nghị: dùng DTO cho response người dùng, đánh dấu `password` với `@JsonProperty(access = WRITE_ONLY)` hoặc tách model request/response.

2. **JWT secret không ổn định và bị in ra log**
   - `JWTService` sinh secret ngẫu nhiên mỗi lần app khởi động => token cũ bị invalid sau restart.
   - Secret còn bị `System.out.println`, gây rủi ro lộ key.
   - Khuyến nghị: đọc secret từ `application.properties`/env var, không log secret.

3. **Race condition khi đặt vé (double booking)**
   - `BookingService.bookTicket` check `existsByShowtimeAndSeat` rồi mới `save`, nếu 2 request đồng thời có thể cùng pass check.
   - Khuyến nghị: đặt unique constraint DB (`showtime_id`, `seat_id`) + xử lý exception conflict, hoặc dùng lock phù hợp.

## Ưu tiên trung bình (Medium Priority)
4. **Logic thời gian showtime sai**
   - `getShowtimesByMovie` dùng `LocalTime.ofSecondOfDay(LocalTime.now().getHour())`.
   - Hàm trên nhận số giây từ đầu ngày, nhưng truyền giờ (0-23), nên filter lệch lớn.
   - Khuyến nghị: dùng `LocalTime.now()` trực tiếp.

5. **`bookTicket` không validate ghế thuộc showtime đang đặt**
   - Seat được tìm bằng `findBySeatNumber(seatNumber)` (không theo showtime), có thể map sai ghế giữa các suất chiếu.
   - Khuyến nghị: query theo cả `showtimeId + seatNumber` hoặc dùng `seatId` scoped theo showtime.

6. **Xử lý lỗi JWT filter còn thiếu**
   - `JwtFilter` gọi parse token trực tiếp, token lỗi format có thể ném exception trước khi xuống chain, dễ ra 500.
   - Khuyến nghị: bắt các exception JWT, clear context và trả 401 nhất quán.

7. **Dùng `Map<String, Object>` cho request body**
   - `BookingController.createBooking` parse cast thủ công `(int) body.get(...)`, dễ lỗi kiểu số từ JSON parser.
   - Khuyến nghị: tạo DTO request với validation annotation (`@NotNull`, `@Min`, ...).

8. **Xóa booking chưa check tồn tại trước khi `deleteById` ở service layer**
   - Controller đang check, nhưng service chưa bảo vệ; dễ lỗi nếu tái sử dụng service từ chỗ khác.
   - Khuyến nghị: service chịu trách nhiệm business invariant, throw exception domain rõ ràng.

## Ưu tiên thấp (Low Priority / Cleanup)
9. **Code dư / không dùng**
   - `BookingService.getAvailableSeats` có biến `movieId` không dùng.
   - Có 2 lớp `SeatDTO` ở package `Models` và `DTOs` (trùng tên, dễ gây nhầm).
   - Khuyến nghị: xóa code thừa và hợp nhất DTO.

10. **Field injection thay vì constructor injection**
    - Nhiều class dùng `@Autowired` field injection.
    - Khuyến nghị: constructor injection để dễ test, immutable dependency, và rõ ràng hơn.

11. **CORS mở toàn cục `*` cho controller**
    - Có thể phù hợp dev, nhưng production nên giới hạn domain tin cậy.

12. **API contract chưa nhất quán**
    - `/login` hiện trả string "Failed" thay vì mã HTTP rõ ràng.
    - Khuyến nghị: trả `ResponseEntity` + payload chuẩn lỗi/success.

## Đề xuất roadmap ngắn
1. Bảo mật dữ liệu user + JWT secret (ngay lập tức).
2. Chặn double booking ở mức DB + sửa validate seat/showtime.
3. Chuẩn hóa request/response DTO + exception handler toàn cục (`@RestControllerAdvice`).
4. Refactor DI và cleanup DTO trùng.
