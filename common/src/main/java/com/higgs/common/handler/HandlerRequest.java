package com.higgs.common.handler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class HandlerRequest extends HandlerMessage {
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
}
