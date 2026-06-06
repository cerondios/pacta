package com.pacta.pacta_app.user.infrastructure.controller;

import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import com.pacta.pacta_app.user.domain.User;
import com.pacta.pacta_app.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository users;

    @GetMapping("/me")
    public UserResponse me(@RequestHeader(PactaTokenFilter.HEADER_USER_EMAIL) String email) {
        return users.findByEmail(email)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<UserResponse> listAll() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable String id) {
        return users.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public record UserResponse(String id, String email, Set<String> roles, Instant createdAt) {
        static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getEmail(),
                    u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                    u.getCreatedAt());
        }
    }
}
