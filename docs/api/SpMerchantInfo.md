# 商户信息查询接口（SpMerchantInfoReq）

查询代售商户的基本信息，包括商户名称、IP 限制配置、开放线路列表等。

## 接口说明

| 项目 | 说明 |
|------|------|
| SDK 方法 | `CoinyueOpenClient.getMerchantInfo()` |
| MCP Tool | `getMerchantInfo` |
| 请求参数 | 无 |
| 返回类型 | `SpMerchantInfoResp` |

## SDK 调用方式

```java
CoinyueOpenClient client = new CoinyueOpenClient(
    "sk-63-你的Bearer",
    "你的密钥",
    "https://smsa.coinyue.com/open"
);

SpMerchantInfoResp resp = client.getMerchantInfo();
System.out.println("商户名: " + resp.info.name);
```

## MCP 调用方式

在 Agent 对话中直接提问：

```
用户: 查询当前商户信息
```

Agent 会自动调用 `getMerchantInfo` 工具并返回结果。

### Agent 返回示例

```json
{
  "retCode": 0,
  "retMsg": "成功",
  "info": {
    "name": "公开测试",
    "tokenIpLimit": true,
    "tokenIpList": "1.2.3.4,5.6.7.8",
    "notityUrl": "https://abc.coinyue.com/a/b/c",
    "vers": 1,
    "openLines": [
      {
        "lineId": 12345,
        "name": "广州-深圳",
        "forbidKid": false,
        "forceRealName": true,
        "startCity": {"cityName": "广州", "cityCode": "440100"},
        "endCity": {"cityName": "深圳", "cityCode": "440300"}
      }
    ]
  }
}
```

## 响应字段说明

### SpMerchantInfoResp（业务层）

| 字段 | 类型 | 说明 |
|------|------|------|
| `retCode` | int | 业务返回码，0 表示成功 |
| `retMsg` | String | 业务返回消息 |
| `msgType` | String | 消息类型，固定 `SpMerchantInfoResp` |
| `info` | WSpMerchant_open | 商户信息对象 |

### WSpMerchant_open（商户信息）

| 字段 | 类型 | 说明 |
|------|------|------|
| `name` | String | 商户名称 |
| `tokenIpLimit` | boolean | 是否启用 IP 限制 |
| `tokenIpList` | String | 允许的 IP 列表（逗号分隔） |
| `notityUrl` | String | 通知回调 URL |
| `vers` | int | 版本号 |
| `openLines` | List\<WLine_open\> | 开放线路列表 |

### WLine_open（线路信息）

| 字段 | 类型 | 说明 |
|------|------|------|
| `lineId` | long | 线路 ID |
| `name` | String | 线路名称 |
| `forbidKid` | boolean | 是否禁止儿童票 |
| `forceRealName` | boolean | 是否强制实名制 |
| `startCity` | WCity_open | 起点城市 |
| `endCity` | WCity_open | 终点城市 |

### WCity_open（城市信息）

| 字段 | 类型 | 说明 |
|------|------|------|
| `cityName` | String | 城市名称 |
| `cityCode` | String | 城市编码 |

## 错误码说明

### retCode（业务层错误码）

| 值 | 说明 |
|----|------|
| 0 | 成功 |
| 其他 | 业务错误，详见 retMsg |

### GateState（网关层状态码）

| 值 | 说明 |
|----|------|
| 0 | 网关成功 |
| -1 | SDK 层错误（加密/解密/HTTP 异常） |
| -4 | 缺少 Bearer/Model Header |
| -5 | 数据解码异常 |
| -2000 | 持有者受限（Bearer 无效） |
| -3000 | 未知错误 |
