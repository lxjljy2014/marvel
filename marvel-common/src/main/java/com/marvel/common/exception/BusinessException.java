package com.marvel.common.exception;

import lombok.Getter;

import java.io.Serializable;

/**
 * 业务异常，由全局异常处理器统一转换为 R.fail。
 */
@Getter
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int code;

    public BusinessException(String message) {
        this(500, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
