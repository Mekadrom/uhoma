package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Action;
import com.higgs.server.db.entity.ActionParameter;
import com.higgs.server.db.entity.Node;
import com.higgs.server.db.repo.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NodeService {
    private final ActionParameterService actionParameterService;
    private final ActionService actionService;
    private final HomeService homeService;
    private final NodeRepository nodeRepository;

    @Transactional
    public Node upsert(final Node node) {
        // grab existing state of node in DB in order to remove deleted child entities
        final Optional<Node> savedOpt = this.nodeRepository.findById(node.getNodeSeq());

        node.setHome(this.homeService.getHome(node.getRoom().getHomeSeq()));
        if (savedOpt.isEmpty()) {
            this.nodeRepository.saveAndFlush(node);
        }

        final Collection<Action> savedActions = savedOpt.map(Node::getActions).orElse(Collections.emptyList());
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

    public List<Node> performNodeSearch(final Node searchCriteria, final Collection<Long> homeSeqs) {
        if (searchCriteria != null) {
            if (searchCriteria.getNodeSeq() != null) {
                return List.of(this.nodeRepository.getById(searchCriteria.getNodeSeq()));
            }
            if (searchCriteria.getRoom() != null && searchCriteria.getRoom().getRoomSeq() != null) {
                if (searchCriteria.getName() != null) {
                    return this.nodeRepository.getByNameContainingIgnoreCaseAndRoomRoomSeqAndHomeHomeSeq(searchCriteria.getName(), searchCriteria.getRoom().getRoomSeq(), searchCriteria.getHomeSeq());
                }
                return this.nodeRepository.getByRoomRoomSeqAndHomeHomeSeq(searchCriteria.getRoom().getRoomSeq(), searchCriteria.getHomeSeq());
            } else {
                if (searchCriteria.getHomeSeq() != null) {
                    return this.nodeRepository.getByHomeHomeSeqIn(List.of(searchCriteria.getHomeSeq()));
                }
                if (searchCriteria.getName() != null) {
                    return this.nodeRepository.getByNameAndHomeHomeSeq(searchCriteria.getName(), searchCriteria.getHomeSeq());
                }
            }
        }
        return this.nodeRepository.getByHomeHomeSeqIn(homeSeqs);
    }
}
