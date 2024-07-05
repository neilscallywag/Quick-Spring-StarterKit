package com.starterkit.demo.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;


@Data
public class MeResponseDTO {
    private String username;
    private List<String> roles;
    private Date issuedAt;
    private Date expiresAt;

}
