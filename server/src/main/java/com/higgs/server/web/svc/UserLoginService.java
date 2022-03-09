package com.higgs.server.web.svc;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserLoginService {
    private final UserLoginRepository userLoginRepository;

    public Optional<UserLogin> findByUsername(final String name) {
        return this.userLoginRepository.findByUsername(name);
    }

    public UserLogin save(final UserLogin userLogin) {
        return this.userLoginRepository.save(userLogin);
    }
}
