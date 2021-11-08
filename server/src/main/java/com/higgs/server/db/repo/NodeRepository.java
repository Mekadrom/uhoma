package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long>, JpaSpecificationExecutor<Node> {

    Node getByNodeSeqAndRoomAccountAccountSeq(Long nodeSeq, Long accountSeq);

    List<Node> getByRoomAccountAccountSeq(Long accountSeq);

    List<Node> getByNameAndRoomAccountAccountSeq(String name, Long accountSeq);

    List<Node> getByRoomRoomSeqAndRoomAccountAccountSeq(Long roomSeq, Long accountSeq);

    List<Node> getByNameContainingIgnoreCaseAndRoomRoomSeqAndRoomAccountAccountSeq(String name, Long roomSeq, Long accountSeq);

}
