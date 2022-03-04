package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Room;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.RoomService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "room")
public class RoomRest {
    private final RestUtils restUtils;
    private final RoomService roomService;

    @PostMapping(value = "upsert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Room> upsert(@RequestBody(required = false) final Room room) {
        return ResponseEntity.ok(this.roomService.upsert(room));
    }

    @SneakyThrows
    @PostMapping(value = "search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Room>> search(@RequestBody(required = false) final Room searchCriteria, @NonNull final Principal principal) {
        this.restUtils.filterInvalidRequest(principal, searchCriteria);
        return ResponseEntity.ok(this.roomService.performRoomSearch(searchCriteria, this.restUtils.getHomeSeqs(principal)));
    }
}
