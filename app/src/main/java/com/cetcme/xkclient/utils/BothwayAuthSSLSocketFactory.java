/**
 * JF
 */
package com.cetcme.xkclient.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

/**
 * @author wangyong
 *
 */
public class BothwayAuthSSLSocketFactory {
	static final String TAG = "JF_HTTPS";

	static Context context = null;

	public static SSLSocketFactory getSSLSocketFactory(Context ctx) {
		context = ctx;
		SSLContext sctx = null;
		try {
			sctx = SSLContext.getInstance("TLS");
			KeyManager[] socketFactory = createKeyManagers("dbq_android_c.pfx", "deW3FoT42sOz8Mjv", (String)null);
			TrustManager[] trustManagers = createTrustManagers("dbq_android_s.bks", "eSjMc4PoN9RkXa3h");
			sctx.init(socketFactory, trustManagers, (SecureRandom)null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		SSLSocketFactory socketFactory = sctx.getSocketFactory();
		return socketFactory;
	}

	static KeyManager[] createKeyManagers(String keyStoreFileName, String keyStorePassword, String alias)
			throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
		InputStream is = context.getResources().getAssets().open(keyStoreFileName);
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(is, keyStorePassword.toCharArray());

		printKeyStoreInfo(keyStore);// for debug

		KeyManager[] managers;
		if (alias != null) {
			managers = new KeyManager[] { new BothwayAuthSSLSocketFactory().new AliasKeyManager(keyStore, alias, keyStorePassword) };
		} else {
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, keyStorePassword == null ? null : keyStorePassword.toCharArray());
			managers = keyManagerFactory.getKeyManagers();
		}
		return managers;
	}

	static TrustManager[] createTrustManagers(String trustStoreFileName, String trustStorePassword)
			throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		InputStream is = context.getResources().getAssets().open(trustStoreFileName);
		KeyStore trustStore = KeyStore.getInstance("bks");
		trustStore.load(is, trustStorePassword.toCharArray());

		printKeyStoreInfo(trustStore);// for debug

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(trustStore);

		return trustManagerFactory.getTrustManagers();
	}

	static void printKeyStoreInfo(KeyStore keyStore) throws KeyStoreException {
		Log.d(TAG, "Provider : " + keyStore.getProvider().getName());
		Log.d(TAG, "Info : " + keyStore.getProvider().getInfo());
		Log.d(TAG, "Type : " + keyStore.getType());
		Log.d(TAG, "Size : " + keyStore.size());

		Enumeration<String> en = keyStore.aliases();
		while (en.hasMoreElements()) {
			Log.d(TAG, "Alias : " + en.nextElement());
		}
	}

	class AliasKeyManager implements X509KeyManager {
		KeyStore _ks;
		String _alias;
		String _password;

		public AliasKeyManager(KeyStore _ks, String _alias, String _password) {
			this._ks = _ks;
			this._alias = _alias;
			this._password = _password;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.net.ssl.X509KeyManager#chooseClientAlias(java.lang.String[],
		 * java.security.Principal[], java.net.Socket)
		 */
		@Override
		public String chooseClientAlias(String[] str, Principal[] principal, Socket socket) {
			return _alias;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.net.ssl.X509KeyManager#chooseServerAlias(java.lang.String,
		 * java.security.Principal[], java.net.Socket)
		 */
		@Override
		public String chooseServerAlias(String str, Principal[] principal, Socket socket) {
			return _alias;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.net.ssl.X509KeyManager#getCertificateChain(java.lang.String)
		 */
		@Override
		public X509Certificate[] getCertificateChain(String alias) {
			try {
				Certificate[] certificates = this._ks.getCertificateChain(alias);
				if (certificates == null)
					throw new FileNotFoundException("no certificate found for alias:" + alias);

				X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
				System.arraycopy(certificates, 0, x509Certificates, 0, certificates.length);
				return x509Certificates;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.net.ssl.X509KeyManager#getClientAliases(java.lang.String,
		 * java.security.Principal[])
		 */
		@Override
		public String[] getClientAliases(String str, Principal[] principal) {
			return new String[] { _alias };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.net.ssl.X509KeyManager#getPrivateKey(java.lang.String)
		 */
		@Override
		public PrivateKey getPrivateKey(String alias) {
			try {
				return (PrivateKey) _ks.getKey(alias, _password == null ? null : _password.toCharArray());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.net.ssl.X509KeyManager#getServerAliases(java.lang.String,
		 * java.security.Principal[])
		 */
		@Override
		public String[] getServerAliases(String str, Principal[] principal) {
			return new String[] { _alias };
		}
	}
}
