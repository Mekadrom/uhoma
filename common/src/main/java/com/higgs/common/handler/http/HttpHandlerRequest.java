package com.higgs.common.handler.http;

import com.higgs.common.handler.HandlerRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.io.Serial;
import java.util.Map;

@Getter
@Setter
public class HttpHandlerRequest extends HandlerRequest {
    @Serial
    private static final long serialVersionUID = 214L;

    private HttpMethod httpMethod;
    private String connectType;
    private String url;
    private String port;
    private String endpoint;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String body;
}

