import Page_Object.*;
import Page_Object.ConstructorPagePOM;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;


import static io.restassured.RestAssured.given;


public class ConstructorSectionNavigationTests {

    private WebDriver driver;
    private ConstructorPagePOM constructorPagePOM;
    private static String accessToken;
    private MainPagePOM mainPagePOM;
    private LoginPagePOM loginPagePOM;
    private BrowserFactory browserFactory;



    @BeforeEach
    void setUp() {
        browserFactory = new BrowserFactory();
        driver = browserFactory.getWebDriver("chrome");
        mainPagePOM = new MainPagePOM(driver);
        constructorPagePOM = new ConstructorPagePOM(driver);
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
    @DisplayName("Проверка переходов между разделами конструктора")
    public void constructorSectionNavigation() {
        UserData userData = new UserData("evgenpharaosha@gmail.com", "12345678", "Rengoku");

        loginUser(userData);
        openConstructor();

        navigateToBuns();
        navigateToSauces();
        navigateToFillings();
    }

    @Step("Логинимся под пользователем")
    private void loginUser(UserData userData) {
        driver.get("https://stellarburgers.education-services.ru/login");
        loginPagePOM = new LoginPagePOM(driver);
        loginPagePOM.enterEmail(userData.getEmail());
        loginPagePOM.enterPassword(userData.getPassword());
        loginPagePOM.clickLoginButton();
    }

    @Step("Открываем конструктор")
    private void openConstructor() {
        mainPagePOM = new MainPagePOM(driver);
        mainPagePOM.clickConstructorButton();
        constructorPagePOM = new ConstructorPagePOM(driver);
    }

    @Step("Перейти в раздел 'Булки'")
    private void navigateToBuns() {
        constructorPagePOM.clickBunsTab();
        Assertions.assertTrue(constructorPagePOM.isTabActive("Булки"), "Вкладка 'Булки' не активна");
    }

    @Step("Перейти в раздел 'Соусы'")
    private void navigateToSauces() {
        constructorPagePOM.clickSaucesTab();
        Assertions.assertTrue(constructorPagePOM.isTabActive("Соусы"), "Вкладка 'Соусы' не активна");
    }

    @Step("Перейти в раздел 'Начинки'")
    private void navigateToFillings() {
        constructorPagePOM.clickFillingsTab();
        Assertions.assertTrue(constructorPagePOM.isTabActive("Начинки"), "Вкладка 'Начинки' не активна");
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
