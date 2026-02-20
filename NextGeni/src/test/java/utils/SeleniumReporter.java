package utils;

import io.restassured.response.Response;  // ← RESTASSURED Response (not Selenium)
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SeleniumReporter {
    private static WebDriver driver;
    private static boolean initialized = false;

    public static synchronized void init() {
        if (!initialized) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-dev-shm-usage", "--no-sandbox", "--disable-gpu");
            // options.addArguments("--headless");
            driver = new ChromeDriver(options);

            driver.get("data:text/html," +
                    "<!DOCTYPE html><html><head><title>API Test Dashboard</title>" +
                    "<style>body{font-family:Arial;background:#f0f2f5;padding:20px}" +
                    ".log{border-left:5px solid #ddd;padding:15px;margin:10px 0;background:white;" +
                    "border-radius:8px;box-shadow:0 2px 5px rgba(0,0,0,0.1)}" +
                    ".success{border-left-color:#28a745}.error{border-left-color:#dc3545}" +
                    "pre{background:#f8f9fa;padding:15px;border-radius:5px;overflow:auto;max-height:400px;" +
                    "font-size:12px;line-height:1.4}</style></head>" +
                    "<body><h1>🚀 QA Automation Results</h1><div id='logs'></div></body></html>");
            initialized = true;
        }
    }

    // ✅ FIXED SIGNATURE: Takes RestAssured Response
    public static void logApiCall(String title, Response resp) {
        init();  // Lazy init

        int status = resp.getStatusCode();
        String statusClass = (status >= 200 && status < 300) ? "success" : "error";
        String body = resp.asPrettyString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        String logHtml = String.format(
                "<div class='log %s'>" +
                        "<h3>📊 %s <span style='float:right;font-weight:bold;color:%s'>Status: %d</span></h3>" +
                        "<small>Time: %d ms | Size: %d bytes</small>" +
                        "<pre>%s</pre>" +
                        "</div>",
                statusClass, title, statusClass.equals("success") ? "#28a745" : "#dc3545",
                status, resp.getTime(), resp.getBody().asByteArray().length, body
        );

        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('logs').innerHTML += arguments[0]; " +
                        "document.getElementById('logs').scrollTop = document.getElementById('logs').scrollHeight;",
                logHtml
        );
    }

    public static void close() {
        if (driver != null) {
            driver.quit();
            driver = null;
            initialized = false;
        }
    }
}
