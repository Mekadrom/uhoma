package com.higgs.server.web.svc;

import com.higgs.server.config.security.JwtTokenUtil;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import com.higgs.server.web.dto.AuthResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;

    public AuthResult getTokens(final String username, final String password) {
        final Authentication authenticate = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        final UserDetails user = (UserDetails) authenticate.getPrincipal();
        final Optional<UserLogin> userLoginOpt = this.userLoginRepository.findByUsername(user.getUsername());
        if (userLoginOpt.isPresent()) {
            final UserLogin userLogin = userLoginOpt.get();
            return new AuthResult(this.jwtTokenUtil.generateToken(userLogin), userLogin);
        }
        throw new UsernameNotFoundException(user.getUsername());
    }

    public UserLogin performUserSearch(@NonNull final String name) {
        final Optional<UserLogin> userLogin = this.userLoginRepository.findByUsername(name);
        if (userLogin.isPresent()) {
            return userLogin.get();
        }
        throw new UsernameNotFoundException(name);
    }

    public boolean validate(final String bearer) {
        return this.jwtTokenUtil.parseAndValidateToken(bearer, this.userLoginRepository::findByUsername).isPresent();
    }
}
