package com.higgs.server.web.svc;

import com.higgs.server.db.entity.ActionParameter;
import com.higgs.server.db.repo.ActionParameterRepository;
import com.higgs.server.web.svc.util.PersistenceUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ActionParameterService {
    private final ActionParameterRepository actionParameterRepository;
    private final PersistenceUtils persistenceUtils;

    public void deleteAll(final Collection<ActionParameter> saved, final Collection<ActionParameter> update) {
        this.persistenceUtils.deleteAllIntersect(saved, update, ActionParameter::getActionParameterSeq, this.actionParameterRepository);
    }

    public void saveAll(final Collection<ActionParameter> actionParameters) {
        this.actionParameterRepository.saveAll(actionParameters);
    }

    public void flush() {
        this.actionParameterRepository.flush();
    }
}
