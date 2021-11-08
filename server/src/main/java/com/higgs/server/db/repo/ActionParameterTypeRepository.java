package com.higgs.server.db.repo;

import com.higgs.server.db.entity.ActionParameterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionParameterTypeRepository extends JpaRepository<ActionParameterType, Long>, JpaSpecificationExecutor<ActionParameterType> {

    List<ActionParameterType> getByAccountAccountSeq(Long accountSeq);

}
