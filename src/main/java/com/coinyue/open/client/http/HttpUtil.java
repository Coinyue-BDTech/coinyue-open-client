package com.coinyue.open.client.http;

import com.coinyue.open.client.exception.CoinyueOpenException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * HTTP POST 工具，基于 JDK 21 内置 HttpClient。
 */
public class HttpUtil {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    /**
     * 发送 HTTP POST 请求。
     *
     * @param url     目标 URL
     * @param headers 自定义 Header（Bearer、Model 等）
     * @param body    请求体（纯文本）
     * @return 响应体文本
     */
    public static String post(String url, Map<String, String> headers, String body)
            throws CoinyueOpenException {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(DEFAULT_TIMEOUT)
                    .build();

            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(DEFAULT_TIMEOUT)
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body));

            // 添加自定义 Header
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    reqBuilder.header(entry.getKey(), entry.getValue());
                }
            }

            HttpResponse<String> response = client.send(
                    reqBuilder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new CoinyueOpenException(response.statusCode(),
                        "HTTP 请求失败: " + response.statusCode() + " " + response.body());
            }

            return response.body();
        } catch (CoinyueOpenException e) {
            throw e;
        } catch (Exception e) {
            throw new CoinyueOpenException(-1, "HTTP 请求异常: " + e.getMessage(), e);
        }
    }
}
