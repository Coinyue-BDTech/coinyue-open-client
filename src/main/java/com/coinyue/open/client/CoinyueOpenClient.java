package com.coinyue.open.client;

import com.coinyue.open.client.crypto.AesUtil;
import com.coinyue.open.client.exception.CoinyueOpenException;
import com.coinyue.open.client.http.HttpUtil;
import com.coinyue.open.client.model.req.SpMerchantInfoReq;
import com.coinyue.open.client.model.req.SpRefIdQueryReq;
import com.coinyue.open.client.model.resp.GateResp;
import com.coinyue.open.client.model.resp.SpMerchantInfoResp;
import com.coinyue.open.client.model.resp.SpRefIdQueryResp;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * 葵圆 OpenAPI 客户端。
 * <p>
 * 通过 AES 加密 + HTTP POST 与 smsa 网关通信。
 * 可在 SDK 模式下直接调用，也可被 MCP Server 代理调用。
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * CoinyueOpenClient client = new CoinyueOpenClient(
 *     "sk-63-xxx-1024",
 *     "6WuSRZwLIsYiK/RWZTCKDA==",
 *     "https://smsa.coinyue.com/open"
 * );
 * SpMerchantInfoResp resp = client.getMerchantInfo();
 * }</pre>
 */
public class CoinyueOpenClient {

    private static final Gson GSON = new Gson();

    /** Bearer 令牌 */
    private final String bearer;
    /** Model 名称 */
    private final String model;
    /** AES 密钥（Base64 编码） */
    private final String secretKey;
    /** API 地址 */
    private final String apiUrl;

    /**
     * 构造：指定 Bearer、密钥、API 地址（Model 使用默认值）。
     */
    public CoinyueOpenClient(String bearer, String secretKey, String apiUrl) {
        this(bearer, "default", secretKey, apiUrl);
    }

    /**
     * 构造：完整参数。
     *
     * @param bearer     Bearer 令牌，格式 sk-{merIgNum}-{random16}-{encode(code)}-{balanceId}
     * @param model      Model 名称，与 BMH 的 modelCid 关联
     * @param secretKey  AES 密钥（Base64 编码，16 字节）
     * @param apiUrl     API 地址，如 https://smsa.coinyue.com/open
     */
    public CoinyueOpenClient(String bearer, String model, String secretKey, String apiUrl) {
        if (bearer == null || bearer.isBlank()) throw new IllegalArgumentException("bearer 不能为空");
        if (secretKey == null || secretKey.isBlank()) throw new IllegalArgumentException("secretKey 不能为空");
        if (apiUrl == null || apiUrl.isBlank()) throw new IllegalArgumentException("apiUrl 不能为空");
        this.bearer = bearer;
        this.model = model;
        this.secretKey = secretKey;
        this.apiUrl = apiUrl;
    }

    // ============================================================
    //  业务方法
    // ============================================================

    /**
     * 查询代售商户信息。
     *
     * @return 商户信息响应
     */
    public SpMerchantInfoResp getMerchantInfo() throws CoinyueOpenException {
        SpMerchantInfoReq req = new SpMerchantInfoReq();
        return send(req, SpMerchantInfoResp.class);
    }

    /**
     * 查询或创建关联ID。
     * <p>
     * 传入一批 refStrs（如文件路径），返回每个 refStr 对应的 assignId。
     * 如果 refStr 不存在则自动创建。
     * </p>
     *
     * @param objType 对象类型
     * @param refStrs 关联字符串列表
     * @return 关联ID查询响应
     */
    public SpRefIdQueryResp queryRefId(String objType, java.util.List<String> refStrs) throws CoinyueOpenException {
        SpRefIdQueryReq req = new SpRefIdQueryReq();
        req.objType = objType;
        req.refStrs = refStrs;
        return send(req, SpRefIdQueryResp.class);
    }

    // ============================================================
    //  内部通用方法
    // ============================================================

    /**
     * 通用请求发送流程：序列化 → 加密 → HTTP POST → 解密 → 反序列化。
     */
    private <T> T send(Object reqObj, Class<T> respType) throws CoinyueOpenException {
        // 1. 序列化请求
        String reqJson = GSON.toJson(reqObj);

        // 2. 加密
        String postLineEn = AesUtil.encrypt(reqJson, secretKey);

        // 3. 构建 Header 并发送
        Map<String, String> headers = new HashMap<>();
        headers.put("Bearer", bearer);
        headers.put("Model", model);

        String rawBody = HttpUtil.post(apiUrl, headers, postLineEn);

        // 4. 解密响应
        String decryptedJson = AesUtil.decrypt(rawBody, secretKey);

        // 5. 解析网关响应
        GateResp gateResp = GSON.fromJson(decryptedJson, GateResp.class);

        // 6. 检查网关状态
        if (gateResp.gateState != 0) {
            throw new CoinyueOpenException(gateResp.gateState,
                    "网关失败: " + gateResp.gateStateMsg + " (gateState=" + gateResp.gateState + ")");
        }

        // 7. 检查业务状态并返回
        T dto = gateResp.getDto(respType);

        if (dto instanceof SpMerchantInfoResp spResp && spResp.retCode != 0) {
            throw new CoinyueOpenException(0,
                    "业务失败: " + spResp.retMsg + " (retCode=" + spResp.retCode + ")");
        }
        if (dto instanceof SpRefIdQueryResp refIdResp && refIdResp.retCode != 0) {
            throw new CoinyueOpenException(0,
                    "业务失败: " + refIdResp.retMsg + " (retCode=" + refIdResp.retCode + ")");
        }

        return dto;
    }

    // ============================================================
    //  Getter（MCP 模块需要访问这些配置）
    // ============================================================

    public String getBearer() { return bearer; }
    public String getModel() { return model; }
    public String getSecretKey() { return secretKey; }
    public String getApiUrl() { return apiUrl; }
}
