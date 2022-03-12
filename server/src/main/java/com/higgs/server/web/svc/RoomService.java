package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Room;
import com.higgs.server.db.repo.RoomRepository;
import com.higgs.server.web.svc.util.PersistenceUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomService {
    private final PersistenceUtils persistenceUtils;
    private final RoomRepository roomRepository;

    public Room upsert(@NonNull final Room room) {
        return this.roomRepository.save(room);
    }

    public List<Room> performRoomSearch(final Room searchCriteria, final Collection<Long> homeSeqs) {
        if (searchCriteria != null) {
            if (searchCriteria.getRoomSeq() != null) {
                return Collections.singletonList(this.roomRepository.getByRoomSeqAndHomeHomeSeq(searchCriteria.getRoomSeq(), searchCriteria.getHomeSeq()));
            }
            if (searchCriteria.getName() != null) {
                return this.roomRepository.getByNameContainingIgnoreCaseAndHomeHomeSeq(searchCriteria.getName(), searchCriteria.getHomeSeq());
            }
        }
        return this.persistenceUtils.getByHomeSeqs(searchCriteria, homeSeqs, this.roomRepository::getByHomeHomeSeqIn);
    }
}
