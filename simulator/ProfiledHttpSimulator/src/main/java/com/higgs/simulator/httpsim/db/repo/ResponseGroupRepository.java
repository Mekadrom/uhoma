package com.higgs.simulator.httpsim.db.repo;

import com.higgs.simulator.httpsim.db.entity.ResponseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseGroupRepository extends JpaRepository<ResponseGroup, Long>, JpaSpecificationExecutor<ResponseGroup> {
    Optional<ResponseGroup> findByProfileSeqAndEndpoint(Long profileSeq, String endpoint);
}
