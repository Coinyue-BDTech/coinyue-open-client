package com.coinyue.open.client.model.resp;

import java.util.Map;

/**
 * 查询关联ID响应。
 */
public class SpRefIdQueryResp {
    /** 业务返回码，0 表示成功 */
    public int retCode;
    /** 业务返回消息 */
    public String retMsg;
    /** 消息类型 */
    public String msgType;
    /** refStr -> assignId 映射 */
    public Map<String, String> result;
}
