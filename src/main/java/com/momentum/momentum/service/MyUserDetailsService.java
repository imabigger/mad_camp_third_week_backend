package com.momentum.momentum.service;

import com.momentum.momentum.entity.User;
import com.momentum.momentum.exception.UserIdAlreadyExistsException;
import com.momentum.momentum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(),
                new ArrayList<>());
    }

    public User saveUser(User user) {
        if (userRepository.existsByUserId(user.getUserId())) {
            throw new UserIdAlreadyExistsException("User ID already exists: " + user.getUserId());
        }
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public  User findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
}
