package com.coinyue.open.client.model.resp;

import java.util.ArrayList;
import java.util.List;

/**
 * 代售商户信息。
 */
public class WSpMerchant_open {
    /** 商户名称 */
    public String name;
    /** 是否启用 IP 限制 */
    public boolean tokenIpLimit;
    /** 允许的 IP 列表 */
    public String tokenIpList;
    /** 通知回调 URL */
    public String notityUrl;
    /** 版本号 */
    public int vers;
    /** 开放线路列表 */
    public List<WLine_open> openLines = new ArrayList<>();
}
