package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Node;

import java.util.List;

public interface NodeRepository extends SimpleNamedSequenceRepository<Node, Long> {

    Node getByNodeSeqAndRoomAccountAccountSeq(Long nodeSeq, Long accountSeq);

    List<Node> getByRoomAccountAccountSeq(Long accountSeq);

    List<Node> getByNameAndRoomAccountAccountSeq(String name, Long accountSeq);

    List<Node> getByRoomRoomSeqAndRoomAccountAccountSeq(Long roomSeq, Long accountSeq);

    List<Node> getByNameContainingIgnoreCaseAndRoomRoomSeqAndRoomAccountAccountSeq(String name, Long roomSeq, Long accountSeq);

}
