package com.higgs.server.db.repo;

import com.higgs.server.db.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long>, JpaSpecificationExecutor<UserLogin> {
    Optional<UserLogin> findByUsername(String username);
}
