package com.pacta.pacta_app.landlord.application;

import com.pacta.pacta_app.user.domain.Role;
import com.pacta.pacta_app.user.domain.User;
import com.pacta.pacta_app.user.domain.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandlordService {

    private final IUserRepository users;

    public List<User> findAll() {
        return users.findAllByRole(Role.LANDLORD);
    }

    public User findById(String id) {
        User user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Landlord not found: " + id));

        if (!user.hasRole(Role.LANDLORD)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Landlord not found: " + id);
        }
        return user;
    }
}
