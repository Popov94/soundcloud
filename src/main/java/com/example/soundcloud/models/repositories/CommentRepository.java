package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query(value = "SELECT * FROM comments Where song_id = :songId", nativeQuery = true)
    public List<Comment> findCommentBySong(@Param("songId") long songId);
}
