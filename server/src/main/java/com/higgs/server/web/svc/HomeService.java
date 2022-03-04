package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.HomeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class HomeService {
    private final HomeRepository homeRepository;

    public Home getHome(final Long homeSeq) {
        return this.homeRepository.getById(homeSeq);
    }

    public List<Home> getHomesForUser(final UserLogin userLogin) {
        return this.getHomesForUser(userLogin.getUserLoginSeq());
    }

    private List<Home> getHomesForUser(final Long userLoginSeq) {
        return this.homeRepository.findByOwnerUserLoginSeq(userLoginSeq);
    }

    public List<Home> performHomeSearch(final Home searchCriteria, final Collection<Long> homeSeqs) {
        if (searchCriteria != null) {
            if (searchCriteria.getHomeSeq() != null && homeSeqs.contains(searchCriteria.getHomeSeq())) {
                return List.of(this.homeRepository.getById(searchCriteria.getHomeSeq()));
            }
            if (searchCriteria.getName() != null) {
                return this.homeRepository.getByNameContainingIgnoreCaseAndHomeSeqIn(searchCriteria.getName(), homeSeqs);
            }
        }
        return this.homeRepository.findAllById(homeSeqs);
    }

    public Home createHome(final String name, final Long userLoginSeq) {
        return this.homeRepository.save(new Home().setName(name).setType(Home.HOME_TYPE_USER).setOwnerUserLoginSeq(userLoginSeq));
    }
}
