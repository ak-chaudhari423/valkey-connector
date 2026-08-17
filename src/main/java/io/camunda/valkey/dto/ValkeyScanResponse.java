package io.camunda.valkey.dto;


import java.util.List;

public record ValkeyScanResponse(
        String cursor,
        List<String> keys
) {
}