package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public List<User> findAllByEmail (String email);
    public List<User> findAllByUsername(String username);


}
