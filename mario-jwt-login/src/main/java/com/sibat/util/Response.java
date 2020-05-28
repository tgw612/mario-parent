package com.sibat.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSON model
 */
@Data
@NoArgsConstructor
public class Response {
    private static final String OK = "200";
    private static final String NO_CONTENT = "204";
    private static final String BAD_REQUEST = "400";
    private static final String UNAUTHORIZED = "401";
    private static final String FORBIDDEN = "403";
    private static final String NOT_FOUND = "404";
    private static final String INTERNAL_SERVER_ERROR = "500";
    private String status;
    private String message;
    private Object data;


    public Response success(String status) {
        this.status = status;
        return this;
    }

    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public Response failure(String status) {
        this.status = status;
        return this;
    }

    public Response(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Response(String status) {
        this.status = status;
    }
}
