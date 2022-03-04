package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Home;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.HomeService;
import com.higgs.server.web.svc.UserLoginService;
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
    private final HomeService homeService;
    private final RestUtils restUtils;
    private final UserLoginService userLoginService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Home> create(@RequestBody @NonNull final Home newHome, final Principal principal) {
        return this.userLoginService.findByUsername(principal.getName())
                .map(userLogin -> ResponseEntity.ok(this.homeService.createHome(newHome.getName(), userLogin.getUserLoginSeq())))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Home>> search(@RequestBody(required = false) final Home searchCriteria, @NonNull final Principal principal) {
        return ResponseEntity.ok(this.homeService.performHomeSearch(searchCriteria, this.restUtils.getHomeSeqs(principal)));
    }
}
