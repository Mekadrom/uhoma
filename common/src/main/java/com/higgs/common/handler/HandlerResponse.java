package com.higgs.common.handler;

import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class HandlerResponse extends HandlerMessage {
    @Serial
    private static final long serialVersionUID = 181L;

    public boolean isExpected() {
        return (boolean) this.get("expected");
    }

    public void setExpected(final boolean expected) {
        this.put("expected", expected);
    }
}
