package com.coinyue.open.client.mcp;

import com.coinyue.open.client.CoinyueOpenClient;
import com.coinyue.open.client.exception.CoinyueOpenException;
import com.coinyue.open.client.model.resp.SpMerchantInfoResp;
import com.coinyue.open.client.model.resp.SpRefIdQueryResp;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP 工具定义。
 * 每个 API 对应一个 Tool，注册到 MCP Server 中。
 */
public class McpTools {

    /**
     * 构建 getMerchantInfo 工具定义。
     */
    public static McpServerFeatures.SyncToolSpecification getMerchantInfoTool(CoinyueOpenClient client) {
        McpSchema.Tool tool = McpSchema.Tool.builder()
                .name("getMerchantInfo")
                .description("查询葵圆 OpenAPI 代售商户信息，包括商户名称、IP 限制、开放线路列表等")
                .inputSchema(new McpSchema.JsonSchema("object", Map.of(), List.of(), null, null, null))
                .build();

        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {
                    try {
                        SpMerchantInfoResp resp = client.getMerchantInfo();
                        String json = String.format(
                                "{\"retCode\":%d,\"retMsg\":\"%s\",\"info\":{\"name\":\"%s\",\"tokenIpLimit\":%s,\"tokenIpList\":\"%s\",\"notityUrl\":\"%s\",\"vers\":%d,\"openLines\":%s}}",
                                resp.retCode,
                                escapeJson(resp.retMsg),
                                escapeJson(resp.info.name),
                                resp.info.tokenIpLimit,
                                escapeJson(resp.info.tokenIpList),
                                escapeJson(resp.info.notityUrl),
                                resp.info.vers,
                                formatOpenLines(resp.info.openLines)
                        );
                        return new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(json)),
                                false
                        );
                    } catch (CoinyueOpenException e) {
                        String json = String.format(
                                "{\"error\":true,\"gateState\":%d,\"message\":\"%s\"}",
                                e.getGateState(),
                                escapeJson(e.getMessage())
                        );
                        return new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(json)),
                                true
                        );
                    }
                })
                .build();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String formatOpenLines(List<?> lines) {
        if (lines == null || lines.isEmpty()) return "[]";
        List<String> parts = new ArrayList<>();
        for (Object obj : lines) {
            parts.add(String.valueOf(obj));
        }
        return String.join(",", parts);
    }

    private static String formatResultMap(Map<String, String> map) {
        if (map == null || map.isEmpty()) return "{}";
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            parts.add(String.format("\"%s\":\"%s\"", escapeJson(entry.getKey()), escapeJson(entry.getValue())));
        }
        return "{" + String.join(",", parts) + "}";
    }

    /**
     * 构建 queryRefId 工具定义。
     */
    public static McpServerFeatures.SyncToolSpecification queryRefIdTool(CoinyueOpenClient client) {
        McpSchema.Tool tool = McpSchema.Tool.builder()
                .name("queryRefId")
                .description("查询或创建关联ID。传入对象类型和一组关联字符串（如文件路径），返回每个字符串对应的唯一assignId。如果关联字符串不存在则自动创建。")
                .inputSchema(new McpSchema.JsonSchema("object",
                        Map.of(
                                "objType", Map.of("type", "string", "description", "对象类型"),
                                "refStrs", Map.of("type", "array", "items", Map.of("type", "string"), "description", "关联字符串列表")
                        ),
                        List.of("objType", "refStrs"),
                        null, null, null))
                .build();

        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {
                    try {
                        String objType = (String) request.arguments().get("objType");
                        @SuppressWarnings("unchecked")
                        List<String> refStrs = (List<String>) request.arguments().get("refStrs");
                        SpRefIdQueryResp resp = client.queryRefId(objType, refStrs);
                        String json = String.format(
                                "{\"retCode\":%d,\"retMsg\":\"%s\",\"result\":%s}",
                                resp.retCode,
                                escapeJson(resp.retMsg),
                                formatResultMap(resp.result)
                        );
                        return new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(json)),
                                false
                        );
                    } catch (CoinyueOpenException e) {
                        String json = String.format(
                                "{\"error\":true,\"gateState\":%d,\"message\":\"%s\"}",
                                e.getGateState(),
                                escapeJson(e.getMessage())
                        );
                        return new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(json)),
                                true
                        );
                    }
                })
                .build();
    }

    /**
     * 获取所有 Tool 定义列表。
     * 未来新增 API 时，在此处追加即可。
     */
    public static List<McpServerFeatures.SyncToolSpecification> allTools(CoinyueOpenClient client) {
        return List.of(
                getMerchantInfoTool(client),
                queryRefIdTool(client)
        );
    }
}
