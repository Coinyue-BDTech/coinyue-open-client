package com.coinyue.open.client.mcp;

import com.coinyue.open.client.CoinyueOpenClient;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

/**
 * MCP STDIO 模式启动入口。
 * <p>
 * 通过 STDIN/STDOUT 与 Agent（Claude Code、Cursor 等）通信。
 * </p>
 *
 * <h3>环境变量配置</h3>
 * <ul>
 *   <li>COINYUE_BEARER — Bearer 令牌（必填）</li>
 *   <li>COINYUE_MODEL — Model 名称（可选，默认 default）</li>
 *   <li>COINYUE_SECRET_KEY — AES 密钥，Base64 编码（必填）</li>
 *   <li>COINYUE_API_URL — API 地址（必填）</li>
 * </ul>
 *
 * <h3>Agent 配置示例</h3>
 * <pre>{@code
 * {
 *   "mcpServers": {
 *     "coinyue-open": {
 *       "command": "java",
 *       "args": ["-jar", "/path/to/coinyue-open-client-mcp.jar"],
 *       "env": {
 *         "COINYUE_BEARER": "sk-63-xxx-1024",
 *         "COINYUE_SECRET_KEY": "6WuSRZwLIsYiK/RWZTCKDA==",
 *         "COINYUE_API_URL": "https://smsa.coinyue.com/open"
 *       }
 *     }
 *   }
 * }
 * }</pre>
 */
public class McpStdioLauncher {

    public static void main(String[] args) {
        // 1. 从环境变量读取配置
        String bearer = requireEnv("COINYUE_BEARER");
        String model = System.getenv().getOrDefault("COINYUE_MODEL", "default");
        String secretKey = requireEnv("COINYUE_SECRET_KEY");
        String apiUrl = requireEnv("COINYUE_API_URL");

        // 2. 创建客户端
        CoinyueOpenClient client = new CoinyueOpenClient(bearer, model, secretKey, apiUrl);

        // 3. 创建 STDIO 传输层
        McpServerTransportProvider transportProvider = new StdioServerTransportProvider(
                McpJsonMapper.getDefault()
        );

        // 4. 构建 MCP Server 并注册所有 Tool
        McpSyncServer server = McpServer.sync(transportProvider)
                .serverInfo("coinyue-open-client", "1.0.0")
                .tools(McpTools.allTools(client))
                .build();

        // 5. 启动（阻塞直到 JVM 被关闭）
        System.err.println("[coinyue-open-client] MCP Server 已启动，等待 Agent 连接...");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[coinyue-open-client] MCP Server 被中断");
        } finally {
            server.close();
        }
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("环境变量 " + name + " 未设置");
        }
        return value;
    }
}
