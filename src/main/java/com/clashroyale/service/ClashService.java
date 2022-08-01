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
public class ClashService {

    @Value("${clash.api.members}")
    private String membersApi;

    @Value("${clash.api.river_race}")
    private String riverRaceAPI;

    @Value("${api.key}")
    private String apiKey;

    @Value("${discord.webhook.api}")
    private String discordApi;

    private RestTemplate restTemplate;

    public ClashService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Defaulter> getDefaulters() {
        ResponseEntity<ClashMembersResponse> membersResponse = getClashMembersResponseResponseEntity();
        ResponseEntity<ClashRiverRaceResponse> raceResponse = getClashRiverRaceResponseResponseEntity();

        List<String> memberTags = new ArrayList<>();
        List<Defaulter> defaulters =  new ArrayList<>();

        membersResponse.getBody().getItems().forEach(item -> {
            memberTags.add(item.getTag());
        });

        raceResponse.getBody().getClan().getParticipants().forEach(participant -> {
            if (Integer.valueOf(participant.getFame()) < 1600 && memberTags.contains(participant.getTag())) {
                defaulters.add(new Defaulter(participant.getTag(), participant.getName(), participant.getFame(),
                        participant.getDecksUsedToday()));
            }
        });

        return defaulters;
    }

    private ResponseEntity<ClashRiverRaceResponse> getClashRiverRaceResponseResponseEntity() {
        return restTemplate.exchange(URI.create(riverRaceAPI), HttpMethod.GET,
                getRequestEntity(), ClashRiverRaceResponse.class);
    }

    private ResponseEntity<ClashMembersResponse> getClashMembersResponseResponseEntity() {
        return restTemplate.exchange(URI.create(membersApi),
                HttpMethod.GET, getRequestEntity(), ClashMembersResponse.class);
    }

    public void sendDefaultersToDiscord() {
        ResponseEntity<ClashMembersResponse> membersResponse = getClashMembersResponseResponseEntity();
        ResponseEntity<ClashRiverRaceResponse> raceResponse = getClashRiverRaceResponseResponseEntity();

        List<String> memberTags = new ArrayList<>();
        List<Field> fields =  new ArrayList<>();
        int unusedAttacks = 0;

        DiscordRequest discordRequest = new DiscordRequest();
        discordRequest.setAvatarUrl("https://cdn.royaleapi.com/static/img/badge/legendary-1/A_Char_Pekka_01.png");
        discordRequest.setContent("Summary of defaulters:");
        discordRequest.setUserName("Clash Royale");


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
        discordRequest.setEmbeds(Arrays.asList(new Embed("15258703", fields,
                new Footer("Woah! So cool!", "https://i.imgur.com/fKL31aD.jpg"))));

        HttpEntity<DiscordRequest> requestEntity = new HttpEntity<>(discordRequest);
        restTemplate.exchange(URI.create(discordApi), HttpMethod.POST, requestEntity, String.class);
    }

    private HttpEntity getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return requestEntity;
    }

}
