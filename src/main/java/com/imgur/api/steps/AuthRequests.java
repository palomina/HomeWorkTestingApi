package com.imgur.api.steps;

import com.imgur.api.Endpoints;
import com.imgur.api.enums.UploadType;
import com.imgur.api.json.ImageIdsResponse;
import com.imgur.api.json.ImageInfoResponse;
import com.imgur.api.json.UploadResponse;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;

public class AuthRequests {
    public static Properties properties;

    private static String token = null;
    private static String username = null;
    private static String clientId = null;

    public static void setProperties(Properties properties) {
        AuthRequests.properties = properties;
        token = properties.getProperty("token", System.getProperty("TOKEN", System.getenv("TOKEN")));
        username = properties.getProperty("username", System.getProperty("USERNAME", System.getenv("USERNAME")));
        clientId = properties.getProperty("clientId", System.getProperty("CLIENT_ID", System.getenv("CLIENT_ID")));
    }

    public static RequestSpecification authorization()
    {
        return new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();
    }

    public RequestSpecification invalidToken()
    {
        return new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();
    }

    public static void imageDelete(String imageHash) {
        given()
            .spec(authorization())
            .when()
            .delete(Endpoints.IMAGE_DELETE, imageHash)
            .prettyPeek()
            .then()
            .spec(Responses.success());
    }

    public static void imageDelete(String imageHash, ResponseSpecification response) {
        given()
                .spec(authorization())
                .when()
                .delete(Endpoints.IMAGE_DELETE, imageHash)
                .prettyPeek()
                .then()
                .spec(response);
    }

    public static int requestImageCount() {
        String count = given()
                .spec(authorization())
                .when()
                .get(Endpoints.IMAGES_COUNT, username)
                .then()
                .spec(Responses.success())
                .extract()
                .response()
                .jsonPath()
                .getString("data");

        return Integer.parseInt(count);
    }

    public static ImageIdsResponse requestImageIds() {
        return given()
                .spec(authorization())
                .when()
                .get(Endpoints.IMAGES_IDS, username)
                .then()
                .spec(Responses.success())
                .extract()
                .body()
                .as(ImageIdsResponse.class);
    }

    public static UploadResponse uploadImageFromBase64(String file) {
        byte[] content = new byte[0];
        File inputFile = new File(file);
        try {
            content = readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileContentBase64 = Base64.encodeBase64String(content);
        return CommonRequests.upload(UploadType.BASE64, fileContentBase64, authorization());
    }

    public static UploadResponse uploadImageFromUrl(String url) {
        return CommonRequests.upload(UploadType.URL, url, authorization());
    }

    public static UploadResponse upload(File file) {
        return CommonRequests.upload(file, authorization());
    }


    public static void requestFavorite(String imageHash, String expect) {
        given()
                .spec(authorization())
                .post(Endpoints.IMAGE_FAVORITE, imageHash)
                .then()
                .spec(Responses.success())
                .body("data", equalTo(expect));
    }

    public static void requestAddToFavorite(String imageHash) {
        requestFavorite(imageHash, "favorited");
    }

    public static void requestRemoveFromFavorite(String imageHash) {
        requestFavorite(imageHash, "unfavorited");
    }

    public static ImageInfoResponse requestImageInfo(String imageHash) {
        return CommonRequests.imageInfo(imageHash, authorization());
    }

}
