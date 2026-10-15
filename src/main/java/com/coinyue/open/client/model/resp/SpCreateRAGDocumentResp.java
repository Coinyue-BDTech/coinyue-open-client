package com.coinyue.open.client.model.resp;

/**
 * 创建RAG文档响应。
 */
public class SpCreateRAGDocumentResp {
    /** 业务返回码，0 表示成功 */
    public int retCode;
    /** 业务返回消息 */
    public String retMsg;
    /** 消息类型 */
    public String msgType;
    /** 文档标识（identify） */
    public String identify;
}
