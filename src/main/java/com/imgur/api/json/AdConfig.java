
package com.imgur.api.json;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "safeFlags",
    "highRiskFlags",
    "unsafeFlags",
    "wallUnsafeFlags",
    "showsAds"
})
public class AdConfig {

    @JsonProperty("safeFlags")
    private List<String> safeFlags = new ArrayList<String>();
    @JsonProperty("highRiskFlags")
    private List<Object> highRiskFlags = new ArrayList<Object>();
    @JsonProperty("unsafeFlags")
    private List<String> unsafeFlags = new ArrayList<String>();
    @JsonProperty("wallUnsafeFlags")
    private List<Object> wallUnsafeFlags = new ArrayList<Object>();
    @JsonProperty("showsAds")
    private Boolean showsAds;

    @JsonProperty("safeFlags")
    public List<String> getSafeFlags() {
        return safeFlags;
    }

    @JsonProperty("highRiskFlags")
    public List<Object> getHighRiskFlags() {
        return highRiskFlags;
    }

    @JsonProperty("unsafeFlags")
    public List<String> getUnsafeFlags() {
        return unsafeFlags;
    }

    @JsonProperty("wallUnsafeFlags")
    public List<Object> getWallUnsafeFlags() {
        return wallUnsafeFlags;
    }

    @JsonProperty("showsAds")
    public Boolean getShowsAds() {
        return showsAds;
    }

}
