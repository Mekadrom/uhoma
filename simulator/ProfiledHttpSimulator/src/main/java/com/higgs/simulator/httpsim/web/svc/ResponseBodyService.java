package com.higgs.simulator.httpsim.web.svc;

import com.higgs.simulator.httpsim.db.entity.ResponseBody;
import com.higgs.simulator.httpsim.db.repo.ResponseBodyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ResponseBodyService {
    private final ResponseBodyRepository responseBodyRepository;

    public Collection<ResponseBody> getAllResponseBodies() {
        return this.responseBodyRepository.findAll();
    }

    public void saveResponseBody(final ResponseBody responseBody) {
        this.responseBodyRepository.save(responseBody);
    }
}
