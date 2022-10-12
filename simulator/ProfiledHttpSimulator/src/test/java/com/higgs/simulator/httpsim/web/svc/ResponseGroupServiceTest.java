package com.higgs.simulator.httpsim.web.svc;

import com.higgs.simulator.httpsim.db.entity.Profile;
import com.higgs.simulator.httpsim.db.entity.ResponseGroup;
import com.higgs.simulator.httpsim.db.repo.ResponseGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponseGroupServiceTest {
    @Mock
    private ResponseGroupRepository responseGroupRepository;

    private ResponseGroupService responseGroupService;

    @BeforeEach
    void setUp() {
        this.responseGroupService = new ResponseGroupService(this.responseGroupRepository);
    }

    @Test
    void testFindByProfileAndEndpointOrDefault() {
        final ResponseGroupService responseGroupServiceSpy = spy(this.responseGroupService);
        final Profile profile = mock(Profile.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        when(profile.getProfileSeq()).thenReturn(1L);
        when(this.responseGroupRepository.findByProfileSeqAndEndpoint(any(), any())).thenReturn(Optional.of(responseGroup));
        assertThat(responseGroupServiceSpy.findByProfileAndEndpointOrDefault(profile, "endpoint"), is(equalTo(responseGroup)));
        verify(this.responseGroupRepository, times(1)).findByProfileSeqAndEndpoint(1L, "endpoint");
        verify(responseGroupServiceSpy, times(0)).createNewResponseGroup(profile, "endpoint");
    }

    @Test
    void testFindByProfileAndEndpointOrDefaultDefaults() {
        final ResponseGroupService responseGroupServiceSpy = spy(this.responseGroupService);
        final Profile profile = mock(Profile.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        when(profile.getProfileSeq()).thenReturn(1L);
        doReturn(responseGroup).when(responseGroupServiceSpy).createNewResponseGroup(any(), any());
        when(this.responseGroupRepository.findByProfileSeqAndEndpoint(any(), any())).thenReturn(Optional.empty());
        assertThat(responseGroupServiceSpy.findByProfileAndEndpointOrDefault(profile, "endpoint"), is(equalTo(responseGroup)));
        verify(this.responseGroupRepository, times(1)).findByProfileSeqAndEndpoint(1L, "endpoint");
        verify(responseGroupServiceSpy, times(1)).createNewResponseGroup(profile, "endpoint");
    }

    @Test
    void testCreateNewResponseGroup() {
        final Profile profile = new Profile();
        profile.setProfileSeq(1L);
        final ResponseGroup responseGroup = new ResponseGroup().setProfileSeq(1L).setEndpoint("endpoint");
        when(this.responseGroupRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        assertThat(this.responseGroupService.createNewResponseGroup(profile, "endpoint"), is(equalTo(responseGroup)));
        verify(this.responseGroupRepository, times(1)).save(responseGroup);
    }
}
