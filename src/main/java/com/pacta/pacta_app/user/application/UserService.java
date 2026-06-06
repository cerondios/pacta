package com.pacta.pacta_app.user.application;

import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.user.domain.Phone;
import com.pacta.pacta_app.user.domain.Role;
import com.pacta.pacta_app.user.domain.User;
import com.pacta.pacta_app.user.domain.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final MetricRecorder  metrics;

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User completeOnboarding(String userId, Set<Role> roles, String city) {
        User updated = getById(userId).completeOnboarding(roles, city);
        userRepository.update(updated);
        metrics.incrementCounter("user.onboarding.completed");
        return updated;
    }

    @Transactional
    public User updateProfile(String userId, String fullName, Phone phone, String updatedBy) {
        User updated = getById(userId).updateProfile(fullName, phone, updatedBy);
        userRepository.update(updated);
        return updated;
    }

    @Transactional
    public User block(String userId, String adminId) {
        User updated = getById(userId).block(adminId);
        userRepository.update(updated);
        metrics.incrementCounter("user.blocked");
        log.info("User blocked userId={} by={}", userId, adminId);
        return updated;
    }

    @Transactional
    public User unblock(String userId, String adminId) {
        User updated = getById(userId).unblock(adminId);
        userRepository.update(updated);
        metrics.incrementCounter("user.unblocked");
        log.info("User unblocked userId={} by={}", userId, adminId);
        return updated;
    }

    @Transactional
    public void remove(String userId, String reason, String removedBy) {
        User updated = getById(userId).remove(reason, removedBy);
        userRepository.update(updated);
        metrics.incrementCounter("user.removed");
        log.info("User removed userId={} by={}", userId, removedBy);
    }
}
