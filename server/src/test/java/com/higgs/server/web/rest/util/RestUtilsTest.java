package com.higgs.server.web.rest.util;

import com.higgs.server.db.entity.DtoFilter;
import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
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

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link RestUtils}.
 */
@ExtendWith(MockitoExtension.class)
class RestUtilsTest {
    @Mock
    private HomeService homeService;

    @Mock
    private UserLoginService userLoginService;

    private RestUtils restUtils;

    @BeforeEach
    void setUp() {
        this.restUtils = new RestUtils(this.homeService, this.userLoginService);
    }

    /**
     * Tests the {@link RestUtils#getHomeSeqs(Principal)} method with valid inputs. Verifies method calls and return
     * value.
     */
    @Test
    void testGetHomeSeqs() {
        final Principal principal = mock(Principal.class);
        final UserLogin userLogin = mock(UserLogin.class);
        final Home home = mock(Home.class);
        when(principal.getName()).thenReturn("user");
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(userLogin));
        when(this.homeService.getHomesForUser(any())).thenReturn(Collections.singletonList(home));
        when(home.getHomeSeq()).thenReturn(1L);
        final Collection<Long> actual = this.restUtils.getHomeSeqs(principal);
        verify(this.userLoginService, times(1)).findByUsername(any());
        verify(this.homeService, times(1)).getHomesForUser(any());
        assertThat(actual, is(equalTo(Collections.singletonList(1L))));
    }

    /**
     * Tests the {@link RestUtils#getHomeSeqs(Principal)} method with invalid inputs. Verifies method calls and return
     * values when the user has no access to any homes
     */
    @Test
    void testGetHomeSeqsNoHomes() {
        final Principal principal = mock(Principal.class);
        final UserLogin userLogin = mock(UserLogin.class);
        when(principal.getName()).thenReturn("user");
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(userLogin));
        when(this.homeService.getHomesForUser(any())).thenReturn(Collections.emptyList());
        final Collection<Long> actual = this.restUtils.getHomeSeqs(principal);
        verify(this.userLoginService, times(1)).findByUsername(any());
        verify(this.homeService, times(1)).getHomesForUser(any());
        assertThat(actual, is(equalTo(Collections.emptyList())));
    }

    /**
     * Tests the {@link RestUtils#getHomeSeqs(Principal)} method with invalid inputs. Verifies method calls and return
     * values when the user itself is invalid.
     */
    @Test
    void testGetHomeSeqsUserNotFound() {
        final Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> this.restUtils.getHomeSeqs(principal));
        verify(this.userLoginService, times(1)).findByUsername(any());
    }

    /**
     * Tests the {@link RestUtils#getHomeSeqs(Principal)} method with invalid inputs. Verifies that an
     * {@link IllegalArgumentException} is thrown when the user is not found.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testGetHomeSeqsNullPrincipal() {
        assertThrows(IllegalArgumentException.class, () -> this.restUtils.getHomeSeqs(null));
    }

    /**
     * Tests the {@link RestUtils#filterInvalidRequest(Principal, DtoFilter)} method with valid inputs. Verifies the
     * method calls and implicitly checks that no exception is thrown.
     */
    @Test
    void testFilterInvalidRequestPrincipalSearchCriteria() {
        final Principal principal = mock(Principal.class);
        final DtoFilter dtoFilter = mock(DtoFilter.class);
        final Home home = mock(Home.class);
        when(principal.getName()).thenReturn("user");
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(mock(UserLogin.class)));
        when(dtoFilter.getHomeSeq()).thenReturn(1L);
        when(home.getHomeSeq()).thenReturn(1L);
        when(this.homeService.getHomesForUser(any())).thenReturn(Collections.singletonList(home));
        this.restUtils.filterInvalidRequest(principal, dtoFilter);
        verify(this.userLoginService, times(1)).findByUsername(any());
        verify(this.homeService, times(1)).getHomesForUser(any());
    }

    /**
     * Tests the {@link RestUtils#filterInvalidRequest(Principal, DtoFilter)} method with invalid inputs. Verifies the
     * method calls and that an {@link IllegalArgumentException} is thrown when the searchCriteria does not contain a
     * homeSeq.
     */
    @Test
    void testFilterInvalidRequestPrincipalSearchCriteriaInvalid() {
        final Principal principal = mock(Principal.class);
        final DtoFilter dtoFilter = mock(DtoFilter.class);
        when(principal.getName()).thenReturn("user");
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(mock(UserLogin.class)));
        assertThrows(IllegalArgumentException.class, () -> this.restUtils.filterInvalidRequest(principal, dtoFilter));
        verify(principal, times(2)).getName();
        verify(dtoFilter, times(2)).getHomeSeq();
    }

    /**
     * Tests the {@link RestUtils#filterInvalidRequest(Principal, DtoFilter)} method with invalid inputs. Verifies the
     * method throws an {@link IllegalArgumentException} when any of the inputs are null.
     * @param principal a mock {@link Principal}
     * @param dtoFilter a mock {@link DtoFilter}
     */
    @ParameterizedTest
    @MethodSource("getTestFilterInvalidRequestPrincipalSearchCriteriaNullParams")
    void testFilterInvalidRequestPrincipalSearchCriteriaNull(final Principal principal, final DtoFilter dtoFilter) {
        assertThrows(IllegalArgumentException.class, () -> this.restUtils.filterInvalidRequest(principal, dtoFilter));
    }

    public static Stream<Arguments> getTestFilterInvalidRequestPrincipalSearchCriteriaNullParams() {
        return Stream.of(
                Arguments.of(null, mock(DtoFilter.class)),
                Arguments.of(mock(Principal.class), null)
        );
    }

    /**
     * Tests the {@link RestUtils#filterInvalidRequest(String, DtoFilter, Collection)} method with valid inputs.
     * Verifies the method calls and explicitly checks that no exception is thrown.
     */
    @Test
    void testFilterInvalidRequest() {
        final DtoFilter dtoFilter = mock(DtoFilter.class);
        when(dtoFilter.getHomeSeq()).thenReturn(1L);
        assertDoesNotThrow(() -> this.restUtils.filterInvalidRequest("user", dtoFilter, Collections.singletonList(1L)));
        verify(dtoFilter, times(2)).getHomeSeq();
    }

    /**
     * Tests the {@link RestUtils#filterInvalidRequest(String, DtoFilter, Collection)} method with invalid inputs.
     * Verifies that an {@link IllegalArgumentException} is thrown when any of the inputs are null.
     * @param searchCriteriaHomeSeq a test searchCriteriaHomeSeq
     * @param allowedHomeSeqs a set of test homeSeqs
     */
    @ParameterizedTest
    @MethodSource("getTestFilterInvalidRequestParams")
    void testFilterInvalidRequestInvalid(final Long searchCriteriaHomeSeq, final Collection<Long> allowedHomeSeqs) {
        final DtoFilter dtoFilter = mock(DtoFilter.class);
        when(dtoFilter.getHomeSeq()).thenReturn(searchCriteriaHomeSeq);
        assertThrows(IllegalArgumentException.class, () -> this.restUtils.filterInvalidRequest("user", dtoFilter, allowedHomeSeqs));
    }

    public static Stream<Arguments> getTestFilterInvalidRequestParams() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(1L, Collections.emptyList()),
                Arguments.of(null, Collections.singletonList(1L)),
                Arguments.of(2L, Collections.singletonList(1L))
        );
    }

    /**
     * Tests the {@link RestUtils#filterInvalidRequest(String, DtoFilter, Collection)} method with invalid inputs.
     * Verifies that an {@link IllegalArgumentException} is thrown when the searchCriteria is null.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testFilterInvalidRequestNullFilter() {
        assertThrows(IllegalArgumentException.class, () -> this.restUtils.filterInvalidRequest("user", null, Collections.emptyList()));
    }
}
