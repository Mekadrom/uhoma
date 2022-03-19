package com.higgs.simulator.httpsim.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {
    public static String setToString(final Set<String> set) {
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        return set.stream().sorted().reduce((s1, s2) -> s1 + "," + s2).orElse(null);
    }

    public static Set<String> stringToSet(final String string) {
        if (org.apache.commons.lang3.StringUtils.isBlank(string)) {
            return new HashSet<>();
        }
        return Arrays.stream(string.split(",")).collect(Collectors.toSet());
    }
}
