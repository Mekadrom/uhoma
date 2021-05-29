package com.higgs.server.web.service;

import com.higgs.server.util.HASpringConstants;
import com.higgs.server.web.HASResponse;
import org.springframework.beans.factory.annotation.Value;
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
    @Value(value = HASpringConstants.VERSION)
    private String version;

    @ResponseBody
    @GetMapping(value = "version", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getVersion() {
        return HASResponse.builder(this.version)
                .status(HttpStatus.OK)
                .error(null)
                .build().toResponseEntity();
    }
}

