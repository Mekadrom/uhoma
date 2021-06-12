package com.higgs.server.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface SimpleNamedSequenceRepository<E, ID> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {

    Optional<List<E>> findByNameLike(String name);

}
