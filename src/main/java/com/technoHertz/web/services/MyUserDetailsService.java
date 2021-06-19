package com.technoHertz.web.services;

import com.technoHertz.web.models.UserData;
import com.technoHertz.web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        String username = s;
        String password;
        if(s.equals("admin")) {
            password = "admin";
        }
        else {
            UserData userData = userRepository.findByEmail(s).get();
            password = userData.getPassword();
        }

        return new User(username, password,
                new ArrayList<>());
    }
}
