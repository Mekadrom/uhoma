package com.higgs.common.handler.http;

import com.higgs.common.handler.HandlerResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HttpHandlerResponse extends HandlerResponse {
    @Serial
    private static final long serialVersionUID = 180L;

    public HttpHandlerResponse(final HttpHandlerRequest requestFor) {
        this.setExpected(requestFor.isReturnResponse());
        this.setToNodeSeq(requestFor.getToNodeSeq());
        this.setFromNodeSeq(requestFor.getFromNodeSeq());
        this.setToUsername(requestFor.getToUsername());
        this.setFromUsername(requestFor.getFromUsername());
    }

    public int getResponseCode() {
        return (int) this.get("responseCode");
    }

    public void setResponseCode(final int responseCode) {
        this.put("responseCode", responseCode);
    }

    public Map<String, List<String>> getHeaders() {
        return (Map<String, List<String>>) this.get("headers");
    }

    public void setHeaders(final Map<String, List<String>> headers) {
        this.put("headers", headers);
    }

    public String getBody() {
        return (String) this.get("body");
    }

    public void setBody(final String body) {
        this.put("body", body);
    }
}
