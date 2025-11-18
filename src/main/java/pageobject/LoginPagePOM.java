package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPagePOM {

    private WebDriver driver;
    private WebDriverWait wait;

    //                       ЛОКАТОРЫ

    private final By emailField = By.xpath("//label[text()='Email']/following-sibling::input");
    private final By passwordField = By.xpath("//label[text()='Пароль']/following-sibling::input");
    private final By loginButton = By.xpath("//button[contains(text(),'Войти')]");
    private final By resetPasswordButton = By.xpath("//a[normalize-space(text())='Восстановить пароль']");

    //                       МЕТОДЫ

    public LoginPagePOM(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void waitForLoginPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
    }

    public void enterEmail(String email) {
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement passInput = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        passInput.clear();
        passInput.sendKeys(password);
    }

    public void clickLoginButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        btn.click();
    }

    public void clickResetPasswordButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(resetPasswordButton));
        btn.click();
    }

    public void openLoginPage() {
        driver.get("https://stellarburgers.education-services.ru/login");
            }

    public void submitLogin() { driver.findElement(loginButton).click(); }

}
