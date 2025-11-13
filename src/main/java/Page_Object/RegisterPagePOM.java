package Page_Object;

import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static io.restassured.RestAssured.given;


public class RegisterPagePOM {

    private WebDriver driver;

    public RegisterPagePOM(WebDriver driver) {
        this.driver = driver;
    }

    //                       ЛОКАТОРЫ

    // Поле "Имя"
    private By nameInput = By.xpath("//label[text()='Имя']/following-sibling::input");

    // Поле "Email"
    private By emailInput = By.xpath("//label[text()='Email']/following-sibling::input");

    // Поле "Пароль"
    private By passwordInput = By.xpath("//label[text()='Пароль']/following-sibling::input");

    // Кнопка "Зарегистрироваться"
    private By registerButton = By.xpath(".//button[text()='Зарегистрироваться']");

    // Ошибка при неправильном пароле
    private By errorMessage = By.cssSelector("p.input__error.text_type_main-default");

    // Ссылка "Войти"
    private By loginLink = By.xpath(".//a[text()='Войти']");

    private final By passwordError = By.xpath("//p[contains(@class,'input__error') and text()='Некорректный пароль']");


//                       МЕТОДЫ

    public void enterName(String name) {
        driver.findElement(nameInput).sendKeys(name);

    }

    public void enterEmail(String email){
        driver.findElement(emailInput).sendKeys(email);
    }

    public void enterPassword(String password){
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickRegister() {
        driver.findElement(registerButton).click();
    }

    public boolean isErrorVisible() {
        return driver.findElement(errorMessage).isDisplayed();
    }

    public void clickLoginLink() {
        driver.findElement(loginLink).click();
    }

    public String getPasswordErrorText() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(passwordError)).getText();
    }

    public Response getRegistrationResponse(String email, String password) {
        return given()
                .header("Content-type", "application/json")
                .body("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}")
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/login");
    }
}
