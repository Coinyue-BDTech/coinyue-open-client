package com.coinyue.open.client.model.req;

import java.util.List;

/**
 * 查询关联ID请求。
 */
public class SpRefIdQueryReq {
    /** 消息类型，固定为类名 */
    public String msgType = "SpRefIdQueryReq";
    /** 对象类型 */
    public String objType;
    /** 关联字符串列表 */
    public List<String> refStrs;
}
