package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Room;
import com.higgs.server.db.entity.RoomLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;

public interface RoomLinkRepository extends JpaRepository<RoomLink, Long>, JpaSpecificationExecutor<RoomLink> {

    Collection<RoomLink> getAllByStartRoom(Room startRoom);

}
