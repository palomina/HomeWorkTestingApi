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

public class UnAuthRequests {
    public static Properties properties;

    private static String username = null;
    private static String clientId = null;

    public static void setProperties(Properties properties) {
        UnAuthRequests.properties = properties;
        username = properties.getProperty("username");
        clientId = properties.getProperty("clientId");
    }

    public static RequestSpecification authorization()
    {
        return new RequestSpecBuilder()
                .addHeader("Authorization", "Client-ID " + clientId)
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

    public static void uploadEmptyData(UploadType type) {
        CommonRequests.invalidUpload(type, "", authorization(), Responses.error());
    }


    public static ImageInfoResponse imageInfo(String imageDeleteHash) {
        return given()
                .spec(authorization())
                .get(Endpoints.IMAGE_INFO, imageDeleteHash)
                .then()
                .spec(Responses.success())
                .extract()
                .body()
                .as(ImageInfoResponse.class);
    }

}
