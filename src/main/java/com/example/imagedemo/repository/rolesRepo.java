package com.example.imagedemo.repository;

import com.example.imagedemo.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface rolesRepo extends JpaRepository<Roles , Integer> {
}
