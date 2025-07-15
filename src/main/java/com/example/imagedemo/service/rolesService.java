package com.example.imagedemo.service;

import com.example.imagedemo.model.Roles;
import com.example.imagedemo.repository.rolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class rolesService {
    @Autowired
    private rolesRepo rolesRepo;
    public void addRole(Roles roles) {
        rolesRepo.save(roles);
    }

    public Roles findSpecificRole(int id) {
      return   rolesRepo.findById(id).orElse(null);
    }

    public void deleteRole(Roles roles) {
        rolesRepo.delete(roles);
    }
}
