package com.higgs.server.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HAJsonUtils {
    public static Gson getDefaultGson() {
        return new GsonBuilder().serializeNulls().create();
    }
}
