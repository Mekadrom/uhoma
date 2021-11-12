package com.higgs.server.web.svc;

import com.higgs.server.db.entity.ActionHandler;
import com.higgs.server.db.repo.ActionHandlerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ActionHandlerService {
    private final ActionHandlerRepository actionHandlerRepository;

    /**
     * Performs a search of action handlers in both the set of corporate handler definitions and the user specific defs
     * todo: implement criteria filtering
     *
     * @param accountSeq     the account sequence to filter by
     * @param searchCriteria search criteria to filter action handlers by
     * @return a set of {@link ActionHandler} representations of action handler definitions in both the corporate domain and account domain
     */
    public Set<ActionHandler> performActionHandlerSearch(final Long accountSeq, final ActionHandler searchCriteria) {
        return Stream.concat(
                this.actionHandlerRepository.getByAccountAccountSeq(accountSeq).stream(), // account specific handlers
                this.actionHandlerRepository.getByAccountAccountSeq(1L).stream() // corporate handlers
        ).collect(Collectors.toSet());
    }
}
