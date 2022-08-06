package com.higgs.common.handler;

import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.HashMap;

@NoArgsConstructor
public class HandlerResponse extends HashMap<String, Object> {
    @Serial
    private static final long serialVersionUID = 181L;

    public boolean isExpected() {
        return (boolean) this.get("expected");
    }

    public void setExpected(final boolean expected) {
        this.put("expected", expected);
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
