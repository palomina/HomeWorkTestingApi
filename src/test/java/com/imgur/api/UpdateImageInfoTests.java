package com.imgur.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateImageInfoTests extends BaseTest {
    static final String INPUT_TXT_FILE_PATH = "src/test/java/resources/1.txt";
    static final String INPUT_IMG_FILE_PATH = "src/test/java/resources/1x1.png";
    static final String INPUT_IMG_FILE_PATH2 = "src/test/java/resources/forest.jpg";
    static final String INPUT_IMG_URL = "https://www.wpbeginner.com/wp-content/uploads/2020/03/ultimate-small-business-resource-coronavirus.png";
    static byte[] fileContent;
    static Properties properties;
    static String token;
    static String username;
    static String clientId;
    private static String imageHash;

    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("src/test/java/resources/application.properties"));

        File inputFile = new File(INPUT_IMG_FILE_PATH);
        try {
            fileContent = readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        token = properties.getProperty("token");
        username = properties.getProperty("username");
        clientId = properties.getProperty("clientId");

        RestAssured.baseURI = properties.getProperty("base.url");

        imageHash = null;
    }


    @AfterEach
    void tearDown() {
        if (imageHash != null) {
            System.out.println("Delete " + imageHash);
            given()
                    .header("Authorization", token)
                    .when()
                    .delete("/account/{username}/image/{deleteHash}", username, imageHash)
                    .prettyPeek()
                    .then()
                    .statusCode(200);
        }
        imageHash = null;
    }

    private RequestSpecification authorizedRequest()
    {
        return given()
                .header("Authorization", token);
    }

    private RequestSpecification unauthorizedRequest()
    {
        return given()
                .header("Authorization", "Client-ID " + clientId);
    }

    private int requestImageCount() {
        String count = authorizedRequest()
                .when()
                .get("account/{username}/images/count", username)
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getString("data");

        return Integer.parseInt(count);
    }

    private Response requestImageIds() {
        return authorizedRequest()
                .when()
                .get("account/{username}/images/ids", username)
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
    }

    private Response requestUpload(File file) {
        return given()
                .multiPart("type", "file")
                .multiPart("image", file)
                .header("Authorization", token)
                .when()
                .post("image")
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
    }

    private Response requestUpload(String type, String file) {
        return given()
                .multiPart("type", type)
                .multiPart("image", file)
                .header("Authorization", token)
                .when()
                .post("image")
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
    }

    private void uploadImageFromBase64(String file) {
        byte[] content = new byte[0];
        File inputFile = new File(file);
        try {
            content = readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileContentBase64 = Base64.encodeBase64String(content);
        Response response = requestUpload("base64", fileContentBase64);

        imageHash =  response.jsonPath().getString("data.id");
    }

    private void uploadImageFromUrl(String url) {
        Response response = requestUpload("url", url);

        imageHash =  response.jsonPath().getString("data.id");
    }

    private void requestAddToFavorite(String expect) {
        given()
                .header("Authorization", token)
                .post("image/{imageHash}/favorite", imageHash)
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data", equalTo(expect));
    }

    private JsonPath requestImageInfo() {
        return given()
                .header("Authorization", token)
                .get("image/{imageHash}", imageHash)
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .extract().jsonPath();
    }

    @Test
    void tc10UploadBase64Test() {
        uploadImageFromBase64(INPUT_IMG_FILE_PATH2);
    }

    @Test
    void tc11UploadFromUrlTest() {
        uploadImageFromUrl(INPUT_IMG_URL);
    }

    @Test
    void tc12AddToFavoriteTest() {
        JsonPath response;
        uploadImageFromBase64(INPUT_IMG_FILE_PATH2);
        requestAddToFavorite("favorited");
        response = requestImageInfo();
        Assertions.assertTrue(response.getBoolean("data.favorite"));
    }

    @Test
    void tc13RemoveFromFavoriteTest() {
        uploadImageFromBase64(INPUT_IMG_FILE_PATH2);
        requestAddToFavorite("favorited");
        Assertions.assertTrue(requestImageInfo().getBoolean("data.favorite"));
        requestAddToFavorite("unfavorited");
        Assertions.assertFalse(requestImageInfo().getBoolean("data.favorite"));
    }

    @Test
    void tc14AddToFavoriteWithInvalidTokenTest() {
        uploadImageFromBase64(INPUT_IMG_FILE_PATH2);

        given()
                .header("Authorization", "invalid token")
                .post("image/{imageHash}/favorite", imageHash)
                .then()
                .statusCode(403)
                .log()
                .ifStatusCodeIsEqualTo(403)
                .body("success", equalTo(false));
    }

    @Test
    void tc15UpdateImageinfoTest() {
        String title = "title";
        String description = "description";

        Response response = given()
                .multiPart("title", title)
                .multiPart("description", description)
                .multiPart("type", "file")
                .multiPart("image", new File(INPUT_IMG_FILE_PATH2))
                .header("Authorization", token)
                .when()
                .post("image")
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        imageHash =  response.jsonPath().getString("data.id");

        title = title + " updated";
        description = description + " updated";

        given()
                .multiPart("title", title)
                .multiPart("description", description)
                .header("Authorization", token)
                .when()
                .post("image/{imageHash}", imageHash)
                .then()
                .statusCode(200)
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON);

        Assertions.assertEquals(title, requestImageInfo().getString("data.title"));
        Assertions.assertEquals(description, requestImageInfo().getString("data.description"));
    }


    @Test
    void tc16DeleteNotExistingImageTest() {
        given()
                .header("Authorization", token)
                .when()
                .delete("/account/{username}/image/{deleteHash}", username, "invalid")
                .prettyPeek()
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON);
    }

    @Test
    void tc17UploadNotImageFileTest() {
        requestUpload(new File(INPUT_TXT_FILE_PATH));
    }


    @Test
    void tc18UploadFileTest() {
        requestUpload(new File(INPUT_IMG_FILE_PATH));
    }

    @Test
    void tc19ImagesCountTest() {
        int imageCountBefore = requestImageCount();

        uploadImageFromBase64(INPUT_IMG_FILE_PATH);

        int imageCountAfter = requestImageCount();

        Assertions.assertEquals(1 + imageCountBefore, imageCountAfter);
    }

    @Test
    void tc20ImageIdsTest() {
        uploadImageFromBase64(INPUT_IMG_FILE_PATH);

        Response response = requestImageIds();

        Assertions.assertEquals(imageHash, response.jsonPath().getString("data[0]"));
    }


}
