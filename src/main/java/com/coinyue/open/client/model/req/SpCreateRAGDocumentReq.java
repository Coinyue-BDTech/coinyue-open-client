package com.coinyue.open.client.model.req;

/**
 * 创建RAG文档请求。
 */
public class SpCreateRAGDocumentReq {
    /** 消息类型，固定为类名 */
    public String msgType = "SpCreateRAGDocumentReq";
    /** 文档相对路径，如 a/b/c.md */
    public String documentPath;
    /** Markdown 内容 */
    public String markdown;
}
