import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageobject.LoginPagePOM;
import pageobject.MainPagePOM;
import pageobject.PersonalAccountPagePOM;

import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PersonalAccountTests {

    private WebDriver driver;
    private MainPagePOM mainPagePOM;
    private LoginPagePOM loginPagePOM;
    private PersonalAccountPagePOM personalAccountPagePOM;
    private static String accessToken;
    private BrowserFactory browserFactory;

    @BeforeEach
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");
        browserFactory = new BrowserFactory();
        driver = browserFactory.getWebDriver(browser);
        mainPagePOM = new MainPagePOM(driver);
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/";
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) driver.quit();
    }

    @AfterAll
    @DisplayName("Удаление пользователя после тестов")
    public static void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            deleteUser(accessToken);
            accessToken = null;
        }
    }

    // ---------------------- Генерация случайного пользователя ----------------------
    private UserData generateRandomUser() {
        Faker faker = new Faker();
        return new UserData(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12),
                faker.name().firstName()
        );
    }

    // ---------------------- API Helpers ----------------------
    @Step("Создать пользователя через API")
    public Response createNewUser(UserData userData) {
        return given()
                .header("Content-type", "application/json")
                .body(userData)
                .when()
                .post("/api/auth/register");
    }

    @Step("Логин пользователя через API")
    public Response loginUser(UserData userData) {
        return given()
                .header("Content-type", "application/json")
                .body(userData)
                .when()
                .post("/api/auth/login");
    }

    @Step("Проверка успешного логина")
    public void checkUserLoginSuccessfully(Response loginResponse, UserData userData) {
        loginResponse.then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(userData.getEmail().toLowerCase()))
                .body("user.name", equalTo(userData.getName()));
    }

    @Step("Извлечь accessToken из ответа")
    public String extractAccessToken(Response response) {
        String token = response.then().extract().path("accessToken");
        return token != null ? token.replace("Bearer ", "") : null;
    }

    @Step("Удалить пользователя по accessToken")
    public static void deleteUser(String accessToken) {
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/auth/user");

        if (response.statusCode() != 202) {
            System.out.println("Ошибка при удалении пользователя:");
            response.prettyPrint();
        }

        response.then().assertThat().statusCode(202);
    }

    // ---------------------- Web Helpers ----------------------
    @Step("Открыть главную страницу")
    private void openMainPage() {
        driver.get("https://stellarburgers.education-services.ru");
    }

    @Step("Открыть страницу логина")
    private void openLoginPage() {
        driver.get("https://stellarburgers.education-services.ru/login");
        loginPagePOM = new LoginPagePOM(driver);
    }

    @Step("Ввести email и пароль")
    private void enterLoginCredentials(UserData userData) {
        loginPagePOM.enterEmail(userData.getEmail());
        loginPagePOM.enterPassword(userData.getPassword());
    }

    @Step("Нажать кнопку 'Войти'")
    private void submitLogin() {
        loginPagePOM.clickLoginButton();
    }

    @Step("Перейти в личный кабинет")
    private void goToPersonalAccount() {
        mainPagePOM.clickPersonalAccountButton();
        personalAccountPagePOM = new PersonalAccountPagePOM(driver);
    }

    @Step("Дождаться появления и кликабельности кнопки 'Конструктор'")
    private void waitForConstructorButton() {
        By constructorButton = By.xpath("//button[contains(., 'Конструктор')] | //a[contains(., 'Конструктор')]");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(constructorButton));
        wait.until(ExpectedConditions.elementToBeClickable(constructorButton));
    }

    @Step("Нажать кнопку 'Конструктор'")
    private void clickConstructor() {
        By constructorButton = By.xpath("//button[contains(., 'Конструктор')] | //a[contains(., 'Конструктор')]");
        WebElement button = driver.findElement(constructorButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
    }

    @Step("Дождаться появления и кликабельности кнопки 'Выход'")
    private void waitForLogoutButton() {
        By logoutButton = By.xpath("//button[contains(., 'Выход')]");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(logoutButton));
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
    }

    @Step("Нажать кнопку 'Выйти'")
    private void clickLogoutButton() {
        By logoutButton = By.xpath("//button[contains(., 'Выход')]");
        WebElement button = driver.findElement(logoutButton);

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    @Step("Проверить, что пользователь вышел из аккаунта")
    private void verifyLogout() {
        By loginButton = By.xpath("//button[text()='Войти'] | //a[text()='Войти']");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
    }

    // ---------------------- Тесты ----------------------
    @Test
    @DisplayName("Переход в личный кабинет с динамическим пользователем")
    public void createUserAndNavigateToPersonalAccount() {
        UserData userData = generateRandomUser();

        Response registerResponse = createNewUser(userData);
        registerResponse.then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));

        Response loginResponse = loginUser(userData);
        checkUserLoginSuccessfully(loginResponse, userData);
        accessToken = extractAccessToken(loginResponse);

        openMainPage();
        mainPagePOM.clickPersonalAccountButton();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email']/following-sibling::input")));

        loginPagePOM = new LoginPagePOM(driver);
        enterLoginCredentials(userData);
        submitLogin();
    }

    @Test
    @DisplayName("Переход из личного кабинета в конструктор с динамическим пользователем")
    public void navigateToBuilder() {
        UserData userData = generateRandomUser();

        openLoginPage();
        enterLoginCredentials(userData);
        submitLogin();
        goToPersonalAccount();
        waitForConstructorButton();
        clickConstructor();
    }

    @Test
    @DisplayName("Выход из личного кабинета через кнопку 'Выйти' с динамическим пользователем")
    public void logoutFromPersonalAccount() {
        UserData userData = generateRandomUser();

        openLoginPage();
        enterLoginCredentials(userData);
        submitLogin();
        goToPersonalAccount();
        waitForLogoutButton();
        clickLogoutButton();
        verifyLogout();
    }
}