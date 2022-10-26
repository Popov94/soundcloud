package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findAllByUploader(User uploader);
    List<Song> findAllByGenre(String genre);
    Song findSongByTitle(String title);
    @Query(value = "SELECT * FROM songs AS S WHERE S.title LIKE %:charSequence%", nativeQuery = true)
    public List<Song> findSongByCharSequence(@Param("charSequence") String charSequence);

    @Query(value = "SELECT * FROM songs WHERE songs.title like %:keyword% or songs.artist like %:keyword% or songs.genre like %:keyword%", nativeQuery = true)
    public Page<Song> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

//    public Page<Song> findAll()
}
