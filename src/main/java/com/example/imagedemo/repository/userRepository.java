package com.example.imagedemo.repository;

import com.example.imagedemo.model.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface userRepository extends JpaRepository<users, Integer> {
    users findByUsername(String username);

    users findByPhoneNumber(String phoneNumber);
}
