package com.sibat.util.captcha;

import com.sibat.util.jwt.Jwt;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.apache.log4j.Logger;

public class HashUtil {
	private static Logger log = Logger.getLogger(HashUtil.class);
	private final static String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
	private static final String SALT = "gop666safe";
	/*Hashing the string*/
	public final static String strHash(String str) {
		return hashAlgorithm(str,"md5");
	}

	/*generate a random id*/
	public final static String getRandomId() {
		return hashAlgorithm(genRandomStr(32),"md5");
	}

	public static String createPassword(String userName, String password) {
		StringBuilder sb = new StringBuilder(userName).reverse();
		sb.append(password).append(SALT);
		try {
			return Jwt.getMD5(sb.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String genRandomStr(int length){
		Random r = new Random();
		StringBuffer sf = new StringBuffer();

		for (int i = 0; i < length; i++) {
			int number = r.nextInt(62); // 0~61
			sf.append(str.charAt(number));
		}
		//sf.append(DateUtil.getCurrentTimeString()); //add current time
		return sf.toString();
	}
	
	/*here algorithm are SHA 1 2 156 515 md5*/
	private static  String hashAlgorithm(String str,String Algorithm){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(Algorithm);
		} catch (NoSuchAlgorithmException e) {
			log.debug("error using Hash method");
			e.printStackTrace();
		}
		md.update(str.getBytes()); // first we must get the bytes of string
		byte[] bs = md.digest(); // begin encryption

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bs.length; i++) { // bytes convert string
			int v = bs[i] & 0xff;
			if (v < 16) {
				sb.append(0);
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}

}
