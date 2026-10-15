package com.coinyue.open.client.model.resp;

/**
 * 网关层响应。
 */
public class GateResp {
    /** 网关状态码，0 表示成功 */
    public int gateState;
    /** 网关状态描述 */
    public String gateStateMsg;
    /** 请求 ID 回传 */
    public String requestId;
    /** 日志 ID */
    public String logId;
    /** 业务层数据 */
    public SpMerchantInfoResp dto;
}
