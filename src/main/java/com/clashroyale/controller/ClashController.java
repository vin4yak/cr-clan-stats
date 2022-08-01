package com.clashroyale.controller;

import com.clashroyale.dto.Defaulter;
import com.clashroyale.dto.DefaulterResponse;
import com.clashroyale.dto.UnusedAttacksResponse;
import com.clashroyale.service.ClashService;
import com.clashroyale.service.ClashServiceMobileApp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/defaulters")
@Slf4j
public class ClashController {

    private final ClashService clashService;

    private final ClashServiceMobileApp clashServiceMobileApp;

    public ClashController(
            final ClashService clashService,
            final ClashServiceMobileApp clashServiceMobileApp) {
        this.clashService = clashService;
        this.clashServiceMobileApp = clashServiceMobileApp;
    }

    @GetMapping
    public ResponseEntity<DefaulterResponse> defaulterDecks() {
        List<Defaulter> defaulters = clashService.getDefaulters();
        return ResponseEntity.ok(new DefaulterResponse(HttpStatus.OK.toString(), defaulters));
    }

    @GetMapping("/discord")
    public ResponseEntity<String> sendToDiscord() {
        clashService.sendDefaultersToDiscord();
        return ResponseEntity.ok("Done!");
    }

    @GetMapping("/{clanTag}")
    public ResponseEntity<UnusedAttacksResponse> fetchUnusedAttacks(
            @PathVariable("clanTag") String clanTag) {
        UnusedAttacksResponse response = new UnusedAttacksResponse(
                "200", String.valueOf(clashServiceMobileApp.fetchUnusedAttacks(clanTag)));
        return ResponseEntity.ok(response);
    }

}
