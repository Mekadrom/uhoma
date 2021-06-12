package com.higgs.server.db.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersistenceUtils {
    public static String getLikeString(final String queryArg) {
        return String.format("%%%s%%", queryArg);
    }
}
