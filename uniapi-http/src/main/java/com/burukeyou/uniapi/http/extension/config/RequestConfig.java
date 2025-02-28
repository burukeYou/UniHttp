package com.burukeyou.uniapi.http.extension.config;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestConfig implements Serializable {
    
    private String url;
    private String processor;
    private String jsonConverter;
    private String xmlConverter;

    private String path;
    private String method;
    private Map<String,String> headers;
    private String contentType;
    private Map<String,String> params;
    private String cookie;

    // timeout
    private TimeOutConfig timeOutConfig;

    //
    private boolean async = false;

    // retry
    private RetryConfig retryConfig;
}
