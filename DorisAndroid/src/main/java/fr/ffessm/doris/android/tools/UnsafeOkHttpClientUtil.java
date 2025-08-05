package fr.ffessm.doris.android.tools;

import android.os.Build;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Class used to workaround Let's Encrypt invalid certificate chain on Android 6 (Marshmallow) and bellow
 * UNSAFE ! MUST NOT BE USED IN ANY OTHER ANDROID VERSION THAN THESE ONES
 */
public class UnsafeOkHttpClientUtil {
    private static final String LOG_TAG = UnsafeOkHttpClientUtil.class.getSimpleName();


    // --- Singleton Instance ---
    private static volatile OkHttpClient sInstance = null;

    // Private constructor to prevent instantiation
    private UnsafeOkHttpClientUtil() {}

    public static OkHttpClient getOkHTTPClientInstance() {
        if (sInstance == null) {
            synchronized (UnsafeOkHttpClientUtil.class) {
                if (sInstance == null) {
                    sInstance = createCustomOkHttpClient();
                }
            }
        }
        return sInstance;
    }

    private static OkHttpClient createCustomOkHttpClient() {
        // --- Condition: Apply only for Android versions Nougat (API 24) and older ---
        // Adjust this SDK check as per your needs for Let's Encrypt / specific SSL issues
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) { // N is API 24 (Nougat 6.0)
            Log.w(LOG_TAG, "Applying unsafe SSLSocketFactory for OkHttpClient on Android API " + Build.VERSION.SDK_INT);
            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[0];
                            }

                        }
                };
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                return builder.build();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                Log.e(LOG_TAG, "Failed to create unsafe SSLSocketFactory for Picasso", e);
                return new OkHttpClient.Builder().build(); // Fallback
            }
        } else {
            Log.d(LOG_TAG, "Using standard OkHttpClient for Picasso on Android API " + Build.VERSION.SDK_INT);
            return new OkHttpClient.Builder().build();
        }
    }
}
