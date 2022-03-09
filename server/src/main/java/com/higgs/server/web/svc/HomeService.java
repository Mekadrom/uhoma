package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.HomeRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class HomeService {
    private final HomeRepository homeRepository;

    public Home getHome(final Long homeSeq) {
        return this.homeRepository.getById(homeSeq);
    }

    public List<Home> getHomesForUser(final UserLogin userLogin) {
        if (userLogin == null) {
            throw new IllegalArgumentException("userLogin cannot be null");
        }
        return this.getHomesForUser(userLogin.getUserLoginSeq());
    }

    List<Home> getHomesForUser(final Long userLoginSeq) {
        if (userLoginSeq == null) {
            throw new IllegalArgumentException("userLoginSeq cannot be null");
        }
        return this.homeRepository.findByOwnerUserLoginSeq(userLoginSeq);
    }

    public Home upsert(final String homeName, final Long ownerUserLoginSeq) {
        if (StringUtils.isBlank(homeName)) {
            throw new IllegalArgumentException("homeName cannot be blank");
        }
        if (ownerUserLoginSeq == null) {
            throw new IllegalArgumentException("ownerUserLoginSeq cannot be null");
        }
        return this.homeRepository.save(new Home().setName(homeName).setType(Home.HOME_TYPE_USER).setOwnerUserLoginSeq(ownerUserLoginSeq));
    }

    public List<Home> performHomeSearch(final Home searchCriteria, final Collection<Long> homeSeqs) {
        if (searchCriteria != null) {
            if (searchCriteria.getHomeSeq() != null && homeSeqs.contains(searchCriteria.getHomeSeq())) {
                return Collections.singletonList(this.homeRepository.getById(searchCriteria.getHomeSeq()));
            }
            if (searchCriteria.getName() != null) {
                return this.homeRepository.getByNameContainingIgnoreCaseAndHomeSeqIn(searchCriteria.getName(), homeSeqs);
            }
        }
        return this.homeRepository.findAllById(homeSeqs);
    }
}
