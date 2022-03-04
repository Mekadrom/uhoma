package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Room getByRoomSeqAndHomeHomeSeq(Long roomSeq, Long homeSeq);

    List<Room> getByHomeHomeSeq(Long homeSeq);

    List<Room> getByNameContainingIgnoreCaseAndHomeHomeSeq(String name, Long homeSeq);

    List<Room> getByHomeHomeSeqIn(Collection<Long> homeSeqs);

}
