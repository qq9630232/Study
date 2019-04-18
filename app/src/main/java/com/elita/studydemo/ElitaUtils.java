package com.elita.studydemo;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 所有Util的集合
 */
public class ElitaUtils {
 
	/**
	 * 使用zip进行压缩
	 *
	 * @param str 压缩前的文本
	 * @return 返回压缩后的文本
	 */
	public static final String zip(String str) {
		if (str == null)
			return null;
		byte[] compressed;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;
		String compressedStr = null;
		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes("utf-8"));
			zout.closeEntry();
			compressed = out.toByteArray();
			//compressedStr = new sun.misc.BASE64Encoder().encodeBuffer(compressed);//base64解密(java写法)
			compressedStr = Base64.encodeToString(compressed, Base64.NO_WRAP);//base64解密(android写法)
		} catch (IOException e) {
		} finally {
			compressed = null;
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return compressedStr;
	}
 
	/**
	 * 使用zip进行解压缩
	 *
	 * @param compressedStr 压缩后的文本
	 * @return 解压后的字符串
	 */
	public static final String unzip(String compressedStr) {
		if (compressedStr == null) {
			return null;
		}
 
		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed = null;
		try {
			//byte[] compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);//base64加密(java写法)
			byte[] compressed = Base64.decode(compressedStr, Base64.DEFAULT);//base64加密(android写法)
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			decompressed = null;
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return decompressed;
	}

	public static void main(String[] args) throws IOException {
		String temp = "l;jsafljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看看看看看  ";
		System.out.println("原字符串=" + temp);
		String temp1 = ElitaUtils.zip(temp);
		System.out.println("压缩后的字符串=" + temp1);
		System.out.println("原长=" + temp.length());
		System.out.println("压缩后的长=" + temp1.length());
		System.out.println("开始解压缩时间" + System.currentTimeMillis());
		System.out.println("解压后的字符串=" + ElitaUtils.unzip(temp1));
		System.out.println("结束解压缩时间" + System.currentTimeMillis());

	}
 
}
