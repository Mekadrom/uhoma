package com.higgs.server.db.repo;

import com.higgs.server.db.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long>, JpaSpecificationExecutor<Node> {

    List<Node> getByNameAndHomeHomeSeqIn(String name, Collection<Long> homeSeq);

    List<Node> getByRoomRoomSeqAndHomeHomeSeq(Long roomSeq, Long homeSeq);

    List<Node> getByNameContainingIgnoreCaseAndRoomRoomSeqAndHomeHomeSeq(String name, Long roomSeq, Long homeSeq);

    List<Node> getByHomeHomeSeqIn(Collection<Long> homeSeqs);

}
