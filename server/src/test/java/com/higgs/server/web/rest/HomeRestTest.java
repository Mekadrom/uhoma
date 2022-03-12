package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.web.dto.HomeDto;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.HomeService;
import com.higgs.server.web.svc.UserLoginService;
import com.higgs.server.web.svc.util.mapper.DtoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link HomeRest}.
 */
@ExtendWith(MockitoExtension.class)
class HomeRestTest {
    @Mock
    private DtoEntityMapper dtoEntityMapper;

    @Mock
    private HomeService homeService;

    @Mock
    private RestUtils restUtils;

    @Mock
    private UserLoginService userLoginService;

    private HomeRest homeRest;

    @BeforeEach
    void setUp() {
        this.homeRest = new HomeRest(this.dtoEntityMapper, this.homeService, this.restUtils, this.userLoginService);
    }

    /**
     * Tests the method {@link HomeRest#upsert(HomeDto, Principal)}. This method should return a {@link ResponseEntity}
     * with the {@link Home} that was upserted.
     */
    @Test
    void testUpsert() {
        final Home home = mock(Home.class);
        final HomeDto homeDto = mock(HomeDto.class);
        final Principal principal = mock(Principal.class);
        final UserLogin userLogin = mock(UserLogin.class);
        when(home.getName()).thenReturn("name");
        when(userLogin.getUserLoginSeq()).thenReturn(1L);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(userLogin));
        when(this.homeService.upsert(any(), any())).thenReturn(home);
        when(this.dtoEntityMapper.map(homeDto, Home.class)).thenReturn(home);
        final ResponseEntity<Home> actual = this.homeRest.upsert(homeDto, principal);
        verify(this.homeService, times(1)).upsert("name", 1L);
        verify(this.dtoEntityMapper, times(1)).map(homeDto, Home.class);
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(home))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link HomeRest#upsert(HomeDto, Principal)} with invalid input. In this case, the user trying to
     * insert or update a home is invalid. This method should throw {@link IllegalArgumentException} in this case.
     */
    @Test
    void testUpsertBadRequest() {
        final HomeDto homeDto = mock(HomeDto.class);
        final Principal principal = mock(Principal.class);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.empty());
        when(this.dtoEntityMapper.map(homeDto, Home.class)).thenReturn(mock(Home.class));
        final ResponseEntity<Home> actual = this.homeRest.upsert(homeDto, principal);
        verify(this.homeService, never()).upsert(any(), any());
        assertAll(
                () -> assertNull(actual.getBody()),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(400)))
        );
    }

    /**
     * Tests the method {@link HomeRest#upsert(HomeDto, Principal)} with invalid (null) inputs. This method should throw
     * an {@link IllegalArgumentException} in this case.
     * @param homeDto a mock {@link HomeDto}
     * @param principal a mock {@link Principal}
     */
    @ParameterizedTest
    @MethodSource("getTestUpsertNullArgsParams")
    void testUpsertNullArgs(final HomeDto homeDto, final Principal principal) {
        assertThrows(IllegalArgumentException.class, () -> this.homeRest.upsert(homeDto, principal));
    }

    public static Stream<Arguments> getTestUpsertNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, mock(Principal.class)),
                Arguments.of(mock(HomeDto.class), null)
        );
    }

    /**
     * Tests the method {@link HomeRest#search(HomeDto, Principal)}. This method should return a {@link ResponseEntity}
     * with a {@link List} of {@link Home} that were found matching the input criteria.
     */
    @Test
    void testSearch() {
        final Home home = mock(Home.class);
        final HomeDto homeDto = mock(HomeDto.class);
        final List<Home> homeSingletonList = Collections.singletonList(home);
        final List<Long> homeSeqs = Collections.singletonList(1L);
        final Principal principal = mock(Principal.class);
        when(this.homeService.performHomeSearch(any(), any())).thenReturn(homeSingletonList);
        when(this.restUtils.getHomeSeqs(any())).thenReturn(homeSeqs);
        when(this.dtoEntityMapper.map(homeDto, Home.class)).thenReturn(home);
        final ResponseEntity<List<Home>> actual = this.homeRest.search(homeDto, principal);
        verify(this.homeService, times(1)).performHomeSearch(home, homeSeqs);
        verify(this.dtoEntityMapper, times(1)).map(homeDto, Home.class);
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(homeSingletonList))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link HomeRest#search(HomeDto, Principal)} with invalid input. In this case, somehow spring has
     * not provided a principal. This method should throw {@link IllegalArgumentException} in this case.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testSearchNullPrincipal() {
        assertThrows(IllegalArgumentException.class, () -> this.homeRest.search(mock(HomeDto.class), null));
    }
}
