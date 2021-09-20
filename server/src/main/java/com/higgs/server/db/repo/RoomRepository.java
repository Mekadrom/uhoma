package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Room getByRoomSeqAndAccountAccountSeq(final Long roomSeq, final Long accountSeq);

    List<Room> getByAccountAccountSeq(Long accountSeq);

    List<Room> getByNameContainingIgnoreCaseAndAccountAccountSeq(final String name, final Long accountSeq);

}
