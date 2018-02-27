package com.cetcme.xkclient.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAHelper {

	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"; // 加密block需要预留11字节
	public static final int KEYBIT = 2048;
	public static final int RESERVEBYTES = 11;

	//即富对外开放测试密钥
	private final String publickey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlqbs0SrlwQ9VG93dbSNGDR9upp9kxlOPTul6eSsYRL/b6w3Xs6F6LKNO+pVi/3jHae9BPUGrwIuemKmclT3Q5Q48jFfrSYC6oNt19/IaQn5dRqmBd9nz6H+JZ/u9x+Q3ydx5KiBj6paUTf1x1/zo4a7ABJdiVU8V+Xxhr+dE+OUTbYdCEi/Z6KrygOPN9IAc20/k5tReJuMOqcii7H5TUCDe6Nj7odMVCVBqOGbR5TNFHI+3Bnr37Iup/JBsT/OlaAoAk333Nnv4dipaYFMgodo2UqWyoO3PRoM7Kjd/9lFNj4J9EushQsHrAyEsBqLWe7/HSDK+blpSipWxtq0zqwIDAQAB";
	private final String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDi1P5QNsMRbwK5XnwOYH90OsRAYhZlsLpyF4MBJ0LBwlNp70tKwBy11nmqRX/0OBOfoSbapoqGcg89EenjKWoq91Lywwe3IQ0KjvnP7yISSbdF7ubrWpppPWCYAGXo5XJpb3KeAqHP2BJ5622Yre7nqwLbGdXL2lixYYGIMTUPSWjmL4KOQRA6X1SDnfmUzBM4OrH0qGi2yitcgR05ld+OHUhieb1nVmNdwRqDPPbrJzh33XHiFw+95hGTo7/onuoIjd6Gp/+1tDk3Hg+ObNcZb/IBIu5bolYxd87xvKuN8LbPzV1KppWF0+Hesh29BdllXkcVZOzhDc4pK35v5qL7AgMBAAECggEBANkZbHJPh3H3Is9SPD/yYepXmJ/agX9An6I1GKCQ+BbEq9tXOWPI7XGoXff23tlaloWQ0sQw46J8JlOYApf1enS3FH/e5FKE4pt5bmVyhdSlEzFuzzrxE5qoMPwqXV+Dw3ZgQX40HLT2I/DmwJWB24eFqI0VdAfQ6lSTc0trdVIzbeG2aPBuAKDhsnV2d6YnlMPCMnITon+sLVMk4NKwHuCQAQ4aZdGIUD4X3wnjj+9kiqe5CgFJBIAti4BOcxaEow0edOg8+D2l9woVYozIVq5oaEGqGKxWEccYzR+cTyJSrT76DLDsnlhItT6Ig6gy/p0thppBW2GYvqqFM5CkCpECgYEA/AnqQlf/z76RGD786YXq3nQYYHuJQ8PpGG9tUTq4YxJRvKAQP/UgT4d2AIzsghM29CniKS9F638SqKUVeN4mumuNh2guW7R/15n8YYLb/MUGYSjMngibxLyli27lBqFX8oRbaGpokxp6uAgTTa+E7ZAYyG+8eYnoHK1FmnaOZqkCgYEA5mWoitO5Za1SobM9tPkZ1adrCs/1lP/x3UvgijPcdrB9RaiQNKPV11egHVBbkxo90+mZV4U+t6c82xlPzrINm8XjzsrZhEpZVbEtXbuHG99bJNIbjtHSGCe1+augBnTq0bL6gZZYRYU/gIldxKSASyB4DxXgt4kBBIE9sPuYVwMCgYEA5egNfvNS8Kg/JsTeZA66zq1MR0apUd111cpfB8fuxsDBOrUv3Ye3L5S7uYjTfVwVtug126e5ujYySRA95AGRhl/xvGD2WMTwETXwGOsLzSnUxfA3prjnjrEeuXAxDrlCxoCh+T+HoRh4dUM4iJbhROs7ECuM27lGuFH+ugYHo/ECgYATdHYc8u/u1AnsOZR2WaqCzhZN3W2hXVNxGl0ljmeJZjHN1gQHTeZavr14fDafX1HlOQ6Hw2qeW3LaFXMVwxc/FiC8b4iakSSmBcYM6i7ofZ3WqB9HmhQhkgjdwk5K/Uhu2KwqkSV3CV9pXTAb0MA8l8tLis9k8B6z4K3y6evdPQKBgQC82t3AQk7Lzec+I7olBi3zvWViNUeWQwS4+IzKtxqpuVuef+BCdbFuNIMxswelIdTkrXyDCRnbCs21mx0M6V6x3Y+g4J2WQq4MZllJ2i21HMTfzy9H7mcKiIF5LNat0UvzbPhq5p0qeX3auL9a3BGZkZeydR7sQqtgOGD7O46soA==";

	private PrivateKey localPrivKey;
	private PublicKey peerPubKey;

	public RSAHelper() {
		try {
			if(!privateKey.isEmpty())
				localPrivKey = getPrivateKey(privateKey);
			if(!publickey.isEmpty())
				peerPubKey = getPublicKey(publickey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testMe(String publickey1) {
		if(!publickey1.isEmpty()) {
			try {
				peerPubKey = getPublicKey(publickey1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public RSAPublicKey getPublicKey(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return getPublicKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				throw new Exception("关闭输入缓存流出错");
			}

			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				throw new Exception("关闭输入流出错");
			}
		}
	}

	public RSAPublicKey getPublicKey(String publicKeyStr) throws Exception {
		try {
			byte[] buffer = Base64AndZip.decode(publicKeyStr.toCharArray());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory
					.generatePublic(keySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public RSAPrivateKey getPrivateKey(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return getPrivateKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				throw new Exception("关闭输入缓存流出错");
			}

			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				throw new Exception("关闭输入流出错");
			}
		}
	}

	public RSAPrivateKey getPrivateKey(String privateKeyStr) throws Exception {
		try {
			byte[] buffer = Base64AndZip.decode(privateKeyStr.toCharArray());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory
					.generatePrivate(keySpec);
			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new Exception("私钥非法");
		}catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * RAS加密
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] encryptRSA(byte[] plainBytes, boolean useBase64Code, String charset)
			throws Exception {
		String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"; // 加密block需要预留11字节
		int KEYBIT = 2048;
		int RESERVEBYTES = 11;
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		int decryptBlock = KEYBIT / 8; // 256 bytes
		int encryptBlock = decryptBlock - RESERVEBYTES; // 245 bytes
		// 计算分段加密的block数 (向上取整)
		int nBlock = (plainBytes.length / encryptBlock);
		if ((plainBytes.length % encryptBlock) != 0) { // 余数非0，block数再加1
			nBlock += 1;
		}
		// 输出buffer, 大小为nBlock个decryptBlock
		ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock
				* decryptBlock);
		cipher.init(Cipher.ENCRYPT_MODE, peerPubKey);
		// cryptedBase64Str =
		// Base64.encodeBase64String(cipher.doFinal(plaintext.getBytes()));
		// 分段加密
		for (int offset = 0; offset < plainBytes.length; offset += encryptBlock) {
			// block大小: encryptBlock 或剩余字节数
			int inputLen = (plainBytes.length - offset);
			if (inputLen > encryptBlock) {
				inputLen = encryptBlock;
			}
			// 得到分段加密结果
			byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
			// 追加结果到输出buffer中
			outbuf.write(encryptedBlock);
		}
		// 如果是Base64编码，则返回Base64编码后的数组
		if (useBase64Code) {
			return new String(Base64AndZip.encode(outbuf.toByteArray())).getBytes();
		} else {
			return outbuf.toByteArray(); // ciphertext
		}
	}

	/**
	 * RSA解密
	 * @param cryptedBytes
	 *            待解密信息
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] decryptRSA(byte[] cryptedBytes, boolean useBase64Code,
			String charset) throws Exception {
		String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"; // 加密block需要预留11字节
		byte[] data = null;

		// 如果是Base64编码的话，则要Base64解码
		if (useBase64Code) {
			data = Base64AndZip.decode(new String(cryptedBytes, charset).toCharArray());
		} else {
			data = cryptedBytes;
		}

		int KEYBIT = 2048;
		int RESERVEBYTES = 11;
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		int decryptBlock = KEYBIT / 8; // 256 bytes
		int encryptBlock = decryptBlock - RESERVEBYTES; // 245 bytes
		// 计算分段解密的block数 (理论上应该能整除)
		int nBlock = (data.length / decryptBlock);
		// 输出buffer, , 大小为nBlock个encryptBlock
		ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock
				* encryptBlock);
		cipher.init(Cipher.DECRYPT_MODE, localPrivKey);
		// plaintext = new
		// String(cipher.doFinal(Base64.decodeBase64(cryptedBase64Str)));
		// 分段解密
		for (int offset = 0; offset < data.length; offset += decryptBlock) {
			// block大小: decryptBlock 或剩余字节数
			int inputLen = (data.length - offset);
			if (inputLen > decryptBlock) {
				inputLen = decryptBlock;
			}

			// 得到分段解密结果
			byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
			// 追加结果到输出buffer中
			outbuf.write(decryptedBlock);
		}
		outbuf.flush();
		outbuf.close();
		return outbuf.toByteArray();
	}

	/**
	 * RSA签名
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] signRSA(byte[] plainBytes, boolean useBase64Code,
			String charset) throws Exception {
		String SIGNATURE_ALGORITHM = "SHA1withRSA";
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(localPrivKey);
		signature.update(plainBytes);

		// 如果是Base64编码的话，需要对签名后的数组以Base64编码
		if (useBase64Code) {
			return new String(Base64AndZip.encode(signature.sign()))
					.getBytes(charset);
		} else {
			return signature.sign();
		}
	}

	/**
	 * 验签操作
	 * @param plainBytes
	 *            需要验签的信息
	 * @param signBytes
	 *            签名信息
	 * @return boolean
	 */
	public boolean verifyRSA(byte[] plainBytes, byte[] signBytes,
			boolean useBase64Code, String charset) throws Exception {
		boolean isValid = false;
		try {
			String SIGNATURE_ALGORITHM = "SHA1withRSA";
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
			signature.initVerify(peerPubKey);
			signature.update(plainBytes);

			// 如果是Base64编码的话，需要对验签的数组以Base64解码
			if (useBase64Code) {
				isValid = signature.verify(Base64AndZip.decode(new String(
						signBytes, charset).toCharArray()));
			} else {
				isValid = signature.verify(signBytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isValid;
	}
}
