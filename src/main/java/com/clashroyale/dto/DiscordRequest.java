package com.clashroyale.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscordRequest {

    private String userName;

    @JsonProperty("avatar_url") private String avatarUrl;

    private String content;

    private List<Embed> embeds;



}
