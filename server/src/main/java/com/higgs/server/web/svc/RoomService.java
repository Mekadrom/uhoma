package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Room;
import com.higgs.server.db.repo.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room upsert(final Room room) {
        return this.roomRepository.save(room);
    }

    public List<Room> performRoomSearch(final Long accountSeq, final Room searchCriteria) {
        if (searchCriteria != null) {
            if (searchCriteria.getRoomSeq() != null) {
                return List.of(this.roomRepository.getByRoomSeqAndAccountAccountSeq(searchCriteria.getRoomSeq(), accountSeq));
            }
            if (searchCriteria.getName() != null) {
                return this.roomRepository.getByNameContainingIgnoreCaseAndAccountAccountSeq(searchCriteria.getName(), accountSeq);
            }
        }
        return this.roomRepository.getByAccountAccountSeq(accountSeq);
    }
}
