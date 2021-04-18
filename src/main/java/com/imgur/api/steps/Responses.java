package com.imgur.api.steps;


import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;

import static org.hamcrest.CoreMatchers.equalTo;

public class Responses  {

    public static ResponseSpecification success() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();
    }

    public static ResponseSpecification error() {
        return new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();
    }

    public static ResponseSpecification errorInvalidAuth() {
        return new ResponseSpecBuilder()
                .expectStatusCode(401)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();
    }

    public static ResponseSpecification errorInvalidToken() {
        return new ResponseSpecBuilder()
                .expectStatusCode(403)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .expectBody("success", equalTo(false))
                .build();
    }

}
