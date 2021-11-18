package com.higgs.server.web.rest.util;

import com.higgs.server.db.repo.UserLoginRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public final class RestUtils {
    private final UserLoginRepository userLoginRepository;

    @SneakyThrows
    public Long getAccountSeq(final Principal principal) {
        Long accountSeq = null;
        if (principal != null) {
            final Optional<Long> accountSeqOpt = this.userLoginRepository.findByUsername(principal.getName()).map(it -> it.getAccount().getAccountSeq());
            if (accountSeqOpt.isPresent()) {
                accountSeq = accountSeqOpt.get();
            }
        }
        if (accountSeq == null) {
            RestUtils.log.error("Account not found: {}", Optional.ofNullable(principal).map(Principal::getName).orElse("null"));
            throw new AuthenticationException();
        }
        return accountSeq;
    }
}