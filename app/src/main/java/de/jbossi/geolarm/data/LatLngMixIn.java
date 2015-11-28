package de.jbossi.geolarm.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Johannes on 08.07.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class LatLngMixIn {
    LatLngMixIn(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude) {
    }
}
