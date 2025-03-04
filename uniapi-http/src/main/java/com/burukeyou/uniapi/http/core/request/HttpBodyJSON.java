package com.burukeyou.uniapi.http.core.request;

import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author caizhihao
 */
@Setter
@Getter
public class HttpBodyJSON extends HttpBody {

    private String bodyJson;

    public HttpBodyJSON(String bodyJson) {
        super(MediaTypeEnum.APPLICATION_JSON.getType());
        this.bodyJson = bodyJson;
    }

    @Override
    public boolean emptyContent() {
        return StringUtils.isBlank(bodyJson);
    }

    @Override
    public String toStringBody() {
        return bodyJson;
    }

}
