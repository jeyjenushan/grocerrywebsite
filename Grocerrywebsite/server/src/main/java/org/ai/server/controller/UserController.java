package org.ai.server.controller;

import lombok.AllArgsConstructor;
import org.ai.server.dto.Response;
import org.ai.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/userDetails")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Response> getUserDetails(@RequestHeader("Authorization") String token) {
        Response response=userService.getUserByToken(token);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
