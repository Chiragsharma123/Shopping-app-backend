package com.example.imagedemo.service;

import com.example.imagedemo.model.users;
import com.example.imagedemo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class userService {
    @Autowired
    private userRepository userRepo;

    public void deleteUser(int uid) {
        userRepo.deleteById(uid);
    }

    public users getByUsername(String name) {
        return userRepo.findByUsername(name);
    }

    public void registerUser(users u) {
        userRepo.save(u);
    }

    public users getSpecificUser(int id) {
        return userRepo.findById(id).orElse(null);
    }


    public users getUserByPhone(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber);
    }

    public List<users> getAllUsers() {
        return userRepo.findAll();
    }
}
