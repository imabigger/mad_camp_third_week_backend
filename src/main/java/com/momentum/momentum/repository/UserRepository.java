package com.momentum.momentum.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.momentum.momentum.entity.User;

// 몽고 디비에서 유저 찾기

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUserId(String userId);
    User findByUsername(String username);
    User findByUserId(String userId);
}