package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.HomeService;
import com.higgs.server.web.svc.UserLoginService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link HomeRest}.
 */
@ExtendWith(MockitoExtension.class)
class HomeRestTest {
    @Mock
    private HomeService homeService;

    @Mock
    private RestUtils restUtils;

    @Mock
    private UserLoginService userLoginService;

    private HomeRest homeRest;

    @BeforeEach
    void setUp() {
        this.homeRest = new HomeRest(this.homeService, this.restUtils, this.userLoginService);
    }

    /**
     * Tests the method {@link HomeRest#upsert(Home, Principal)}. This method should return a {@link ResponseEntity}
     * with the {@link Home} that was upserted.
     */
    @Test
    void testUpsert() {
        final Home home = mock(Home.class);
        final Principal principal = mock(Principal.class);
        final UserLogin userLogin = mock(UserLogin.class);
        when(home.getName()).thenReturn("name");
        when(userLogin.getUserLoginSeq()).thenReturn(1L);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(userLogin));
        when(this.homeService.upsert(any(), any())).thenReturn(home);
        final ResponseEntity<Home> actual = this.homeRest.upsert(home, principal);
        verify(this.homeService).upsert(eq("name"), eq(1L));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(home))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link HomeRest#upsert(Home, Principal)} with invalid input. In this case, the user trying to
     * insert or update a home is invalid. This method should throw {@link IllegalArgumentException} in this case.
     */
    @Test
    void testUpsertBadRequest() {
        final Home home = mock(Home.class);
        final Principal principal = mock(Principal.class);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.empty());
        final ResponseEntity<Home> actual = this.homeRest.upsert(home, principal);
        assertAll(
                () -> assertNull(actual.getBody()),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(400)))
        );
    }

    /**
     * Tests the method {@link HomeRest#upsert(Home, Principal)} with invalid (null) inputs. This method should throw an
     * {@link IllegalArgumentException} in this case.
     * @param home a mock {@link Home}
     * @param principal a mock {@link Principal}
     */
    @ParameterizedTest
    @MethodSource("getTestUpsertNullArgsParams")
    void testUpsertNullArgs(final Home home, final Principal principal) {
        assertThrows(IllegalArgumentException.class, () -> this.homeRest.upsert(home, principal));
    }

    public static Stream<Arguments> getTestUpsertNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, mock(Principal.class)),
                Arguments.of(mock(Home.class), null)
        );
    }

    /**
     * Tests the method {@link HomeRest#search(Home, Principal)}. This method should return a {@link ResponseEntity}
     * with a {@link List} of {@link Home} that were found matching the input criteria.
     */
    @Test
    void testSearch() {
        final Home home = mock(Home.class);
        final List<Home> homeSingletonList = Collections.singletonList(home);
        final List<Long> homeSeqs = Collections.singletonList(1L);
        final Principal principal = mock(Principal.class);
        when(this.homeService.performHomeSearch(any(), any())).thenReturn(homeSingletonList);
        when(this.restUtils.getHomeSeqs(any())).thenReturn(homeSeqs);
        final ResponseEntity<List<Home>> actual = this.homeRest.search(home, principal);
        verify(this.homeService).performHomeSearch(eq(home), eq(homeSeqs));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(homeSingletonList))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link HomeRest#search(Home, Principal)} with invalid input. In this case, somehow spring has
     * not provided a principal. This method should throw {@link IllegalArgumentException} in this case.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testSearchNullPrincipal() {
        assertThrows(IllegalArgumentException.class, () -> this.homeRest.search(mock(Home.class), null));
    }
}
