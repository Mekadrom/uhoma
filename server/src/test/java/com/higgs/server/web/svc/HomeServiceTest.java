package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.HomeRepository;
import com.higgs.server.web.svc.util.PersistenceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private PersistenceUtils persistenceUtils;

    private HomeService homeService;

    @BeforeEach
    void setUp() {
        this.homeService = new HomeService(this.homeRepository, this.persistenceUtils);
    }

    /**
     * Tests the {@link HomeService#performHomeSearch(Home, Collection)} method with valid input of a home_seq that
     * exists. Only verifies that the call is delegated to the {@link HomeRepository#getById(Object)} method.
     */
    @Test
    void testGetHome() {
        final Home home = mock(Home.class);
        when(this.homeRepository.getById(1L)).thenReturn(home);
        assertThat(this.homeService.getHome(1L), is(equalTo(home)));
        verify(this.homeRepository, times(1)).getById(1L);
    }

    /**
     * Tests the {@link HomeService#getHomesForUser(UserLogin)} method with valid input of a nonnull
     * {@link UserLogin}.
     */
    @Test
    void testGetHomesForUserLogin() {
        final UserLogin userLogin = mock(UserLogin.class);
        final Home home = mock(Home.class);
        when(userLogin.getUserLoginSeq()).thenReturn(1L);
        when(this.homeRepository.findByOwnerUserLoginSeq(any())).thenReturn(List.of(home));
        assertThat(this.homeService.getHomesForUser(userLogin), is(equalTo(List.of(home))));
        this.homeService.getHomesForUser(1L);
        verify(this.homeRepository, times(2)).findByOwnerUserLoginSeq(1L);
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
        final Home home = mock(Home.class);
        when(this.homeRepository.save(any())).thenReturn(home);
        assertThat(this.homeService.upsert("test", 1L), is(equalTo(home)));
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
        final Collection<Long> homeSeqs = List.of(1L);
        when(home.getHomeSeq()).thenReturn(1L);
        when(this.persistenceUtils.getByHomeSeqs(any(), any(), any())).thenCallRealMethod();
        when(this.homeRepository.findAllById(any())).thenReturn(List.of(home));
        assertThat(this.homeService.performHomeSearch(home, homeSeqs), is(equalTo(List.of(home))));
        verify(this.persistenceUtils, times(1)).getByHomeSeqs(eq(home), eq(homeSeqs), any());
        verify(this.homeRepository, times(1)).findAllById(List.of(1L));
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
        when(this.persistenceUtils.getByHomeSeqs(any(), any(), any())).thenCallRealMethod();
        when(this.homeRepository.findAllById(any())).thenReturn(List.of(home));
        assertThat(this.homeService.performHomeSearch(home, homeSeqs), is(equalTo(List.of(home))));
        verify(this.persistenceUtils, times(1)).getByHomeSeqs(eq(home), eq(homeSeqs), any());
        verify(this.homeRepository, times(1)).findAllById(homeSeqs);
    }

    /**
     * Tests the {@link HomeService#performHomeSearch(Home, Collection)} method with valid input, filtering on
     * {@link Home#getName()}.
     */
    @Test
    void testPerformHomeSearchHomeName() {
        final Home home = mock(Home.class);
        final Collection<Long> homeSeqs = List.of(1L);
        when(home.getName()).thenReturn("test");
        when(this.homeRepository.getByNameContainingIgnoreCaseAndHomeSeqIn(any(), any())).thenReturn(List.of(home));
        assertThat(this.homeService.performHomeSearch(home, homeSeqs), is(equalTo(List.of(home))));
        verify(this.homeRepository, times(1)).getByNameContainingIgnoreCaseAndHomeSeqIn("test", homeSeqs);
    }

    /**
     * Tests the {@link HomeService#performHomeSearch(Home, Collection)} method with valid input (null home filter),
     * which returns all homes the user can access.
     */
    @Test
    void testPerformHomeSearchNull() {
        final Home home = mock(Home.class);
        final Collection<Long> homeSeqs = List.of(1L);
        when(this.homeRepository.findAllById(any())).thenReturn(List.of(home));
        when(this.persistenceUtils.getByHomeSeqs(any(), any(), any())).thenCallRealMethod();
        assertThat(this.homeService.performHomeSearch(null, homeSeqs), is(equalTo(List.of(home))));
        verify(this.persistenceUtils, times(1)).getByHomeSeqs(eq(null), eq(homeSeqs), any());
        verify(this.homeRepository, times(1)).findAllById(homeSeqs);
    }
}
