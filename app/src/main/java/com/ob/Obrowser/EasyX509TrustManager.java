package com.ob.Obrowser;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 *   Instance of this interface manage which X509 certificates may be used to authenticate 
 *   the remote side of a secure socket. Decisions may be based on trusted certificate
 *   authorities, certificate revocation lists, online status checking or other means. 
 *  
 */
public class EasyX509TrustManager implements X509TrustManager {
	private X509TrustManager standardTrustManager = null;

	/** * Constructor for EasyX509TrustManager. */
	public EasyX509TrustManager(KeyStore keystore)throws NoSuchAlgorithmException, KeyStoreException {
		super();
		TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		factory.init(keystore);
		TrustManager[] trustmanagers = factory.getTrustManagers();
		if (trustmanagers.length == 0) {
			throw new NoSuchAlgorithmException("no trust manager found");
		}
		this.standardTrustManager = (X509TrustManager) trustmanagers[0];
	}

	/**
	 *  Given the partial or complete certificate chain provided by the peer, build a certificate 
	 *  path to a trusted root and return if it can be validated and is trusted for client SSL
	 *   authentication based on the authentication type.
	 */
	public void checkClientTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
		standardTrustManager.checkClientTrusted(certificates, authType);
	}

	/*
	 *   Given the partial or complete certificate chain provided by the peer, 
	 *   build a certificate path to a trusted  root and return if it can 
	 *   be validated and is trusted for server SSL authentication based on the authentication type.
	 */
	public void checkServerTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
		if ((certificates != null) && (certificates.length == 1)) {
			certificates[0].checkValidity();
		} else {
			standardTrustManager.checkServerTrusted(certificates, authType);
		}
	}

	/** * 
	 * Return an array of certificate authority certificates 
	 * which are trusted for authenticating peers.
	 **/
	
	public X509Certificate[] getAcceptedIssuers() {
		return this.standardTrustManager.getAcceptedIssuers();
	}
}
