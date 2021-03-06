package com.higgs.server.db.repo;

import com.higgs.server.db.entity.ActionParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionParameterRepository extends JpaRepository<ActionParameter, Long>, JpaSpecificationExecutor<ActionParameter> {
}
