package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Action;
import com.higgs.server.db.repo.ActionRepository;
import com.higgs.server.web.svc.util.PersistenceUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ActionService {
    private final ActionRepository actionRepository;
    private final PersistenceUtils persistenceUtils;

    public void deleteAll(final Collection<Action> saved, final Collection<Action> update) {
        this.persistenceUtils.deleteAllIntersect(saved, update, Action::getActionSeq, this.actionRepository);
    }

    public void saveAll(final Collection<Action> actions) {
        this.actionRepository.saveAll(actions);
    }

    public void flush() {
        this.actionRepository.flush();
    }
}
