package com.example.seastar.bossclientandroid;

/**
 * Created by seastar on 2017/2/9.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MessageSender {

    private final String USER_AGENT = "Mozilla/5.0";
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private boolean isThreadRunning = false;

    public void send(String message,String url) {
        Log.d("LoggerWatcher", message);
        if (messageQueue.size() > 200)
            return;
        messageQueue.offer(message);
    }

    public void startup() {
        isThreadRunning = true;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (isThreadRunning) {
                    String message = messageQueue.peek();
                    if (message == null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        continue;
                    }

                    try {
                        HttpURLConnection connection = createHttpUrlConnectionWithPost("");

                        Map<String, String> headers = new HashMap<>();
                        headers.put("content-type", "application/zip");
                        headers.put("Accept", "*/*");

                        sendMessageWithHttp(connection, message, headers);

                        closeHttpUrlConnection(connection);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    // 剔除已经发送成功的
                    messageQueue.poll();
                }
            }
        });
    }

    public void shutdown() {
        isThreadRunning = false;
    }

    private HttpURLConnection createHttpUrlConnectionWithPost(String url) throws MalformedURLException, IOException {
        URL uri = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        return connection;
    }

    private HttpsURLConnection createHttpsUrlConnectionWithPost(String url) throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, MalformedURLException, IOException {
        TrustManager[] tm = {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
        }};

        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());

        URL uri = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) uri.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());


        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        return connection;
    }

    private void sendMessageWithHttp(HttpURLConnection connection, String message, Map<String, String> headers) throws IOException {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.setRequestProperty("Restful-Agent", USER_AGENT);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(message.getBytes("UTF-8"));
                gzipOutputStream.flush();


                outputStream.write(byteArrayOutputStream.toByteArray());
                outputStream.flush();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    StringBuffer buffer = new StringBuffer();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        Log.d("LoggerWatcher", responseCode + " " + buffer.toString());
                    }
                }
            }
        }
    }

    private void closeHttpUrlConnection(HttpURLConnection connection) throws IOException {
        connection.disconnect();
    }


}
