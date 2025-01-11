package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import javax.net.ssl.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;

public class PriyankaTest {

    // Disable SSL verification (not recommended for production)
    private static void disableSslVerification() {
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCertificates, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to download the driver
    public static void downloadFile(String fileUrl, String destination) throws IOException {
        // Disable SSL verification before making the request
        disableSslVerification();

        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(destination)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Download completed: " + destination);
        }
    }

    public static void main(String[] args) {
        String browser = "chrome"; // or "gecko"
        String version = "115.0.5790.102"; // Version of the driver you need
        String downloadLocation = "C:/drivers/driver.zip"; // Change this to your desired location

        String downloadUrl = getDriverDownloadUrl(browser, version);

        if (downloadUrl != null) {
            try {
                downloadFile(downloadUrl, downloadLocation);
            } catch (IOException e) {
                System.err.println("Error downloading file: " + e.getMessage());
            }
        } else {
            System.err.println("Invalid browser or version specified.");
        }
    }

    public static String getDriverDownloadUrl(String browser, String version) {
        String baseUrl = "";

        switch (browser.toLowerCase()) {
            case "chrome":
                baseUrl = "https://chromedriver.storage.googleapis.com/";
                return baseUrl + version + "/chromedriver_win32.zip"; // Windows version
            case "gecko":
                baseUrl = "https://github.com/mozilla/geckodriver/releases/download/v";
                return baseUrl + version + "/geckodriver-v" + version + "-win64.zip"; // Windows version for GeckoDriver (Firefox)
            default:
                return null;
        }
    }
}
