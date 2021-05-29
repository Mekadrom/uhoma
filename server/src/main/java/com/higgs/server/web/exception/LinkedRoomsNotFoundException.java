package com.higgs.server.web.exception;

import com.higgs.server.db.entity.Room;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LinkedRoomsNotFoundException extends Exception {
    public LinkedRoomsNotFoundException(final Room startRoom) {
        super(String.format("%s has no linked rooms", startRoom.getName()));
    }
}
