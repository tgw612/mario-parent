package com.mario.common.util;

import java.io.IOException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 陈志杭
 * @contact 279397942@qq.com
 * @date 2017/2/6
 * @description
 */
@Slf4j
public class EncryptUtils {


  /**
   * DES加密 解密算法
   */
  private final static String DES = "DES";
  private final static String ENCODE = "UTF8";


  public static void main(String[] args) throws Exception {
//        StringBuilder stringBuilder = new StringBuilder();
//        int i = 0;
//        while (i < 100) {
//            stringBuilder.append(MessageFormat.format("CREATE SCHEMA `sibu_wms_base{0}` DEFAULT CHARACTER SET utf8 ;", i));
//            i++;
//        }
//        System.out.println(stringBuilder.toString());
//        if (true) {
//            return;
//        }
//        DateTime now = new DateTime();
//        DateTime nowPlus3 = now.plusMinutes(30);
//        System.out.println(Minutes.minutesBetween(now, nowPlus3).getMinutes());
//        System.out.println(Minutes.minutesBetween(nowPlus3, now).getMinutes());
//
//        String data = "123456";
//        String key = ")(*&^%$#}";
//        System.out.println("原始字符串：" + data);
//        String encryptData = encryptDES(data, key);
//        System.out.println("加密字符串：" + encryptData + " " + encryptData.length());
//        String decryptData = decryptDES(encryptDES(data, key), key);
//        System.out.println("解密字符串：" + decryptData);

    String token = "qEUK8QQ9ogcHAsX+jAfDWF3wUKusx8znFiLfG7wsvNrXXo8Vwz21EeSPXd1Y2WBP4Z46gJMtrgCv5JRoVMWAurbnN//gi26TlRRpQTTsbzZ8/NF1M9NtXHLcTPeyPKKVdlCB7WdBkf0ThMnYpTD/V3cNCmLHGt7rcYvpHugu/gb1xjAyHzhWRIxXq9szKq46mZJhpzqGIK2McjPIlzmaJ13vnhX4mw0C";
    try {
      String json = decryptDES(token, "!@#1234abcfpokls");
      System.out.println(json);
    } catch (Exception e) {
      log.error("错误", e);
    }
  }

  /**
   * Description 根据键值进行加密
   *
   * @param data
   * @param key  加密键byte数组
   * @return
   * @throws Exception
   */
  public static String encryptDES(String data, String key) throws Exception {
    byte[] bt = encryptDES(data.getBytes(ENCODE), key.getBytes(ENCODE));
    return CodecUtils.encodeBase64(bt);
  }

  /**
   * Description 根据键值进行解密
   *
   * @param data
   * @param key  加密键byte数组
   * @return
   * @throws IOException
   * @throws Exception
   */
  public static String decryptDES(String data, String key) throws IOException,
      Exception {
    if (data == null) {
      return null;
    }
    byte[] buf = CodecUtils.decodeBase64Byte(data);
    byte[] bt = decryptDES(buf, key.getBytes(ENCODE));
    return new String(bt, ENCODE);
  }

  /**
   * Description 根据键值进行加密
   *
   * @param data
   * @param key  加密键byte数组
   * @return
   * @throws Exception
   */
  private static byte[] encryptDES(byte[] data, byte[] key) throws Exception {
    // 生成一个可信任的随机数源
    SecureRandom sr = new SecureRandom();

    // 从原始密钥数据创建DESKeySpec对象
    DESKeySpec dks = new DESKeySpec(key);

    // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
    SecretKey securekey = keyFactory.generateSecret(dks);

    // Cipher对象实际完成加密操作
    Cipher cipher = Cipher.getInstance(DES);

    // 用密钥初始化Cipher对象
    cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

    return cipher.doFinal(data);
  }

  /**
   * Description 根据键值进行解密
   *
   * @param data
   * @param key  加密键byte数组
   * @return
   * @throws Exception
   */
  private static byte[] decryptDES(byte[] data, byte[] key) throws Exception {
    // 生成一个可信任的随机数源
    SecureRandom sr = new SecureRandom();

    // 从原始密钥数据创建DESKeySpec对象
    DESKeySpec dks = new DESKeySpec(key);

    // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
    SecretKey securekey = keyFactory.generateSecret(dks);

    // Cipher对象实际完成解密操作
    Cipher cipher = Cipher.getInstance(DES);

    // 用密钥初始化Cipher对象
    cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

    return cipher.doFinal(data);
  }


}
