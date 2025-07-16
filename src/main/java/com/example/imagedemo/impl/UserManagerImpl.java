package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.common.addressUsingKey;
import com.example.imagedemo.dto.userDto;
import com.example.imagedemo.model.Seller;
import com.example.imagedemo.service.*;
import com.example.imagedemo.util.UserValidation;
import com.example.imagedemo.dto.loginRequestDto;
import com.example.imagedemo.model.Cart;
import com.example.imagedemo.model.users;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserManagerImpl implements UserValidation {
   Logger logger = LoggerFactory.getLogger(UserManagerImpl.class);
    @Autowired
    private userService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private jwtService jwtservice;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private cartUseService cartUseService;
    @Autowired
    private cartOrderProductService cartService;
    @Autowired
    private orderCartService orderService;
    @Autowired
    private addressUsingKey addressUsingKey;
    @Autowired
    private sellerService sellerService;
    @Transactional
    @Override
    public ResponseDto<?> UserRegister(userDto user, int requestId) throws Exception {
        logger.info("Registering user : {}", user.getUsername());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || user.getId() != 0 || !auth.getPrincipal().equals("anonymousUser")) {
            logger.error("Invalid request need to login");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Invalid request need to logout", null);
        }
        if (user.getId() != 0 && auth != null) {
            users userToRegister = userService.getSpecificUser(user.getId());
            if (userToRegister == null) {
                logger.error("Please provide a valid id");
                return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Please provide a valid", null);
            }
            if (userService.getByUsername(user.getUsername()) != null) {
                logger.error("Registration Failed: Username {} already exists", user.getUsername());
                return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Username is already taken", null);
            }
            if (user.getPassword() != null) {
                userToRegister.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            if (user.getUsername() != null) {
                userToRegister.setUsername(user.getUsername());
            }
            if (user.getRole() != null) {
                userToRegister.setRole(user.getRole());
            }
            if (user.getPhoneNumber() != null) {
                userToRegister.setPhoneNumber(user.getPhoneNumber());
            }
            userToRegister.setUpdatedAt(LocalDateTime.now());
            if (user.getAddress() != null) {
                userToRegister.setAddress(user.getAddress());
            }else if(user.getPinCode()!=null && user.getAddress()==null){
                String add = addressUsingKey.getAddressFromPin(user.getPinCode());
                userToRegister.setAddress(add);
            }
            userService.registerUser(userToRegister);
            logger.info("user {} is updated successfully", userToRegister.getUsername());
            return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "user is updated successfully", userToRegister.getUsername());
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            logger.error("Registration failed : username is null or empty");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User can not be blank", null);
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            logger.error("Registration Failed: Password can't be empty or null");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Password must be of at least 6 characters", null);
        }
        if (user.getRole() == null || (!user.getRole().equalsIgnoreCase("user") && !user.getRole().equalsIgnoreCase("admin"))) {
            logger.error("Invalid role : {}", user.getRole());
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Role Should be admin or user", null);
        }
        if (userService.getByUsername(user.getUsername()) != null) {
            logger.error("Registration Failed: Username {} already exists", user.getUsername());
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Username is already taken", null);
        }

        users u = new users();
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        u.setUsername(user.getUsername());
        u.setRole(user.getRole());
        u.setPhoneNumber(user.getPhoneNumber());
        u.setCreatedat(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        u.setPhoneNumber(user.getPhoneNumber());
        if(user.getAddress()!=null) {
            u.setAddress(user.getAddress());
        }
        else if(user.getAddress()==null && user.getPinCode()!=null){
            u.setAddress(addressUsingKey.getAddressFromPin(user.getPinCode()));
        }
        userService.registerUser(u);
        Cart cart = new Cart();
        cart.setStatus("Empty");
        cart.setUser(u);
        cartUseService.setStatus(cart);
        logger.info("User {} registered successfully", u.getUsername());

        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "User registered successfully", u.getUsername());

    }

    @Override
    public ResponseDto<?> LoginUser(loginRequestDto User, HttpServletResponse response, int requestId) {
        logger.info("{} is trying to login", User.getUsername());
        if (User.getUsername() == null || User.getUsername().trim().isEmpty()) {
            logger.error("Login failed: Username is null or empty");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Username is required", null);
        }
        if (User.getPassword() == null || User.getPassword().trim().isEmpty()) {
            logger.error("Login failed: Password is null or empty");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Password is required", null);
        }
        Authentication p = SecurityContextHolder.getContext().getAuthentication();
        if (p.getName() == null || !p.getPrincipal().equals("anonymousUser")) {
            logger.error("User {} is already logged in", p.getName());
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User is already logged in", null);
        }
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(User.getUsername(), User.getPassword()));
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("ROLE_USER");
            if (auth.isAuthenticated()) {
                String token = jwtservice.gettoken(User.getUsername() , role);
                ResponseCookie loginCookie = ResponseCookie.from("jwtToken", token).httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(10 * 60 * 60).build();
                response.addHeader(HttpHeaders.SET_COOKIE, loginCookie.toString());
                logger.info("{} {} logged in successfully",role, User.getUsername());
                if("ROLE_USER".equalsIgnoreCase(role)) {
                    users u = userService.getByUsername(User.getUsername());
                    u.setStatus("Active");
                    userService.registerUser(u);
                }
                if("ROLE_SELLER".equalsIgnoreCase(role)){
                    Seller seller = sellerService.findByEmail(User.getUsername());
                    seller.setStatus("Active");
                    sellerService.registerSeller(seller);
                }
                return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "User logged in successfully", User.getUsername());
            } else {
                logger.error("Login failed: Invalid credentials for {}", User.getUsername());
                return new ResponseDto<>(Status.UNAUTHORIZED.getStatusCode().value(), Status.UNAUTHORIZED.getStatusDescription(), requestId, "Invalid credentials", null);
            }
        } catch (BadCredentialsException e) {
            logger.error("Login failed: Bad credentials for {}", User.getUsername());
            return new ResponseDto<>(Status.UNAUTHORIZED.getStatusCode().value(), Status.UNAUTHORIZED.getStatusDescription(), requestId, "Invalid Credentials", null);
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, "An error occurred during login", null);
        }
    }

    @Override
    public ResponseDto<?> logOutUser(HttpServletResponse response, int requestId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("User {} is trying to log out from the website", auth.getName());
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("Logout attempt failed: No authenticated user found");
            return new ResponseDto<>(Status.UNAUTHORIZED.getStatusCode().value(), Status.UNAUTHORIZED.getStatusDescription(), requestId, "No user is currently logged in", null);
        }
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse("ROLE_USER");
        String username = auth.getName();
        if (userService.getByUsername(username) != null || sellerService.findByEmail(username)!=null) {
            ResponseCookie clearCookie = ResponseCookie.from("jwtToken", "").httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0).build();
            SecurityContextHolder.clearContext();
            response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
            if("ROLE_USER".equalsIgnoreCase(role)) {
                users u = userService.getByUsername(username);
                u.setStatus("Inactive");
                userService.registerUser(u);
            }
            if("ROLE_SELLER".equalsIgnoreCase(role)){
                Seller seller = sellerService.findByEmail(username);
                seller.setStatus("Inactive");
                sellerService.registerSeller(seller);
            }
            logger.info("User {} logged out successfully", username);
            return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "User Logged Out Successfully", username);
        } else {
            logger.error("Logout failed: User not found in the database");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Invalid logout request", null);
        }
    }

    @Transactional
    @Override
    public ResponseDto<?> deleteUser(userDto request, int requestId) throws Exception {
        if(request.getId()==0){
            logger.error("Please provide a user id to be deleted");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(),Status.BAD_REQUEST.getStatusDescription(),requestId,"Please make a valid request provide the user id to be deleted",null);
        }
        users u = userService.getSpecificUser(request.getId());
        if(u==null && request.getId()!=0){
            logger.error("The user you want to delete doesn't exists in the database");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(),Status.NOT_FOUND.getStatusDescription(), requestId,"User doesn't found in the database",null);
        }
        String usern = u.getUsername();
        logger.info("Trying to delete  user {}", u.getUsername());
        Cart c = u.getCart();
        if (u != null) {
            cartService.deleteByCart(c);
            orderService.deleteByCart(c);
            userService.deleteUser(request.getId());
            logger.info("User  {} is deleted successfully ", u.getUsername());
            return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "User is deleted Successfully", usern);
        }
        logger.error("User {} doesn't exists", u.getUsername());
        return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User doesn't found in the database", null);
    }

}

