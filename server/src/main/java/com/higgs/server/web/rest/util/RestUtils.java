package com.higgs.server.web.rest.util;

import com.higgs.server.db.entity.DtoFilter;
import com.higgs.server.db.entity.Home;
import com.higgs.server.web.svc.HomeService;
import com.higgs.server.web.svc.UserLoginService;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RestUtils {
    private final HomeService homeService;
    private final UserLoginService userLoginService;

    @SneakyThrows
    public Collection<Long> getHomeSeqs(@NonNull final Principal principal) {
        return this.userLoginService.findByUsername(principal.getName()).map(userLogin ->
                this.homeService.getHomesForUser(userLogin).stream()
                        .map(Home::getHomeSeq)
                        .collect(Collectors.toList())
        ).orElseThrow(() -> new AuthenticationException("User not found"));
    }

    public void filterInvalidRequest(@NonNull final Principal principal, @NonNull final DtoFilter searchCriteria) {
        this.filterInvalidRequest(principal.getName(), searchCriteria, this.getHomeSeqs(principal));
    }

    public void filterInvalidRequest(final String principalName, @NonNull final DtoFilter searchCriteria, final Collection<Long> allowedHomeSeqs) {
        if (searchCriteria.getHomeSeq() == null || Collections.isEmpty(allowedHomeSeqs) || !allowedHomeSeqs.contains(searchCriteria.getHomeSeq())) {
            throw new IllegalArgumentException(String.format("User %s not allowed to access object with home_seq %d", principalName, searchCriteria.getHomeSeq()));
        }
    }
}
