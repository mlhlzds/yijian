package com.easy.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.shiro.crypto.hash.SimpleHash;
import sun.misc.BASE64Encoder;

/**
 * MD5加密
 */
public class MD5{
	/*
	 * 取得CreateSafeCode实例
	 */
	public static MD5 Instance() {
		return new MD5();
	}

	public final String RMD5(String password) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = password.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String encrypt(String strSrc) {
		String strEncode = "";
		try {
//			String strSrc = "5XxkZ2TlTUW9nF5SVzhVLhO33n8=123456";//id+pwd（明文）
			byte btKey[] = new byte[strSrc.getBytes().length + 1];
			System.arraycopy(strSrc.getBytes(), 0, btKey, 0, strSrc.getBytes().length);
	
			    MessageDigest md = MessageDigest.getInstance("MD5");
			    md.update(btKey);
			    byte btDigest[] = md.digest();
			    BASE64Encoder encoder = new BASE64Encoder();
			    strEncode = encoder.encode(btDigest);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return strEncode;
	}
	
	public static String shiroEncrypt(String password, String salt, 
			int hashIterations, String algorithmName) {
		return new SimpleHash(algorithmName, password,  
                salt, hashIterations).toHex();  
	}
}
