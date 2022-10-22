package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

}
