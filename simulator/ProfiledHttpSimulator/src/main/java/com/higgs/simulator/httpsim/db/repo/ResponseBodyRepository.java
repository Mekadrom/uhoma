package com.higgs.simulator.httpsim.db.repo;

import com.higgs.simulator.httpsim.db.entity.ResponseBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseBodyRepository extends JpaRepository<ResponseBody, Long>, JpaSpecificationExecutor<ResponseBody> {
    List<ResponseBody> findByResponseGroupResponseGroupSeq(Long responseGroupSeq);
}
