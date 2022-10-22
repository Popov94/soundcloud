package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query(value = "SELECT * FROM playlists WHERE name = :name", nativeQuery = true)
    public List<Playlist> findAllByName(@Param("name") String name);
}
