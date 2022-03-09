package com.higgs.server.web.svc;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import com.higgs.server.security.JwtTokenUtils;
import com.higgs.server.security.Role;
import com.higgs.server.web.dto.AuthResult;
import com.higgs.server.web.svc.util.UserAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserLoginService userLoginService;

    public AuthResult getTokens(final String username, final String password) {
        return this.getTokens(new UsernamePasswordAuthenticationToken(username, password));
    }

    public AuthResult getTokens(final Authentication authentication) {
        final Authentication authenticate = this.authenticationManager.authenticate(authentication);
        final UserDetails user = (UserDetails) authenticate.getPrincipal();
        final Optional<UserLogin> userLoginOpt = this.userLoginService.findByUsername(user.getUsername());
        if (userLoginOpt.isPresent()) {
            final UserLogin userLogin = userLoginOpt.get();
            final String token = this.jwtTokenUtil.generateToken(userLogin);
            if (StringUtils.isNotBlank(token)) {
                return new AuthResult(token, this.userLoginService.save(userLogin.setLastLogin(Date.from(OffsetDateTime.now().toInstant()))));
            } else {
                throw new JwtException("Failed to generate token");
            }
        }
        throw new UsernameNotFoundException(user.getUsername());
    }

    public boolean validate(final String bearer) {
        return this.jwtTokenUtil.parseAndValidateToken(bearer, this.userLoginService::findByUsername).isPresent();
    }

    public AuthResult register(final String username, final String password) {
        if (this.userLoginService.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(username);
        }

        final UserLogin userLogin = this.userLoginService.save(new UserLogin()
                .setUsername(username)
                .setPassword(this.passwordEncoder.encode(password))
                .addRole(Role.USER));
        return new AuthResult(this.jwtTokenUtil.generateToken(userLogin), userLogin);
    }

    public UserLogin performUserSearch(@NonNull final String name) {
        final Optional<UserLogin> userLogin = this.userLoginService.findByUsername(name);
        if (userLogin.isPresent()) {
            return userLogin.get();
        }
        throw new UsernameNotFoundException(name);
    }
}
