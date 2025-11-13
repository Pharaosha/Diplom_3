package Page_Object;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;



public class MainPagePOM {

    public MainPagePOM(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private WebDriver driver;
    private WebDriverWait wait;

    //                       ЛОКАТОРЫ

    private final By loginButtonMain = By.xpath("//button[normalize-space(text())='Войти в аккаунт']");

    private final By personalAccountButton = By.xpath("//p[normalize-space(text())='Личный Кабинет']");

    private final By constructorButton = By.xpath("//p[contains(text(),'Конструктор')]");;


    //                      МЕТОДЫ

    public void clickLoginButtonMain() {
        driver.findElement(loginButtonMain).click();
    }

    public void clickPersonalAccountButton() {
        driver.findElement(personalAccountButton).click();
    }

    public void clickConstructorButton() {
        driver.findElement(constructorButton).click();
    }

}
