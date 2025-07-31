package com.interger.quizzy.service;

import com.interger.quizzy.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;



public class MyUserPrincipal implements UserDetails {

    private final User user;

    MyUserPrincipal(User user){
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {

        //System.out.println("user password "+this.user.getPassword());
        return this.user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        // Using email as the username
        return this.user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Add method to get the underlying user


    public User getUser() {
        return this.user;
    }

    public String getString(){
        return "dsf";
    }
}