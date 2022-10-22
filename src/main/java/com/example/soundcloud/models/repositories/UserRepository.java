package com.example.soundcloud.models.repositories;


import com.example.soundcloud.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public List<User> findAllByEmail (String email);
    public List<User> findAllByUsername(String username);
    public Optional<User> findUserByUsername(String username);

    @Query(value = "SELECT * FROM users AS u WHERE u.first_name like %:keyword% or u.last_name like %:keyword%", nativeQuery = true)
    public List<User> findByKeyword(@Param("keyword") String keyword);
    public Optional<User> findUserByVerificationCode(String code);
    @Query(value = "SELECT following_id FROM followers WHERE follower_id = :followerId", nativeQuery = true)
    public List<Long> getFollowingUsers(@Param ("followerId") long followerId);



    User getUserByUsername(String username);
}
