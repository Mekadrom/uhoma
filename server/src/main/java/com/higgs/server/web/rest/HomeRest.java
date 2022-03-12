package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Home;
import com.higgs.server.web.dto.HomeDto;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.HomeService;
import com.higgs.server.web.svc.UserLoginService;
import com.higgs.server.web.svc.util.mapper.DtoEntityMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "home")
public class HomeRest {
    private final DtoEntityMapper dtoEntityMapper;
    private final HomeService homeService;
    private final RestUtils restUtils;
    private final UserLoginService userLoginService;

    @PostMapping(value = "upsert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Home> upsert(@NonNull @RequestBody final HomeDto homeDto, @NonNull final Principal principal) {
        final Home home = this.dtoEntityMapper.map(homeDto, Home.class);
        return this.userLoginService.findByUsername(principal.getName())
                .map(userLogin -> ResponseEntity.ok(this.homeService.upsert(home.getName(), userLogin.getUserLoginSeq())))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Home>> search(@RequestBody(required = false) final HomeDto searchCriteria, @NonNull final Principal principal) {
        return ResponseEntity.ok(this.homeService.performHomeSearch(this.dtoEntityMapper.map(searchCriteria, Home.class), this.restUtils.getHomeSeqs(principal)));
    }
}
