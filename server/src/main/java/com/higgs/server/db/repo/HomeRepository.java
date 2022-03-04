package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {
    List<Home> findByOwnerUserLoginSeq(Long ownerUserLoginSeq);

    List<Home> getByNameContainingIgnoreCaseAndHomeSeqIn(String name, Collection<Long> homeSeqs);
}
