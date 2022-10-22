package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    Song getSongById(long songId);
    List<Song> getAllByGenre(String genre);
    Song getSongByTitle(String title);
    List<Song> getAllByUploader(User uploaderId);

}
