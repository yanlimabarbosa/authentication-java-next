package com.yan.authentication.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokensResponse {
    private String access_token;
    private String refresh_token;
}


