package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.HomeRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link HomeService}.
 */
@ExtendWith(MockitoExtension.class)
class HomeServiceTest {
    @Mock
    private HomeRepository homeRepository;

    private HomeService homeService;

    @BeforeEach
    void setUp() {
        this.homeService = new HomeService(this.homeRepository);
    }

    /**
     * Tests the {@link HomeService#performHomeSearch(Home, Collection)} method with valid input of a home_seq that
     * exists. Only verifies that the call is delegated to the {@link HomeRepository#getById(Object)} method.
     */
    @Test
    void testGetHome() {
        this.homeService.getHome(1L);
        verify(this.homeRepository, times(1)).getById(eq(1L));
    }

    /**
     * Tests the {@link HomeService#getHomesForUser(UserLogin)} method with valid input of a nonnull
     * {@link UserLogin}.
     */
    @Test
    void testGetHomesForUserLogin() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(userLogin.getUserLoginSeq()).thenReturn(1L);
        this.homeService.getHomesForUser(userLogin);
        this.homeService.getHomesForUser(1L);
        verify(this.homeRepository, times(2)).findByOwnerUserLoginSeq(eq(1L));
    }

    /**
     * Tests the {@link HomeService#getHomesForUser(UserLogin)} method with sad path input of a null {@link UserLogin}.
     * Also tests the {@link HomeService#getHomesForUser(Long)} method with sad path input of a null {@link Long}.
     * Both cases should return an {@link IllegalArgumentException}.
     */
    @Test
    void testGetHomesForUserLoginFailsArgValidation() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> this.homeService.getHomesForUser((UserLogin) null)),
                () -> assertThrows(IllegalArgumentException.class, () -> this.homeService.getHomesForUser((Long) null))
        );
    }

    /**
     * Tests the {@link HomeService#upsert(String, Long)} method with valid input of a nonnull home name and
     */
    @Test
    void testCreateHome() {
        this.homeService.upsert("test", 1L);
        final ArgumentCaptor<Home> captor = ArgumentCaptor.forClass(Home.class);
        verify(this.homeRepository, times(1)).save(captor.capture());
        assertAll(
                () -> assertThat(captor.getValue().getName(), is(equalTo("test"))),
                () -> assertThat(captor.getValue().getOwnerUserLoginSeq(), is(equalTo(1L))),
                () -> assertThat(captor.getValue().getType(), is(equalTo(Home.HOME_TYPE_USER)))
        );
    }

    /**
     * Tests the {@link HomeService#upsert(String, Long)} method with sad path input of null or blank arguments.
     * @param homeName the home name for the case
     * @param ownerUserLoginSeq the owner_user_login_seq for the case
     */
    @ParameterizedTest
    @MethodSource("getTestCreateHomeFailArgValidationParams")
    void testCreateHomeFailArgValidation(final String homeName, final Long ownerUserLoginSeq) {
        assertThrows(IllegalArgumentException.class, () -> this.homeService.upsert(homeName, ownerUserLoginSeq));
    }

    public static Stream<Arguments> getTestCreateHomeFailArgValidationParams() {
        return Stream.of(
                Arguments.of("test", null),
                Arguments.of("", 1L),
                Arguments.of("    ", 1L),
                Arguments.of(null, 1L),
                Arguments.of(null, null)
        );
    }

    /**
     * Tests the {@link HomeService#performHomeSearch(Home, Collection)} method with valid input of a home_seq which the
     * user is allowed to access, filtering on {@link Home#getHomeSeq()}
     */
    @Test
    void testPerformHomeSearchHomeSeqAllowed() {
        final Home home = mock(Home.class);
        when(home.getHomeSeq()).thenReturn(1L);
        this.homeService.performHomeSearch(home, List.of(1L));
        verify(this.homeRepository, times(1)).getById(eq(1L));
    }

    /**
     * Tests the {@link HomeService#performHomeSearch(Home, Collection)} method with sad path input of a home_seq which
     * the user is not allowed to access.
     */
    @Test
    void testPerformHomeSearchHomeSeqNotAllowed() {
        final Home home = mock(Home.class);
        final Collection<Long> homeSeqs = List.of(2L);
        when(home.getHomeSeq()).thenReturn(1L);
        this.homeService.performHomeSearch(home, homeSeqs);
        verify(this.homeRepository, times(1)).findAllById(eq(homeSeqs));
    }

    /**
     * Tests the {@link HomeService#performHomeSearch(Home, Collection)} method with valid input, filtering on
     * {@link Home#getName()}.
     */
    @Test
    void testPerformHomeSearchHomeName() {
        final Home home = mock(Home.class);
        final Collection<Long> homeSeqs = List.of(1L);
        when(home.getHomeSeq()).thenReturn(null);
        when(home.getName()).thenReturn("test");
        this.homeService.performHomeSearch(home, homeSeqs);
        verify(this.homeRepository, times(1)).getByNameContainingIgnoreCaseAndHomeSeqIn(eq("test"), eq(homeSeqs));
    }

    /**
     * Tests the {@link HomeService#performHomeSearch(Home, Collection)} method with valid input (null home filter),
     * which returns all homes the user can access.
     */
    @Test
    void testPerformHomeSearchNull() {
        final Collection<Long> homeSeqs = List.of(1L);
        this.homeService.performHomeSearch(null, homeSeqs);
        verify(this.homeRepository, times(1)).findAllById(eq(homeSeqs));
    }
}
