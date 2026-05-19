package com.campus.market.common;

/**
 * BusinessException 业务组件。
 *
 * @author 阿德
 * @date 2026/05/17
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
