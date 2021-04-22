
package com.imgur.api.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class DataUpload {

    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private Object title;
    @JsonProperty("description")
    private Object description;
    @JsonProperty("datetime")
    private Integer datetime;
    @JsonProperty("type")
    private String type;
    @JsonProperty("animated")
    private Boolean animated;
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("views")
    private Integer views;
    @JsonProperty("bandwidth")
    private Integer bandwidth;
    @JsonProperty("vote")
    private Object vote;
    @JsonProperty("favorite")
    private Boolean favorite;
    @JsonProperty("nsfw")
    private Object nsfw;
    @JsonProperty("section")
    private Object section;
    @JsonProperty("account_url")
    private Object accountUrl;
    @JsonProperty("account_id")
    private Integer accountId;
    @JsonProperty("is_ad")
    private Boolean isAd;
    @JsonProperty("in_most_viral")
    private Boolean inMostViral;
    @JsonProperty("has_sound")
    private Boolean hasSound;
    @JsonProperty("tags")
    private List<Object> tags = new ArrayList<Object>();
    @JsonProperty("ad_type")
    private Integer adType;
    @JsonProperty("ad_url")
    private String adUrl;
    @JsonProperty("edited")
    private String edited;
    @JsonProperty("in_gallery")
    private Boolean inGallery;
    @JsonProperty("deletehash")
    private String deletehash;
    @JsonProperty("name")
    private String name;
    @JsonProperty("link")
    private String link;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("title")
    public Object getTitle() {
        return title;
    }

    @JsonProperty("description")
    public Object getDescription() {
        return description;
    }

    @JsonProperty("datetime")
    public Integer getDatetime() {
        return datetime;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("animated")
    public Boolean getAnimated() {
        return animated;
    }

    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    @JsonProperty("height")
    public Integer getHeight() {
        return height;
    }

    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    @JsonProperty("views")
    public Integer getViews() {
        return views;
    }

    @JsonProperty("bandwidth")
    public Integer getBandwidth() {
        return bandwidth;
    }

    @JsonProperty("vote")
    public Object getVote() {
        return vote;
    }

    @JsonProperty("favorite")
    public Boolean getFavorite() {
        return favorite;
    }

    @JsonProperty("nsfw")
    public Object getNsfw() {
        return nsfw;
    }

    @JsonProperty("section")
    public Object getSection() {
        return section;
    }

    @JsonProperty("account_url")
    public Object getAccountUrl() {
        return accountUrl;
    }

    @JsonProperty("account_id")
    public Integer getAccountId() {
        return accountId;
    }

    @JsonProperty("is_ad")
    public Boolean getIsAd() {
        return isAd;
    }

    @JsonProperty("in_most_viral")
    public Boolean getInMostViral() {
        return inMostViral;
    }

    @JsonProperty("has_sound")
    public Boolean getHasSound() {
        return hasSound;
    }

    @JsonProperty("tags")
    public List<Object> getTags() {
        return tags;
    }

    @JsonProperty("ad_type")
    public Integer getAdType() {
        return adType;
    }

    @JsonProperty("ad_url")
    public String getAdUrl() {
        return adUrl;
    }

    @JsonProperty("edited")
    public String getEdited() {
        return edited;
    }

    @JsonProperty("in_gallery")
    public Boolean getInGallery() {
        return inGallery;
    }

    @JsonProperty("deletehash")
    public String getDeletehash() {
        return deletehash;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

}
