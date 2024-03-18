package org.tafta.taftaapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Gathariki Ngigi
 * Created on December 27, 2023.
 * Time 0916h
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property {
    @JsonProperty("company")
    private String company;

    @JsonProperty(value = "county", required = true)
    private String county;

    @JsonProperty(value = "latitude", required = true)
    private String latitude;

    @JsonProperty(value = "location", required = true)
    private String location;

    @JsonProperty(value = "longitude", required = true)
    private String longitude;

    @JsonProperty("maximum_price")
    private String maximum_price;

    @JsonProperty("metadata")
    private Object metadata;

    @JsonProperty("minimum_price")
    private String minimum_price;

    @JsonProperty(value = "photos", required = true)
    private List<MultipartFile> photos;

    @JsonProperty("price")
    private String price;

    @JsonProperty(value = "price_range", required = true)
    private Boolean price_range;

    @JsonProperty(value = "amenities", required = true)
    private String amenities;

    @JsonProperty(value = "description", required = true)
    private String description;

    @JsonProperty(value = "property_id", required = true)
    private String property_id;

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonProperty(value = "type", required = true)
    private String type;

    @JsonProperty(value = "status", defaultValue = "ACTIVE")
    private String status;

    @JsonProperty(value = "verified", defaultValue = "false")
    private String verified;
}