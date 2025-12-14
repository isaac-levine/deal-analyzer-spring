package com.frontstep.deal_analyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ClerkWebhookEvent {
    private String type;
    private Map<String, Object> data;

    @JsonProperty("object")
    private String objectType;
}
