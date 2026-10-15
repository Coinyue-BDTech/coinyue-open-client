package com.coinyue.open.client.model.resp;

/**
 * 线路信息。
 */
public class WLine_open {
    /** 线路 ID */
    public long lineId;
    /** 线路名称 */
    public String name;
    /** 是否禁止儿童票 */
    public boolean forbidKid;
    /** 是否强制实名 */
    public boolean forceRealName;
    /** 起点城市 */
    public WCity_open startCity;
    /** 终点城市 */
    public WCity_open endCity;
}
