package com.example.demo.security;

import com.example.demo.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenProvider {

  private static final String SECRET_KEY = "genie";


  public String create(UserEntity userEntity) {

    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MINUTE, 2);


    Claims claims = Jwts.claims()
            .setSubject("asac2")
            .setIssuedAt(date)
            .setExpiration(calendar.getTime());

    claims.put("secret", "thisissecret");
    // claims.put("username", userEntity.getUsername());

    return Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
            .setClaims(claims)
            .compact();
  }


  public String validateAndGetUserId(String token) {

    Claims claims = Jwts.parser()
        .setSigningKey(SECRET_KEY.getBytes())
        .parseClaimsJws(token)
        .getBody();

    return "loginOk";
  }


}

