package com.higgs.server.web.svc.util.mapper;

import com.higgs.server.db.entity.Action;
import com.higgs.server.web.dto.ActionDto;
import com.higgs.server.web.dto.ActionHandlerDto;
import com.higgs.server.web.dto.ActionParameterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link DtoEntityMapper}.
 */
class DtoEntityMapperTest {
    private DtoEntityMapper dtoEntityMapper;

    @BeforeEach
    void setUp() {
        this.dtoEntityMapper = new DtoEntityMapper();
    }

    /**
     * Test for {@link DtoEntityMapper#map(Object, Class)}. Makes sure that it maps fields correctly between DTO types.
     */
    @Test
    void testMap() {
        final ActionDto actionDto = new ActionDto();
        final ActionParameterDto actionParameter = new ActionParameterDto();
        final ActionHandlerDto actionHandler = new ActionHandlerDto();
        actionDto.setActionSeq(1L);
        actionDto.setName("actionName");
        actionDto.setParameters(List.of(actionParameter));
        actionDto.setOwnerNodeSeq(2L);
        actionDto.setActionHandler(actionHandler);
        final Action entity = this.dtoEntityMapper.map(actionDto, Action.class);
        assertAll(
                () -> assertThat(entity.getActionSeq(), is(equalTo(actionDto.getActionSeq()))),
                () -> assertThat(entity.getName(), is(equalTo(actionDto.getName()))),
                () -> assertNotNull(entity.getParameters()),
                () -> assertThat(entity.getOwnerNodeSeq(), is(equalTo(actionDto.getOwnerNodeSeq()))),
                () -> assertNotNull(entity.getActionHandler())
        );
    }
}
