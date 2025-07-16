package com.example.imagedemo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class sellerPrincipal implements UserDetails {
    private final Seller seller;

    public sellerPrincipal(Seller seller) {
        this.seller = seller;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = seller.getRoles().getRoleName().toUpperCase();
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return seller.getPassword();
    }

    @Override
    public String getUsername() {
        return seller.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
