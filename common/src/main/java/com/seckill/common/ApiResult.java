package com.seckill.common;

public class ApiResult<T> {
    private final int code; private final String message; private final T data;
    private ApiResult(int code, String message, T data) { this.code = code; this.message = message; this.data = data; }
    public static <T> ApiResult<T> ok(T data) { return new ApiResult<>(200, "success", data); }
    public static <T> ApiResult<T> accepted(T data) { return new ApiResult<>(202, "request accepted", data); }
    public static <T> ApiResult<T> fail(int code, String message) { return new ApiResult<>(code, message, null); }
    public int getCode() { return code; } public String getMessage() { return message; } public T getData() { return data; }
}
