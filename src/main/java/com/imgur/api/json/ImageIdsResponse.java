package com.imgur.api.json;

import java.util.ArrayList;
import java.util.List;
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
public class ImageIdsResponse {

    @JsonProperty("data")
    private List<String> data = new ArrayList<String>();
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("status")
    private Integer status;

    @JsonProperty("data")
    public List<String> getData() {
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