package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Room;
import com.higgs.server.web.dto.RoomDto;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.RoomService;
import com.higgs.server.web.svc.util.mapper.DtoEntityMapper;
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
    private final DtoEntityMapper dtoEntityMapper;
    private final RestUtils restUtils;
    private final RoomService roomService;

    @PostMapping(value = "upsert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Room> upsert(@NonNull @RequestBody final RoomDto roomDto) {
        return ResponseEntity.ok(this.roomService.upsert(this.dtoEntityMapper.map(roomDto, Room.class)));
    }

    @SneakyThrows
    @PostMapping(value = "search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Room>> search(@RequestBody(required = false) final RoomDto searchCriteria, @NonNull final Principal principal) {
        final Room room = this.dtoEntityMapper.map(searchCriteria, Room.class);
        this.restUtils.filterInvalidRequest(principal, room);
        return ResponseEntity.ok(this.roomService.performRoomSearch(room, this.restUtils.getHomeSeqs(principal)));
    }
}
