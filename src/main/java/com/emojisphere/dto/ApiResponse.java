package com.emojisphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int status;

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, message, data, 200);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, null);
    }

    public static <T> ApiResponse<T> successMessage(String message) {
        return new ApiResponse<>(true, message, null, 200);
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return new ApiResponse<>(false, message, null, status);
    }
}
