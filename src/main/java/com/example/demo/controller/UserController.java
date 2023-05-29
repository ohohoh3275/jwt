package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final TokenProvider tokenProvider;

  @PostMapping("/sign-up")
  public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
    try {
      if(userDTO == null || userDTO.getPassword() == null ) {
        throw new RuntimeException("Invalid Password value.");
      }
      UserEntity user = UserEntity.builder()
          .username(userDTO.getUsername())
          .password(passwordEncoder.encode(userDTO.getPassword()))
          .build();

      UserEntity registeredUser = userService.create(user);
      UserDTO responseUserDTO = UserDTO.builder()
          .id(registeredUser.getId())
          .username(registeredUser.getUsername())
          .build();

      return ResponseEntity.ok().body(responseUserDTO);

    } catch (Exception e) {

      return ResponseEntity
          .badRequest()
          .body(ResponseDTO
                  .builder()
                  .error(e.getMessage())
                  .build());
    }
  }


  @PostMapping("/sign-in")
  public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {

    UserEntity user = userService.getByCredentials(
        userDTO.getUsername(),
        userDTO.getPassword(),
        passwordEncoder);

    if(user != null) {
      // 토큰 생성
      final String token = tokenProvider.create(user);
      final UserDTO responseUserDTO = UserDTO.builder()
          .username(user.getUsername())
          .id(user.getId())
          .token(token)
          .build();
      return ResponseEntity.ok().body(responseUserDTO);
    } else {
      ResponseDTO responseDTO = ResponseDTO.builder()
          .error("Login failed.")
          .build();
      return ResponseEntity
          .badRequest()
          .body(responseDTO);
    }
  }


}

