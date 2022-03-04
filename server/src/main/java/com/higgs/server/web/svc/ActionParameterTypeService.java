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
     * @param searchCriteria search criteria to filter action parameter types by
     * @return a set of {@link ActionParameterType} representations of action parameter type definitions in both the corporate domain and home domain
     */
    public Set<ActionParameterType> performActionParameterTypeSearch(final ActionParameterType searchCriteria) {
        return Stream.concat(
                this.actionParameterTypeRepository.getByHomeHomeSeq(searchCriteria.getHomeSeq()).stream(), // home specific action parameter types
                this.actionParameterTypeRepository.getByHomeHomeSeq(1L).stream() // corporate action parameter types
        ).collect(Collectors.toSet());
    }
}
