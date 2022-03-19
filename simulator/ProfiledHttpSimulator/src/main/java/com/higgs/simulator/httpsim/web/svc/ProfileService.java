package com.higgs.simulator.httpsim.web.svc;

import com.higgs.simulator.httpsim.db.entity.Profile;
import com.higgs.simulator.httpsim.db.repo.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    public Profile createNewProfile(final String endpoint, final Set<String> keyedFields) {
        return this.profileRepository.save(new Profile().setEndpoint(endpoint).setKeyedFields(keyedFields));
    }

    public Optional<Profile> findByEndpoint(final String root) {
        return this.profileRepository.findByEndpoint(root);
    }
}
