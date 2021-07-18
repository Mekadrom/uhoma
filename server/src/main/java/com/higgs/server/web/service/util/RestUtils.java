package com.higgs.server.web.service.util;

import com.higgs.server.db.repo.UserLoginRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.security.Principal;
import java.util.Optional;

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
            throw new AccountNotFoundException();
        }
        return accountSeq;
    }
}
