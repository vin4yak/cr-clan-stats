package com.clashroyale.service;

import com.clashroyale.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ClashServiceMobileApp {

    @Value("https://api.clashroyale.com/v1/clans/%23")
    private String membersApi;

    @Value("https://api.clashroyale.com/v1/clans/%23")
    private String riverRaceAPI;

    @Value("${api.key}")
    private String apiKey;

    private RestTemplate restTemplate;

    public ClashServiceMobileApp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private ResponseEntity<ClashRiverRaceResponse> getClashRiverRaceResponseResponseEntity(String clanTag) {
        return restTemplate.exchange(URI.create(riverRaceAPI + clanTag + "/currentriverrace"), HttpMethod.GET,
                getRequestEntity(), ClashRiverRaceResponse.class);
    }

    private ResponseEntity<ClashMembersResponse> getClashMembersResponseResponseEntity(String clanTag) {
        return restTemplate.exchange(URI.create(membersApi + clanTag + "/members"),
                HttpMethod.GET, getRequestEntity(), ClashMembersResponse.class);
    }

    public int fetchUnusedAttacks(String clanTag) {
        ResponseEntity<ClashMembersResponse> membersResponse = getClashMembersResponseResponseEntity(clanTag);
        ResponseEntity<ClashRiverRaceResponse> raceResponse = getClashRiverRaceResponseResponseEntity(clanTag);

        List<String> memberTags = new ArrayList<>();
        List<Field> fields =  new ArrayList<>();
        int unusedAttacks = 0;

        membersResponse.getBody().getItems().forEach(item -> {
            memberTags.add(item.getTag());
        });

        for (Participants participant : raceResponse.getBody().getClan().getParticipants()) {
            if (participant.getDecksUsedToday() < 4 && memberTags.contains(participant.getTag())) {
                fields.add(new Field(participant.getName(), "Attacks Used: " + participant.getDecksUsedToday(), true));
                unusedAttacks = unusedAttacks + (4 - participant.getDecksUsedToday());
            }
        }

        fields.add(new Field("Attacks Remaining:", String.valueOf(unusedAttacks), false));
        return unusedAttacks;
    }

    private HttpEntity getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return requestEntity;
    }

}
