package com.higgs.server.web.service;

import com.higgs.server.config.security.Roles;
import com.higgs.server.db.entity.Room;
import com.higgs.server.db.repo.RoomRepository;
import com.higgs.server.web.service.util.RestUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RolesAllowed(Roles.ADMIN)
@RequestMapping(value = "room")
public class RoomRest {
    private final RestUtils restUtils;
    private final RoomRepository roomRepository;

    @PostMapping(value = "upsert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Room> upsert(@RequestBody(required = false) final Room room) {
        return ResponseEntity.of(Optional.of(this.roomRepository.save(room)));
    }

    @SneakyThrows
    @PostMapping(value = "search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Room>> search(@NotNull @RequestBody(required = false) final Room searchCriteria,
                                             final Principal principal) {
        final Long accountSeq = this.restUtils.getAccountSeq(principal);

        if (searchCriteria != null) {
            if (searchCriteria.getRoomSeq() != null) {
                return ResponseEntity.ok(List.of(this.roomRepository.getByRoomSeqAndAccountAccountSeq(searchCriteria.getRoomSeq(), accountSeq)));
            }
            if (searchCriteria.getName() != null) {
                return ResponseEntity.ok(this.roomRepository.getByNameContainingIgnoreCaseAndAccountAccountSeq(searchCriteria.getName(), accountSeq));
            }
        }
        return ResponseEntity.ok(this.roomRepository.getByAccountAccountSeq(accountSeq));
    }
}
