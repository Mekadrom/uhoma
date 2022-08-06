package com.higgs.common.handler.extension;

import com.higgs.common.handler.HandlerRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtensionHandlerRequest extends HandlerRequest {
    @Serial
    private static final long serialVersionUID = 182L;

    public ExtensionHandlerRequest(final Map<String, Object> input) {
        super(input);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getParameters() {
        final Map<String, Object> parameterMap = new HashMap<>();
        final Map<String, Object> actionWithParams = (Map<String, Object>) this.get("actionWithParams");
        if (actionWithParams != null) {
            final List<Map<String, Object>> parameters = (List<Map<String, Object>>) actionWithParams.get("parameters");
            if (parameters != null) {
                for (final Map<String, Object> parameter : parameters) {
                    final String name = (String) parameter.get("name");
                    if (StringUtils.isNotBlank(name)) {
                        parameterMap.put(name, parameter.get("currentValue"));
                    }
                }
            }
        }
        return parameterMap;
    }
}
