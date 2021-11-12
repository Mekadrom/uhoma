package com.higgs.server.web.svc;

import com.higgs.server.db.entity.ActionParameterType;
import com.higgs.server.db.repo.ActionParameterTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ActionParameterTypeService {
    private final ActionParameterTypeRepository actionParameterTypeRepository;

    /**
     * Performs a search of action parameter types in both the set of corporate type definitions and the user specific defs
     * todo: implement criteria filtering
     *
     * @param accountSeq     the account sequence to filter by
     * @param searchCriteria search criteria to filter action parameter types by
     * @return a set of {@link ActionParameterType} representations of action parameter type definitions in both the corporate domain and account domain
     */
    public Set<ActionParameterType> performActionParameterTypeSearch(final Long accountSeq, final ActionParameterType searchCriteria) {
        return Stream.concat(
                this.actionParameterTypeRepository.getByAccountAccountSeq(accountSeq).stream(), // account specific action parameter types
                this.actionParameterTypeRepository.getByAccountAccountSeq(1L).stream() // corporate action parameter types
        ).collect(Collectors.toSet());
    }
}
