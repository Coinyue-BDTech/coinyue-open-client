package com.coinyue.open.client.exception;

/**
 * 葵圆 OpenAPI 统一异常。
 */
public class CoinyueOpenException extends Exception {

    /**
     * 网关状态码。
     * -1 = SDK 层错误（加密/解密/HTTP 异常）
     *  0 = 网关正常但业务失败（需看 retCode）
     * 正数 = 网关状态码
     */
    private final int gateState;
    private final String detail;

    public CoinyueOpenException(int gateState, String detail) {
        super(detail);
        this.gateState = gateState;
        this.detail = detail;
    }

    public CoinyueOpenException(int gateState, String detail, Throwable cause) {
        super(detail, cause);
        this.gateState = gateState;
        this.detail = detail;
    }

    public CoinyueOpenException(String detail, Throwable cause) {
        this(-1, detail, cause);
    }

    public int getGateState() {
        return gateState;
    }

    public String getDetail() {
        return detail;
    }
}
