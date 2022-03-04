package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Room;
import com.higgs.server.db.repo.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room upsert(final Room room) {
        return this.roomRepository.save(room);
    }

    public List<Room> performRoomSearch(final Room searchCriteria, final Collection<Long> homeSeqs) {
        if (searchCriteria != null) {
            if (searchCriteria.getRoomSeq() != null) {
                return List.of(this.roomRepository.getByRoomSeqAndHomeHomeSeq(searchCriteria.getRoomSeq(), searchCriteria.getHomeSeq()));
            }
            if (searchCriteria.getName() != null) {
                return this.roomRepository.getByNameContainingIgnoreCaseAndHomeHomeSeq(searchCriteria.getName(), searchCriteria.getHomeSeq());
            }
        }
        return this.roomRepository.getByHomeHomeSeqIn(homeSeqs);
    }
}
