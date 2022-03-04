package com.higgs.server.web.rest.util;

import com.higgs.server.db.entity.DtoFilter;
import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.web.svc.HomeService;
import com.higgs.server.web.svc.UserLoginService;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public final class RestUtils {
    private final HomeService homeService;
    private final UserLoginService userLoginService;

    @SneakyThrows
    public Collection<Long> getHomeSeqs(final Principal principal) {
        Collection<Long> homeSeqs = null;
        if (principal != null) {
            final Optional<UserLogin> userLoginOpt = this.userLoginService.findByUsername(principal.getName());
            if (userLoginOpt.isEmpty()) {
                throw new AuthenticationException("User not found");
            }
            homeSeqs = this.homeService.getHomesForUser(userLoginOpt.get()).stream()
                    .map(Home::getHomeSeq)
                    .collect(Collectors.toList());
        }
        return homeSeqs;
    }


    public void filterInvalidRequest(final Principal principal, final DtoFilter searchCriteria) {
        this.filterInvalidRequest(principal.getName(), searchCriteria, this.getHomeSeqs(principal));
    }

    public void filterInvalidRequest(final String principalName, final DtoFilter searchCriteria, final Collection<Long> allowedHomeSeqs) {
        if (searchCriteria.getHomeSeq() == null || Collections.isEmpty(allowedHomeSeqs) || !allowedHomeSeqs.contains(searchCriteria.getHomeSeq())) {
            throw new IllegalArgumentException(String.format("User %s not allowed to access object with home_seq %d", principalName, searchCriteria.getHomeSeq()));
        }
    }
}
