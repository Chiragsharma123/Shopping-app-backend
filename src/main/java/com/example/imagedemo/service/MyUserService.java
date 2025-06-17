package com.example.imagedemo.service;

import com.example.imagedemo.model.userPrincipal;
import com.example.imagedemo.model.users;
import com.example.imagedemo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserService implements UserDetailsService {

    @Autowired
    private userRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        users user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found 404");
        }
        return new userPrincipal(user);
    }
}
