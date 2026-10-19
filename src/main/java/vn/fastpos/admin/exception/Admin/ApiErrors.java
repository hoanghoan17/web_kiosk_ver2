package vn.fastpos.admin.exception.Admin;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiErrors {
  @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
  ResponseEntity<?> validation(org.springframework.web.bind.MethodArgumentNotValidException e) {
    return ResponseEntity.badRequest()
        .body(
            Map.of(
                "message",
                e.getBindingResult().getFieldErrors().stream()
                    .map(f -> f.getField() + ": " + f.getDefaultMessage())
                    .collect(java.util.stream.Collectors.joining("; "))));
  }

  @ExceptionHandler({
    org.springframework.orm.ObjectOptimisticLockingFailureException.class,
    org.springframework.dao.CannotAcquireLockException.class
  })
  ResponseEntity<?> concurrent() {
    return ResponseEntity.status(409)
        .body(Map.of("message", "Dữ liệu đang được thao tác ở màn hình khác. Tải lại và thử lại."));
  }

  @ExceptionHandler(ApiException.class)
  ResponseEntity<?> business(ApiException e) {
    return ResponseEntity.status(e.status).body(Map.of("message", e.getMessage()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<?> integrity() {
    return ResponseEntity.status(409)
        .body(
            Map.of(
                "message",
                "Dữ liệu trùng hoặc đang được tham chiếu. Kiểm tra mã, danh mục và món thành"
                    + " phần."));
  }

  @ExceptionHandler({
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class
  })
  ResponseEntity<?> input() {
    return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu gửi lên không hợp lệ."));
  }
}
