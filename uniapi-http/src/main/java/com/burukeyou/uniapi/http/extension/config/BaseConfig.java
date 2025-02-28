package com.burukeyou.uniapi.http.extension.config;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseConfig implements Serializable {

    private String url;
    private String processor;
    private String jsonConverter;
    private String xmlConverter;

    private ConnectionPoolConfig poolConfig;
}
