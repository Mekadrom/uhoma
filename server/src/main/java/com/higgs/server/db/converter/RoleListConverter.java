package com.higgs.server.db.converter;

import com.higgs.server.security.Role;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleListConverter implements AttributeConverter<Set<Role>, String> {
    @Override
    public String convertToDatabaseColumn(final Set<Role> attribute) {
        if (CollectionUtils.isEmpty(attribute)) {
            return null;
        }
        return attribute.stream().map(Role::getRoleName).collect(Collectors.joining(","));
    }

    @Override
    public Set<Role> convertToEntityAttribute(final String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return new HashSet<>();
        }
        return Arrays.stream(dbData.split(",")).map(Role::getByRoleName).collect(Collectors.toSet());
    }
}
