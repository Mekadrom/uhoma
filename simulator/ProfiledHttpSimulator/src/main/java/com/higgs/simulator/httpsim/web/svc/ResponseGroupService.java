package com.higgs.simulator.httpsim.web.svc;

import com.higgs.simulator.httpsim.db.entity.Profile;
import com.higgs.simulator.httpsim.db.entity.ResponseGroup;
import com.higgs.simulator.httpsim.db.repo.ResponseGroupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ResponseGroupService {
    private final ResponseGroupRepository responseGroupRepository;

    public ResponseGroup findByProfileAndEndpointOrDefault(final Profile profile, final String endpoint) {
        return this.responseGroupRepository.findByProfileSeqAndEndpoint(profile.getProfileSeq(), endpoint).orElseGet(() -> this.createNewResponseGroup(profile, endpoint));
    }

    ResponseGroup createNewResponseGroup(final Profile profile, final String endpoint) {
        return this.responseGroupRepository.save(new ResponseGroup().setProfileSeq(profile.getProfileSeq()).setEndpoint(endpoint));
    }
}
