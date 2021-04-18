package com.imgur.api.enums;

public enum UploadType {
    FILE("file"),
    URL("url"),
    BASE64("base64"),
    UNKNOWN("unknown");

    UploadType(String label) {
    }
}
