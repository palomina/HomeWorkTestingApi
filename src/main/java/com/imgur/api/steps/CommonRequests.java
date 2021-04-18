package com.imgur.api.steps;

import com.imgur.api.Endpoints;
import com.imgur.api.enums.UploadType;
import com.imgur.api.json.ImageInfoResponse;
import com.imgur.api.json.UploadResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.io.File;

import static io.restassured.RestAssured.given;

public class CommonRequests {
    public static ImageInfoResponse imageInfo(String imageHash, RequestSpecification requestSpecification) {
        return given()
                .spec(requestSpecification)
                .get(Endpoints.IMAGE_INFO, imageHash)
                .then()
                .spec(Responses.success())
                .extract()
                .body()
                .as(ImageInfoResponse.class);
    }


    public static UploadResponse upload(File file, RequestSpecification requestSpecification) {
        return given()
                .spec(requestSpecification)
                .multiPart("type", UploadType.FILE)
                .multiPart("image", file)
                .when()
                .post(Endpoints.IMAGE)
                .then()
                .spec(Responses.success())
                .extract()
                .body()
                .as(UploadResponse.class);
    }

    public static UploadResponse upload(UploadType type, String file, RequestSpecification requestSpecification) {
        return given()
                .spec(requestSpecification)
                .multiPart("type", type)
                .multiPart("image", file)
                .when()
                .post(Endpoints.IMAGE)
                .prettyPeek()
                .then()
                .spec(Responses.success())
                .extract()
                .body()
                .as(UploadResponse.class);
    }

    public static void invalidUpload(UploadType type, String file, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        given()
            .spec(requestSpecification)
            .multiPart("type", type)
            .multiPart("image", file)
            .when()
            .post(Endpoints.IMAGE)
            .then()
            .spec(responseSpecification);
    }

    public static void invalidUpload(UploadType type, File file, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        given()
                .spec(requestSpecification)
                .multiPart("type", type)
                .multiPart("image", file)
                .when()
                .post(Endpoints.IMAGE)
                .then()
                .spec(responseSpecification);
    }



}
