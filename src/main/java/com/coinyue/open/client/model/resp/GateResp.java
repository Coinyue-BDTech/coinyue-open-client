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
    /** 业务层数据（原始 JSON 对象） */
    public Object dto;

    /**
     * 将 dto 反序列化为指定类型。
     */
    public <T> T getDto(Class<T> clazz) {
        if (dto == null) return null;
        if (clazz.isInstance(dto)) return clazz.cast(dto);
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String json = gson.toJson(dto);
        return gson.fromJson(json, clazz);
    }
}
