package com.higgs.server.web.service;

import com.higgs.server.db.entity.Room;
import com.higgs.server.db.entity.RoomLink;
import com.higgs.server.db.repo.NodeRepository;
import com.higgs.server.db.repo.RoomLinkRepository;
import com.higgs.server.db.repo.RoomRepository;
import com.higgs.server.web.HASResponse;
import com.higgs.server.web.exception.LinkedRoomsNotFoundException;
import com.higgs.server.web.exception.NotFoundException;
import com.higgs.server.web.service.util.RestUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(value = "room")
public class RoomRest {
    private final NodeRepository nodeRepository;
    private final RoomRepository roomRepository;
    private final RoomLinkRepository roomLinkRepository;
    private final RestUtils restUtils;

    @SneakyThrows
    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestParam(required = false, value = "id") final Optional<String> seqOpt,
                                         @RequestParam(required = false, value = "name") final Optional<String> nameOpt) {
        return this.restUtils.searchEntity(seqOpt, nameOpt, Room.class, this.roomRepository);
    }

    @SneakyThrows
    @GetMapping(value = "links", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLinkedRooms(@RequestParam(value = "id") final String seqOpt) {
        final List<Room> linkedRooms = new ArrayList<>();
        final Room startRoom = this.roomRepository.findById(Long.valueOf(seqOpt)).orElseThrow(() -> new NotFoundException(Room.class, Optional.of(seqOpt), Optional.empty()));
        final Collection<RoomLink> roomLinks = this.roomLinkRepository.getAllByStartRoom(startRoom);
        for (final RoomLink roomLink : roomLinks) {
            linkedRooms.add(roomLink.getEndRoom());
        }
        return Optional.of(linkedRooms)
                .filter(it -> it.size() > 0)
                .map(it -> HASResponse.builder(it.toArray(Room[]::new))
                        .status(HttpStatus.OK)
                        .error(null)
                        .build().toResponseEntity())
                .orElseThrow(() -> new LinkedRoomsNotFoundException(startRoom));
    }

    @GetMapping(value = "nodes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getNodesInRoom(@RequestParam(value = "id") final String roomSeq) {
        return HASResponse.builder(this.nodeRepository.getNodesByRoom(this.roomRepository.getById(Long.valueOf(roomSeq)).getRoomSeq())).status(HttpStatus.OK).error(null).build().toResponseEntity();
    }
}
