import com.github.javafaker.Faker;
import pageobject.RegisterPagePOM;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static io.restassured.RestAssured.given;

public class RegisterPageTests {

    private WebDriver driver;
    private RegisterPagePOM registerPagePOM;
    private BrowserFactory browserFactory;
    private static String accessToken;

    @BeforeEach
    void setUp() {
        String browser = System.getProperty("browser", "chrome");
        browserFactory = new BrowserFactory();
        driver = browserFactory.getWebDriver(browser);
        driver.get("https://stellarburgers.education-services.ru/register");
        registerPagePOM = new RegisterPagePOM(driver);
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
        }
    }


    private UserData generateRandomUser() {
        Faker faker = new Faker();
        return new UserData(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12),
                faker.name().firstName()
        );
    }


    @Test
    @DisplayName("Проверка успешной регистрации и последующего удаления через API")
    public void createUserAndLogin() {
        UserData userData = generateRandomUser();

        registerPagePOM.enterName(userData.getName());
        registerPagePOM.enterEmail(userData.getEmail());
        registerPagePOM.enterPassword(userData.getPassword());
        registerPagePOM.clickRegister();

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"email\":\"" + userData.getEmail() + "\",\"password\":\"" + userData.getPassword() + "\"}")
                .when()
                .post("https://stellarburgers.education-services.ru/api/auth/login");

        accessToken = extractAccessToken(loginResponse);
        Assertions.assertNotNull(accessToken, "accessToken не должен быть null");
    }

    @Test
    @DisplayName("Проверка ошибки при вводе короткого пароля (меньше 6 символов)")
    public void shouldShowErrorForShortPassword() {
        registerPagePOM.enterName("Женя");
        registerPagePOM.enterEmail("testshortpass@gmail.com");
        registerPagePOM.enterPassword("12345");
        registerPagePOM.clickRegister();

        String errorText = registerPagePOM.getPasswordErrorText();
        Assertions.assertEquals(
                "Некорректный пароль",
                errorText,
                "Ожидалось сообщение об ошибке для короткого пароля"
        );
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
}