package com.oasis.binary_honam.repository;

import com.oasis.binary_honam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByNameAndPhoneAndEmail(String name, String phone, String email);

    @Query("SELECT e FROM User e WHERE e.name = :name AND e.phone = :phone")
    Optional<User> findEmail(String name, String phone);
}
