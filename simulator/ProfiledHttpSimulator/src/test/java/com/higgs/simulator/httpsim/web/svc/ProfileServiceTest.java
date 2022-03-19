package com.higgs.simulator.httpsim.web.svc;

import com.higgs.simulator.httpsim.db.entity.Profile;
import com.higgs.simulator.httpsim.db.repo.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    private ProfileRepository profileRepository;

    private ProfileService profileService;

    @BeforeEach
    public void setUp() {
        this.profileService = new ProfileService(this.profileRepository);
    }

    @Test
    void testCreateNewProfile() {
        final Profile profile = new Profile();
        profile.setEndpoint("test");
        profile.setKeyedFields(Set.of("bruh"));
        when(this.profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        final Profile actual = this.profileService.createNewProfile("test", Set.of("bruh"));
        assertAll(
                () -> assertThat(actual.getEndpoint(), is("test")),
                () -> assertThat(actual.getKeyedFields(), is(Set.of("bruh")))
        );
    }

    @Test
    void testFindByEndpoint() {
        final Profile profile = mock(Profile.class);
        when(this.profileRepository.findByEndpoint(any())).thenReturn(Optional.of(profile));
        final Optional<Profile> actual = this.profileService.findByEndpoint("test");
        verify(this.profileRepository).findByEndpoint("test");
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(profile));
    }
}
