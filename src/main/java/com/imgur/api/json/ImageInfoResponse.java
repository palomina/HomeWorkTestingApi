
package com.imgur.api.json;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "success",
    "status"
})
public class ImageInfoResponse {

    @JsonProperty("data")
    private DataImage data;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("status")
    private Integer status;

    @JsonProperty("data")
    public DataImage getData() {
        return data;
    }

    @JsonProperty("success")
    public Boolean getSuccess() {
        return success;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

}
