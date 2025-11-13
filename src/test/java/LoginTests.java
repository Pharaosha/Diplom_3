import Page_Object.ForgotPasswordPagePOM;
import Page_Object.LoginPagePOM;
import Page_Object.RegisterPagePOM;
import Page_Object.MainPagePOM;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static io.restassured.RestAssured.given;

public class LoginTests {

    private WebDriver driver;
    private RegisterPagePOM registerPagePOM;
    private MainPagePOM mainPagePOM;
    private LoginPagePOM loginPagePOM;
    private ForgotPasswordPagePOM forgotPasswordPagePOM;
    private static String accessToken;
    private BrowserFactory browserFactory;

    @BeforeEach
    void setUp() {
        browserFactory = new BrowserFactory();
        driver = browserFactory.getWebDriver("chrome");
        registerPagePOM = new RegisterPagePOM(driver);
        mainPagePOM = new MainPagePOM(driver);
        forgotPasswordPagePOM = new ForgotPasswordPagePOM(driver);
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
        }
    }

    @Test
    @DisplayName("Вход через кнопку в форме регистрации с безопасным ожиданием")
    public void loginFromRegisterPage() {
        driver.get("https://stellarburgers.education-services.ru/register");
        String email = "evgenpharaosha@gmail.com";
        String password = "12345678";


        registerPagePOM.enterName("Женя");
        registerPagePOM.enterEmail(email);
        registerPagePOM.enterPassword(password);
        registerPagePOM.clickRegister();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email']/following-sibling::input")));

        loginPagePOM = new LoginPagePOM(driver);
        loginPagePOM.enterEmail(email);
        loginPagePOM.enterPassword(password);
        loginPagePOM.clickLoginButton();

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("https://stellarburgers.education-services.ru/api/auth/login");

        accessToken = extractAccessToken(loginResponse);
        Assertions.assertNotNull(accessToken, "accessToken не должен быть null");
    }

    public static String extractAccessToken(Response response) {
        String token = response.then().extract().path("accessToken");
        return token != null ? token.replace("Bearer ", "") : null;
    }

    public static void deleteUser(String accessToken) {
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("https://stellarburgers.education-services.ru/api/auth/user");

        if (response.statusCode() != 202) {
            System.out.println("Ошибка при удалении пользователя:");
            response.prettyPrint();
        }

        response.then().assertThat().statusCode(202);
    }

    @Test
    @DisplayName("Вход через кнопку 'Войти в аккаунт' на главной")
    public void loginFromMainButton() {
        String email = "evgenpharaosha@gmail.com";
        String password = "12345678";

        driver.get("https://stellarburgers.education-services.ru");
        mainPagePOM.clickLoginButtonMain();
        loginPagePOM = new LoginPagePOM(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email']/following-sibling::input")));

        loginPagePOM.enterEmail(email);
        loginPagePOM.enterPassword(password);
        loginPagePOM.clickLoginButton();
    }

    @Test
    @DisplayName("Вход через кнопку Личный кабинет")
    public void loginFromPersonalAccountButton() {
        String email = "evgenpharaosha@gmail.com";
        String password = "12345678";

        driver.get("https://stellarburgers.education-services.ru");
        mainPagePOM.clickPersonalAccountButton();
        loginPagePOM = new LoginPagePOM(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email']/following-sibling::input")));

        loginPagePOM.enterEmail(email);
        loginPagePOM.enterPassword(password);
        loginPagePOM.clickLoginButton();
    }

    @Test
    @DisplayName("Вход через кнопку Восстановить пароль")
    public void loginFromResetPasswordButton() {
        String email = "evgenpharaosha@gmail.com";
        String password = "12345678";

        driver.get("https://stellarburgers.education-services.ru/login");

        loginPagePOM = new LoginPagePOM(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email']/following-sibling::input")));

        loginPagePOM.clickResetPasswordButton();
        forgotPasswordPagePOM = new ForgotPasswordPagePOM(driver);

        forgotPasswordPagePOM.clickLoginLinkButton();
        loginPagePOM.enterEmail(email);
        loginPagePOM.enterPassword(password);
        loginPagePOM.clickLoginButton();
    }
}
