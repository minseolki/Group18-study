package com.springboot.w3.springboot_w3.Repository;

import com.springboot.w3.springboot_w3.Dto.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);
    Users findUsersByUsername(String username);

}