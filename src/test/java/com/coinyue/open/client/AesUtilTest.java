package com.coinyue.open.client;

import com.coinyue.open.client.crypto.AesUtil;
import com.coinyue.open.client.exception.CoinyueOpenException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AES 加解密单元测试。
 * 验证加解密往返一致性。
 */
public class AesUtilTest {

    /** 与 Tester.java 中使用的相同密钥 */
    private static final String TEST_KEY = "6WuSRZwLIsYiK/RWZTCKDA==";

    @Test
    public void testEncryptDecryptRoundTrip() throws CoinyueOpenException {
        String original = "{\"msgType\":\"SpMerchantInfoReq\"}";

        // 加密
        String encrypted = AesUtil.encrypt(original, TEST_KEY);
        assertNotNull("加密结果不应为空", encrypted);
        assertTrue("加密结果应包含 Base64 字符", encrypted.matches("[A-Za-z0-9+/=]+"));

        // 解密
        String decrypted = AesUtil.decrypt(encrypted, TEST_KEY);
        assertEquals("解密结果应与原文一致", original, decrypted);
    }

    @Test
    public void testEncryptProducesDifferentOutputEachTime() throws CoinyueOpenException {
        String original = "hello world";

        // 两次加密结果应不同（因为 IV 随机）
        String enc1 = AesUtil.encrypt(original, TEST_KEY);
        String enc2 = AesUtil.encrypt(original, TEST_KEY);
        assertNotEquals("每次加密的 IV 不同，结果应不同", enc1, enc2);

        // 但解密结果应相同
        assertEquals("解密结果应一致", original, AesUtil.decrypt(enc1, TEST_KEY));
        assertEquals("解密结果应一致", original, AesUtil.decrypt(enc2, TEST_KEY));
    }

    @Test
    public void testDecryptInvalidInput() {
        try {
            AesUtil.decrypt("not-valid-base64!!", TEST_KEY);
            fail("无效 Base64 应抛异常");
        } catch (Exception e) {
            // 预期行为
        }
    }

    @Test
    public void testDecryptTooShortData() throws CoinyueOpenException {
        // Base64 编码后长度不足 16 字节
        String shortData = AesUtil.encrypt("x", TEST_KEY).substring(0, 4);
        try {
            AesUtil.decrypt(shortData, TEST_KEY);
            fail("数据过短应抛异常");
        } catch (Exception e) {
            // 预期行为
        }
    }

    @Test
    public void testDecryptWithWrongKey() throws CoinyueOpenException {
        String original = "secret message";
        String encrypted = AesUtil.encrypt(original, TEST_KEY);

        // 用错误的密钥解密
        String wrongKey = "AAAAAAAAAAAAAAAAAAAAAA==";
        try {
            AesUtil.decrypt(encrypted, wrongKey);
            fail("错误密钥应抛异常");
        } catch (Exception e) {
            // 预期行为
        }
    }

    @Test
    public void testChineseCharacters() throws CoinyueOpenException {
        String original = "商户名称：公开测试";
        String encrypted = AesUtil.encrypt(original, TEST_KEY);
        String decrypted = AesUtil.decrypt(encrypted, TEST_KEY);
        assertEquals("中文字符应正确加解密", original, decrypted);
    }
}
