package com.higgs.server.web.service;

import com.higgs.server.web.HASResponse;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "metadata")
public class MetaDataRest {
    private BuildProperties buildProperties;

    @ResponseBody
    @GetMapping(value = "version", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getServerVersion() {
        return HASResponse.builder(this.buildProperties.getVersion())
                .status(HttpStatus.OK)
                .error(null)
                .build().toResponseEntity();
    }
}

