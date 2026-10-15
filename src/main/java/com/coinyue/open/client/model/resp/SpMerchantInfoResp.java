package com.coinyue.open.client.model.resp;

/**
 * 商户信息查询响应（业务层）。
 */
public class SpMerchantInfoResp {
    /** 业务返回码，0 表示成功 */
    public int retCode;
    /** 业务返回消息 */
    public String retMsg;
    /** 消息类型 */
    public String msgType;
    /** 商户信息 */
    public WSpMerchant_open info;
}
