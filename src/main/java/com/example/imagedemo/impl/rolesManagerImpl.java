package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.rolesDto;
import com.example.imagedemo.model.Roles;
import com.example.imagedemo.service.rolesService;
import com.example.imagedemo.util.rolesValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class rolesManagerImpl implements rolesValidation {
    Logger logger = LoggerFactory.getLogger(rolesManagerImpl.class);
    @Autowired
    private rolesService rolesService;
    @Override
    public ResponseDto<?> addRole(rolesDto requestDto, int requestId) {
        if(requestDto.getId()==0){
            logger.info("Creating a new role to the database");
            Roles roles = new Roles();
            roles.setRoleName(requestDto.getRoleName());
            roles.setUpdatedAt(LocalDateTime.now());
            roles.setCreatedAt(LocalDateTime.now());
            rolesService.addRole(roles);
        }
        logger.info("Updating the role for the id {}" , requestDto.getId());
        Roles roles = rolesService.findSpecificRole(requestDto.getId());
        if(roles==null){
            logger.error("Please provide a valid id");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(),Status.NOT_FOUND.getStatusDescription(),requestId,"Please provide a valid id",null);
        }
        roles.setRoleName(requestDto.getRoleName());
        roles.setUpdatedAt(LocalDateTime.now());
        logger.info("Roles data of the specific id {} is updated successfully",requestDto.getId());
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(),Status.SUCCESS.getStatusDescription(), requestId,"Updated Successfully",requestDto.getId());
    }

    @Override
    public ResponseDto<?> deleteRole(rolesDto requestDto, int requestId) {
        logger.info("Deleting the role {} from the database", requestDto.getId());
        if(requestDto.getId()==0){
            logger.error("Please make a valid request");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(),Status.BAD_REQUEST.getStatusDescription(), requestId,"Invalid request provide the id",null);
        }
        Roles roles = rolesService.findSpecificRole(requestDto.getId());
        if(roles==null){
            logger.error("Please enter a valid id");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(),Status.NOT_FOUND.getStatusDescription(), requestId,"Enter a valid id",null);
        }
        rolesService.deleteRole(roles);
        logger.info("Role {}  is deleted successfully from the database",roles.getRoleName());
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(),Status.SUCCESS.getStatusDescription(), requestId,"Role is deleted successfully",roles.getRoleName());
    }
}
