package com.higgs.common.handler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class HandlerRequest extends HashMap<String, Object> {
    @Serial
    private static final long serialVersionUID = 329L;

    protected HandlerRequest(final Map<String, Object> input) {
        super(input);
    }

    public boolean isReturnResponse() {
        return (boolean) this.get("return_response");
    }

    public void setReturnResponse(final boolean returnResponse) {
        this.put("return_response", returnResponse);
    }

    public Long getToNodeSeq() {
        return (Long) this.get("toNodeSeq");
    }

    public void setToNodeSeq(final Long toNodeSeq) {
        this.put("toNodeSeq", toNodeSeq);
    }

    public Long getFromNodeSeq() {
        return (Long) this.get("fromNodeSeq");
    }

    public void setFromNodeSeq(final Long fromNodeSeq) {
        this.put("fromNodeSeq", fromNodeSeq);
    }

    public String getToUsername() {
        return (String) this.get("toUsername");
    }

    public void setToUsername(final String toUsername) {
        this.put("toUsername", toUsername);
    }

    public String getFromUsername() {
        return (String) this.get("fromUsername");
    }

    public void setFromUsername(final String fromUsername) {
        this.put("fromUsername", fromUsername);
    }
}
