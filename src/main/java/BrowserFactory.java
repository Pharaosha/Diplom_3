import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

public class BrowserFactory {

    public WebDriver getWebDriver(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chrome":
                return new ChromeDriver();
            case "yandex":

                ChromeOptions options = new ChromeOptions();
                options.setBinary("C:/Users/evgen/AppData/Local/Yandex/YandexBrowser/Application/browser.exe");


                System.setProperty("webdriver.chrome.driver", "X:/Ydriver/yandexdriver.exe");

                return new ChromeDriver(options);
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }
}