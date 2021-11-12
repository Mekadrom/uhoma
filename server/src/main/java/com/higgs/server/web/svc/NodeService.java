package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Action;
import com.higgs.server.db.entity.ActionParameter;
import com.higgs.server.db.entity.Node;
import com.higgs.server.db.repo.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NodeService {
    private final ActionParameterService actionParameterService;
    private final ActionService actionService;
    private final NodeRepository nodeRepository;

    @Transactional
    public Node upsert(final Node node, final Long accountSeq) {
        // grab existing state of node in DB in order to remove deleted child entities
        final Node saved = this.nodeRepository.getByNodeSeqAndRoomAccountAccountSeq(node.getNodeSeq(), accountSeq);

        final Collection<Action> savedActions = saved.getActions();
        final Collection<Action> updatedActions = node.getActions();
        final Collection<ActionParameter> savedActionParameters = this.getAllParametersForNode(savedActions);
        final Collection<ActionParameter> updatedActionParameters = this.getAllParametersForNode(updatedActions);

        // remove orphaned ActionParameter entities
        this.actionParameterService.deleteAll(savedActionParameters, updatedActionParameters);
        // remove orphaned Action entities
        this.actionService.deleteAll(savedActions, updatedActions);
        // save but not flush
        this.actionParameterService.saveAll(updatedActionParameters);
        // save but not flush
        this.actionService.saveAll(updatedActions);
        // finally, flush all
        this.actionParameterService.flush();
        this.actionService.flush();

        return this.nodeRepository.saveAndFlush(node);
    }

    private Collection<ActionParameter> getAllParametersForNode(final Collection<Action> actions) {
        return actions.stream()
                .map(Action::getParameters)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Node> performNodeSearch(final Long accountSeq, final Node searchCriteria) {
        if (searchCriteria != null) {
            if (searchCriteria.getNodeSeq() != null) {
                return List.of(this.nodeRepository.getByNodeSeqAndRoomAccountAccountSeq(searchCriteria.getNodeSeq(), accountSeq));
            }
            if (searchCriteria.getRoom() != null && searchCriteria.getRoom().getRoomSeq() != null) {
                if (searchCriteria.getName() != null) {
                    return this.nodeRepository.getByNameContainingIgnoreCaseAndRoomRoomSeqAndRoomAccountAccountSeq(searchCriteria.getName(), searchCriteria.getRoom().getRoomSeq(), accountSeq);
                }
                return this.nodeRepository.getByRoomRoomSeqAndRoomAccountAccountSeq(searchCriteria.getRoom().getRoomSeq(), accountSeq);
            } else {
                if (searchCriteria.getName() != null) {
                    return this.nodeRepository.getByNameAndRoomAccountAccountSeq(searchCriteria.getName(), accountSeq);
                }
            }
        }
        return this.nodeRepository.getByRoomAccountAccountSeq(accountSeq);
    }
}
