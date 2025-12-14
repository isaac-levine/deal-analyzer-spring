package com.frontstep.deal_analyzer.repository;

import com.frontstep.deal_analyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByClerkUserId(String clerkUserId);
}
