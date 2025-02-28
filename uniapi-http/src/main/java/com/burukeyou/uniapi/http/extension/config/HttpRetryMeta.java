package com.burukeyou.uniapi.http.extension.config;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRetryMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * enable retry
     */
    private Boolean enable;

    /**
     *  the maximum number of attempts , if less than 0, it means unlimited until the processing is successful
     */
    private Integer maxAttempts;

    /**
     * the delay in milliseconds between retries
     */
    private Long delay;

    /**
     * Retries are performed only when a specified exception type occurs，By default, all exceptions are retried
     *
     * @return exception types to retry
     */
    private List<String> includeException;

    /**
     * Exception types that are not retryable.
     *
     * @return exception types to stop retry
     */
    private List<String> excludeException;

}
