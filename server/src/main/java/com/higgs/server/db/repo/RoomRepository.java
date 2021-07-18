package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Room;

import java.util.List;

public interface RoomRepository extends SimpleNamedSequenceRepository<Room, Long> {

    Room getByRoomSeqAndAccountAccountSeq(final Long roomSeq, final Long accountSeq);

    List<Room> getByAccountAccountSeq(Long accountSeq);

    List<Room> getByNameContainingIgnoreCaseAndAccountAccountSeq(final String name, final Long accountSeq);

}
