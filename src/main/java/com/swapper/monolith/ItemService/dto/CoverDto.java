package com.swapper.monolith.ItemService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverDto {

    Long id;

    String url;

    public void setUrl(String url) {
        this.url = url != null && url.startsWith("//") ? "https:" + url : url;
    }

    @JsonProperty("image_id")
    String imageId;

    Integer width;

    Integer height;

    Boolean animated;

    @JsonProperty("alpha_channel")
    Boolean alphaChannel;
}
