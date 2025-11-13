import Page_Object.LoginPagePOM;
import Page_Object.MainPagePOM;
import Page_Object.PersonalAccountPagePOM;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
        browserFactory = new BrowserFactory();
        driver = browserFactory.getWebDriver("chrome");
        mainPagePOM = new MainPagePOM(driver);
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/";
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    @DisplayName("Удаление пользователя после тестов")
    public static void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Переход в личный кабинет")
    public void createUserAndNavigateToPersonalAccount() {
        UserData userData = new UserData("evgenpharaosha@gmail.com", "12345678", "Rengoku");
        Response registerResponse = createNewUser(userData);
        registerResponse.then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
        Response loginResponse = loginUser(userData);
        checkUserLoginSuccessfully(loginResponse, userData);
        accessToken = extractAccessToken(loginResponse);

        openMainPage();
        clickPersonalAccountButton();
        waitForLoginForm();
        enterCredentialsAndLogin(userData);


    }

    @Step("Отправить POST-запрос на создание пользователя (endpoint: /api/auth/register)")
    public Response createNewUser(UserData userData) {
        return given()
                .header("Content-type", "application/json")
                .body(userData)
                .when()
                .post("/api/auth/register");
    }

    @Step("Отправить POST-запрос на логин пользователя (endpoint: /api/auth/login)")
    public Response loginUser(UserData userData) {
        return given()
                .header("Content-type", "application/json")
                .body(userData)
                .when()
                .post("/api/auth/login");
    }

    @Step("Проверить, что пользователь успешно залогинен (status code = 200, success = true)")
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

    @Step("Удалить пользователя по accessToken (endpoint: /api/auth/user)")
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

    @Step("Открыть главную страницу")
    private void openMainPage() {
        driver.get("https://stellarburgers.education-services.ru");
    }

    @Step("Кликнуть по кнопке 'Личный кабинет'")
    private void clickPersonalAccountButton() {
        mainPagePOM.clickPersonalAccountButton();
    }

    @Step("Дождаться появления формы логина")
    private void waitForLoginForm() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Email']/following-sibling::input")));
    }

    @Step("Ввести email и пароль и кликнуть 'Войти'")
    private void enterCredentialsAndLogin(UserData userData) {

        loginPagePOM = new LoginPagePOM(driver);
        loginPagePOM.enterEmail(userData.getEmail());
        loginPagePOM.enterPassword(userData.getPassword());
        loginPagePOM.clickLoginButton();
    }

    @Test
    @DisplayName("Переход из личного кабинета в конструктор")
    public void navigateToBuilder() {
        UserData userData = new UserData("evgenpharaosha@gmail.com", "12345678", "Rengoku");

        openLoginPage();
        enterLoginCredentials(userData);
        submitLogin();
        goToPersonalAccount();
        waitForConstructorButton();
        clickConstructor();
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

    @Step("Дождаться появления кнопки 'Конструктор'")
    private void waitForConstructorButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Конструктор')] | //a[contains(., 'Конструктор')]")
        ));
    }

    @Step("Нажать кнопку 'Конструктор'")
    private void clickConstructor() {
        personalAccountPagePOM.clickConstructorButton();
    }


    @Test
    @DisplayName("Выход из личного кабинета через кнопку 'Выйти'")
    public void logoutFromPersonalAccount() {
        UserData userData = new UserData("evgenpharaosha@gmail.com", "12345678", "Rengoku");

        openLoginPage();
        enterLoginCredentials(userData);
        submitLogin();
        goToPersonalAccount();
        waitForLogoutButton();
        clickLogoutButton();
        verifyLogout();
    }


    @Step("Нажать кнопку 'Выйти'")
    private void clickLogoutButton() {
        personalAccountPagePOM.clickLogoutButton();
    }

    @Step("Проверить, что пользователь вышел из аккаунта")
    private void verifyLogout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Войти'] | //a[text()='Войти']")
        ));
    }

    @Step("Дождаться появления кнопки 'Конструктор'")
    private void waitForLogoutButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Выход']")
        ));
    }

}

