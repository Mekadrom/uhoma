package com.higgs.server.web.svc;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link UserLoginService}.
 */
@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {
    @Mock
    private UserLoginRepository userLoginRepository;

    private UserLoginService userLoginService;

    @BeforeEach
    void setUp() {
        this.userLoginService = new UserLoginService(this.userLoginRepository);
    }

    /**
     * Tests the {@link UserLoginService#findByUsername(String)} method.
     */
    @Test
    void testFindByUsername() {
        this.userLoginService.findByUsername("test");
        verify(this.userLoginRepository, times(1)).findByUsername("test");
    }

    /**
     * Tests the {@link UserLoginService#save(UserLogin)} method.
     */
    @Test
    void testSave() {
        this.userLoginService.save(mock(UserLogin.class));
        verify(this.userLoginRepository, times(1)).save(any());
    }
}
