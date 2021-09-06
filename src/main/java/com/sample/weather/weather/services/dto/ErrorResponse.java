package com.sample.weather.weather.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    @JsonProperty("statusCode")
    private int status;
    private String message;
    private String uri;
    public ErrorResponse(){}
    public ErrorResponse(int status, String message, String uri) {
        this.status = status;
        this.message = message;
        this.uri = uri;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
