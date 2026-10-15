package com.coinyue.open.client.crypto;

import com.coinyue.open.client.exception.CoinyueOpenException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES/CBC/PKCS5Padding 加解密工具。
 * 与服务端 OpenApiUtil 完全兼容。
 * 格式：Base64(IV[16字节] || Ciphertext)
 */
public class AesUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_TYPE = "AES";
    private static final int IV_LENGTH = 16;

    /**
     * 加密：明文 → Base64(IV || Ciphertext)
     *
     * @param plaintext 明文字符串
     * @param base64Key Base64 编码的 AES 密钥（16 字节）
     * @return Base64 编码的加密结果
     */
    public static String encrypt(String plaintext, String base64Key) throws CoinyueOpenException {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, KEY_TYPE);

            // 生成随机 IV
            byte[] iv = new byte[IV_LENGTH];
            java.security.SecureRandom.getInstanceStrong().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] cipherBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // IV + 密文拼接
            byte[] combined = new byte[IV_LENGTH + cipherBytes.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(cipherBytes, 0, combined, IV_LENGTH, cipherBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new CoinyueOpenException("AES 加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解密：Base64(IV || Ciphertext) → 明文
     *
     * @param base64Ciphertext Base64 编码的加密数据（含 IV 前缀）
     * @param base64Key        Base64 编码的 AES 密钥（16 字节）
     * @return 解密后的明文字符串
     */
    public static String decrypt(String base64Ciphertext, String base64Key) throws CoinyueOpenException {
        try {
            byte[] combined = Base64.getDecoder().decode(base64Ciphertext);
            if (combined.length < IV_LENGTH) {
                throw new CoinyueOpenException(-1, "密文数据过短，无法解密");
            }

            // 分离 IV 和密文
            byte[] iv = new byte[IV_LENGTH];
            byte[] cipherBytes = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, cipherBytes, 0, cipherBytes.length);

            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, KEY_TYPE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] plainBytes = cipher.doFinal(cipherBytes);

            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (CoinyueOpenException e) {
            throw e;
        } catch (Exception e) {
            throw new CoinyueOpenException(-1, "AES 解密失败: " + e.getMessage(), e);
        }
    }
}
