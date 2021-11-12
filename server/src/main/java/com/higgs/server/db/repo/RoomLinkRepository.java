package com.higgs.server.db.repo;

import com.higgs.server.db.entity.RoomLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomLinkRepository extends JpaRepository<RoomLink, Long>, JpaSpecificationExecutor<RoomLink> {
}
