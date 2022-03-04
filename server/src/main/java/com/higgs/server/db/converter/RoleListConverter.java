package com.higgs.server.db.converter;

import com.higgs.server.security.Role;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoleListConverter implements AttributeConverter<List<Role>, String> {
    @Override
    public String convertToDatabaseColumn(final List<Role> attribute) {
        if (CollectionUtils.isEmpty(attribute)) {
            return null;
        }
        return attribute.stream().map(Role::getRoleName).reduce((s1, s2) -> s1 + "," + s2).orElse(StringUtils.EMPTY);
    }

    @Override
    public List<Role> convertToEntityAttribute(final String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return new ArrayList<>();
        }
        return Arrays.stream(dbData.split(",")).map(Role::getByRoleName).collect(Collectors.toList());
    }
}
