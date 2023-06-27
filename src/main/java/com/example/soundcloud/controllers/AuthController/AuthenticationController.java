package com.example.soundcloud.controllers.AuthController;


import com.example.soundcloud.models.dto.JWT.AuthenticationRequestDTO;
import com.example.soundcloud.models.dto.JWT.AuthenticationResponseDTO;
import com.example.soundcloud.models.dto.JWT.RegisterRequestDTO;
import com.example.soundcloud.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @RequestMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody RegisterRequestDTO request){

        return ResponseEntity.ok(service.register(request));
    }

    @RequestMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO request){
        return ResponseEntity.ok(service.authenticate(request));
    }
}
