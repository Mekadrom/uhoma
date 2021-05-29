package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Node;
import com.higgs.server.db.entity.Room;

import java.util.List;

public interface NodeRepository extends SimpleNamedSequenceRepository<Node, Long> {

    List<Node> getNodesByRoomSeq(Room byId);

}
