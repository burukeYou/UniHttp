package com.burukeyou.uniapi.http.extension.config;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionPoolConfig implements Serializable {

    private String maxPoolSize;

    private String keepAliveTime;
}
