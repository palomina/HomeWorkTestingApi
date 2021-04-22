package com.imgur.api;

import com.imgur.api.enums.UploadType;
import com.imgur.api.json.UploadResponse;
import com.imgur.api.steps.AuthRequests;
import com.imgur.api.steps.CommonRequests;
import com.imgur.api.steps.Responses;
import com.imgur.api.steps.UnAuthRequests;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;


@DisplayName("Тестирование изображений")
public class ImageTests extends BaseTest {
    private static String imageHash;
    private static String imageDeleteHash;

    @BeforeAll
    static void beforeAll() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(Files.PROPERTIES));

        RestAssured.baseURI = properties.getProperty("base.url", System.getProperty("BASE_URL", System.getenv("BASE_URL")));

        RestAssured.filters(new AllureRestAssured());

        AuthRequests.setProperties(properties);
        UnAuthRequests.setProperties(properties);

        imageHash = null;
        imageDeleteHash = null;
    }

    @AfterEach
    void tearDown() {
        if (imageHash != null) {
            AuthRequests.imageDelete(imageHash);
        }
        imageHash = null;

        if (imageDeleteHash != null) {
            UnAuthRequests.imageDelete(imageDeleteHash);
        }
        imageDeleteHash = null;
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Загрузка base64")
    @DisplayName("Загрузка base64")
    void tc01UploadBase64Test() {
        imageDeleteHash = UnAuthRequests.uploadImageFromBase64(Files.IMG_FILE_PATH).getData().getDeletehash();
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Загрузка по url")
    @DisplayName("Загрузка по url")
    void tc02UploadFromUrlTest() {
        imageDeleteHash = UnAuthRequests.uploadImageFromUrl(Files.IMG_URL).getData().getDeletehash();
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Загрузка по пустого url")
    @DisplayName("Загрузка по пустого url")
    void tc03UploadEmptyUrlTest() {
        UnAuthRequests.uploadEmptyData(UploadType.URL);
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Загрузка по пустого base64")
    @DisplayName("Загрузка по пустого base64")
    void tc04UploadEmptyBase64Test() {
        UnAuthRequests.uploadEmptyData(UploadType.BASE64);
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Загрузка по неизвестного type")
    @DisplayName("Загрузка по неизвестного type")
    void tc05UploadInvalidTypeTest() {
        UnAuthRequests.uploadEmptyData(UploadType.UNKNOWN);
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Загрузка по битого base64")
    @DisplayName("Загрузка по битого base64")
    void tc06UploadBrokenBase64Test() {
        CommonRequests.invalidUpload(UploadType.BASE64, "broken", UnAuthRequests.authorization(), Responses.error());
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Загрузка с неправильным Client-ID")
    @DisplayName("Загрузка с неправильным Client-ID")
    void tc07UploadWithInvalidClientIdTest() {
        CommonRequests.invalidUpload(
                UploadType.URL,
                Files.IMG_URL,
                new RequestSpecBuilder().addHeader("Authorization", "Client-ID invalid").build(),
                Responses.errorInvalidToken()
        );
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Загрузка без Client-ID")
    @DisplayName("Загрузка без Client-ID")
    void tc08UploadWithoutClientIdTest() {
        CommonRequests.invalidUpload(
                UploadType.URL,
                Files.IMG_URL,
                new RequestSpecBuilder().build(),
                Responses.errorInvalidAuth()
        );
    }

    @Test
    @Feature("Неавторизованные запросы")
    @Description("Обновление данных избражения")
    @DisplayName("Обновление данных избражения")
    void tc09UpdateImageInfoTest() {
        String title = "title";
        String description = "description";

        UploadResponse uploadResponse = given()
                .spec(UnAuthRequests.authorization())
                .multiPart("title", title)
                .multiPart("description", description)
                .multiPart("type", UploadType.FILE)
                .multiPart("image", new File(Files.IMG_FILE_PATH))
                .when()
                .post(Endpoints.IMAGE)
                .then()
                .spec(Responses.success())
                .extract()
                .body()
                .as(UploadResponse.class);
        imageHash = uploadResponse.getData().getId();
        imageDeleteHash = uploadResponse.getData().getDeletehash();

        title = title + " updated";
        description = description + " updated";

        given()
                .spec(UnAuthRequests.authorization())
                .multiPart("title", title)
                .multiPart("description", description)
                .when()
                .post(Endpoints.IMAGE_INFO, imageDeleteHash)
                .then()
                .spec(Responses.success());

        Assertions.assertEquals(title, UnAuthRequests.imageInfo(imageHash).getData().getTitle());
        Assertions.assertEquals(description, UnAuthRequests.imageInfo(imageHash).getData().getDescription());
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Загрузка base64")
    @DisplayName("Загрузка base64")
    void tc10UploadBase64Test() {
        imageHash = AuthRequests.uploadImageFromBase64(Files.IMG_FILE_PATH).getData().getId();
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Загрузка по url")
    @DisplayName("Загрузка по url")
    void tc11UploadFromUrlTest() {
        imageHash = AuthRequests.uploadImageFromUrl(Files.IMG_URL).getData().getId();
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Добавление в список Favorite")
    @DisplayName("Добавление в список Favorite")
    void tc12AddToFavoriteTest() {
        imageHash = AuthRequests.uploadImageFromBase64(Files.IMG_FILE_PATH).getData().getId();
        AuthRequests.requestAddToFavorite(imageHash);
        Assertions.assertTrue(AuthRequests.requestImageInfo(imageHash).getData().getFavorite());
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Удаление из списка Favorite")
    @DisplayName("Удаление из списка Favorite")
    void tc13RemoveFromFavoriteTest() {
        imageHash = AuthRequests.uploadImageFromBase64(Files.IMG_FILE_PATH).getData().getId();
        AuthRequests.requestAddToFavorite(imageHash);
        Assertions.assertTrue(AuthRequests.requestImageInfo(imageHash).getData().getFavorite());
        AuthRequests.requestRemoveFromFavorite(imageHash);
        Assertions.assertFalse(AuthRequests.requestImageInfo(imageHash).getData().getFavorite());
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Добавление в список Favorite с инвалидным токеном")
    @DisplayName("Добавление в список Favorite с инвалидным токеном")
    void tc14AddToFavoriteWithInvalidTokenTest() {
        imageHash = AuthRequests.uploadImageFromBase64(Files.IMG_FILE_PATH).getData().getId();

        given()
                .header("Authorization", "invalid token")
                .post(Endpoints.IMAGE_FAVORITE, imageHash)
                .then()
                .spec(Responses.errorInvalidToken());
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Обновление данных избражения")
    @DisplayName("Обновление данных избражения")
    void tc15UpdateImageInfoTest() {
        String title = "title";
        String description = "description";

        UploadResponse uploadResponse = given()
                .spec(AuthRequests.authorization())
                .multiPart("title", title)
                .multiPart("description", description)
                .multiPart("type", UploadType.FILE)
                .multiPart("image", new File(Files.IMG_FILE_PATH))
                .when()
                .post(Endpoints.IMAGE)
                .then()
                .spec(Responses.success())
                .extract()
                .body()
                .as(UploadResponse.class);
        imageHash = uploadResponse.getData().getId();

        title = title + " updated";
        description = description + " updated";

        given()
                .spec(AuthRequests.authorization())
                .multiPart("title", title)
                .multiPart("description", description)
                .when()
                .post(Endpoints.IMAGE_INFO, imageHash)
                .then()
                .spec(Responses.success());

        Assertions.assertEquals(title, AuthRequests.requestImageInfo(imageHash).getData().getTitle());
        Assertions.assertEquals(description, AuthRequests.requestImageInfo(imageHash).getData().getDescription());
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Удаление несуществующего изображения")
    @DisplayName("Удаление несуществующего изображения")
    void tc16DeleteNotExistingImageTest() {
        AuthRequests.imageDelete("invalid", Responses.error());
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Загрузка не изображения")
    @DisplayName("Загрузка не изображения")
    void tc17UploadNotImageFileTest() {
         CommonRequests.invalidUpload(UploadType.FILE, new File(Files.TXT_FILE_PATH), AuthRequests.authorization(), Responses.error());
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Загрузка файла")
    @DisplayName("Загрузка файла")
    void tc18UploadFileTest() {
        imageHash = AuthRequests.upload(new File(Files.IMG_FILE_PATH)).getData().getId();
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Проверка количетсва изображений")
    @DisplayName("Проверка количетсва изображений")
    void tc19ImagesCountTest() {
        int imageCountBefore = AuthRequests.requestImageCount();

        imageHash = AuthRequests.uploadImageFromBase64(Files.IMG_FILE_PATH).getData().getId();

        int imageCountAfter = AuthRequests.requestImageCount();

        Assertions.assertEquals(1 + imageCountBefore, imageCountAfter);
    }

    @Test
    @Feature("Авторизованные запросы")
    @Description("Проверка ids изображений")
    @DisplayName("Проверка ids изображений")
    void tc20ImageIdsTest() {
        imageHash = AuthRequests.uploadImageFromBase64(Files.IMG_FILE_PATH).getData().getId();

        Assertions.assertEquals(imageHash, AuthRequests.requestImageIds().getData().get(0));
    }

}
