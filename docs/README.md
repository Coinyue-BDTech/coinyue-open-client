# coinyue-open-client

葵圆 OpenAPI 客户端 SDK + MCP Server，用于第三方平台对接 cy-wild 开放接口。

## 定位

本项目提供两种使用方式：

| 方式 | 适用场景 | 要求 |
|------|---------|------|
| **SDK** | Java 开发者通过代码调用 | JDK 21+，Maven |
| **MCP Server** | AI Agent（Claude Code、Cursor 等）通过自然语言调用 | JDK 21+，Agent 支持 MCP |

## SDK 使用指南

### 1. 引入依赖

将 `coinyue-open-client` jar 加入项目 classpath：

```xml
<dependency>
    <groupId>com.coinyue.open</groupId>
    <artifactId>coinyue-open-client</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

或直接引用 jar 文件。

### 2. 快速开始

```java
import com.coinyue.open.client.CoinyueOpenClient;
import com.coinyue.open.client.model.resp.SpMerchantInfoResp;

public class Demo {
    public static void main(String[] args) throws Exception {
        // 创建客户端
        CoinyueOpenClient client = new CoinyueOpenClient(
            "sk-63-你的Bearer令牌",
            "你的AES密钥(Base64)",        // 如 "6WuSRZwLIsYiK/RWZTCKDA=="
            "https://smsa.coinyue.com/open"
        );

        // 调用接口
        SpMerchantInfoResp resp = client.getMerchantInfo();
        System.out.println("商户名: " + resp.info.name);
        System.out.println("开放线路: " + resp.info.openLines.size() + " 条");
    }
}
```

### 3. 构造参数说明

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `bearer` | String | 是 | Bearer 令牌，联系平台管理员获取 |
| `model` | String | 否（默认 `default`） | Model 名称，与 BMH 的 modelCid 关联 |
| `secretKey` | String | 是 | AES 密钥（Base64 编码，16 字节） |
| `apiUrl` | String | 是 | API 地址，如 `https://smsa.coinyue.com/open` |

### 4. 可用 API 列表

| 方法 | 说明 | 返回类型 |
|------|------|---------|
| `getMerchantInfo()` | 查询代售商户信息 | `SpMerchantInfoResp` |

## MCP 使用指南

### 什么是 MCP？

MCP（Model Context Protocol）是 Anthropic 提出的一种协议，允许 AI Agent 通过标准化的方式调用外部工具。配置后，Agent 可以直接通过自然语言调用葵圆 OpenAPI。

### 1. 打包 MCP Server

```bash
cd coinyue-open-client
mvn clean package
```

生成的可执行 jar：`target/coinyue-open-client-mcp.jar`

### 2. Agent 配置

#### Claude Code（CLI）

在 `.claude/settings.json` 中配置：

```json
{
  "mcpServers": {
    "coinyue-open": {
      "command": "java",
      "args": ["-jar", "/path/to/coinyue-open-client-mcp.jar"],
      "env": {
        "COINYUE_BEARER": "sk-63-xxx-1024",
        "COINYUE_MODEL": "ajer@ybs",
        "COINYUE_SECRET_KEY": "6WuSRZwLIsYiK/RWZTCKDA==",
        "COINYUE_API_URL": "https://smsa.coinyue.com/open"
      }
    }
  }
}
```

#### Claude Desktop

在 `mcp_config.json` 中配置（格式同上）。

#### Cursor / Cline / 其他 MCP 客户端

参照相同 JSON 格式配置 `mcpServers`。

### 3. 环境变量说明

| 变量 | 必填 | 说明 |
|------|------|------|
| `COINYUE_BEARER` | 是 | Bearer 令牌 |
| `COINYUE_MODEL` | 否 | Model 名称（默认 `default`） |
| `COINYUE_SECRET_KEY` | 是 | AES 密钥（Base64 编码） |
| `COINYUE_API_URL` | 是 | API 地址 |

> 敏感信息通过环境变量传递，不会出现在进程列表中。

### 4. 可用 Tool 列表

| Tool 名称 | 说明 | 参数 |
|-----------|------|------|
| `getMerchantInfo` | 查询葵圆 OpenAPI 代售商户信息 | 无 |

### 5. Agent 对话示例

```
用户: 帮我查询一下当前商户的信息

Agent: [调用 getMerchantInfo 工具]

Agent: 当前商户信息如下：
  - 名称：公开测试
  - IP 限制：已启用
  - 开放线路：2 条
    - 广州-深圳（ID: 12345）
    - 广州-珠海（ID: 12346）
```

## 常见问题

### Q: 支持 JDK 1.7 吗？

不支持。本项目最低要求 JDK 21，主要因为 MCP SDK 要求 Java 17+。

### Q: 如何获取 Bearer 和 secretKey？

联系葵圆平台管理员获取商户凭证。

### Q: HTTPS 证书验证失败怎么办？

确保运行环境信任 `smsa.coinyue.com` 的证书。如为自签名证书测试环境，需额外配置信任。

### Q: 未来会增加哪些 API？

根据业务需求逐步增加，每增加一个 API 会同步更新本 SDK 和 MCP Tool。
