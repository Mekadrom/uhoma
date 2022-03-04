package com.higgs.server.db.repo;

import com.higgs.server.db.entity.ActionHandler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionHandlerRepository extends JpaRepository<ActionHandler, Long>, JpaSpecificationExecutor<ActionHandler> {
    List<ActionHandler> getByHomeHomeSeq(Long homeSeq);
}
