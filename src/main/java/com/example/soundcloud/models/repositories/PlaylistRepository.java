package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.entities.Playlist;
import com.example.soundcloud.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query(value = "SELECT * FROM playlists WHERE name = :name", nativeQuery = true)
    public List<Playlist> findAllByName(@Param("name") String name);

    @Query(value = "SELECT songs_id FROM playlists_songs WHERE playlists_id = :playlistId", nativeQuery = true)
    public List<Long> findAllSongsInPlaylist(@Param("playlistId") long playlistId);

    @Query(value = "SELECT * FROM playlists AS p WHERE p.name like %:keyword%", nativeQuery = true)
    public List<Playlist> findPlaylistByName(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM playlists JOIN users ON(playlists.owner_id = users.id) WHERE users.username LIKE %:keyword%", nativeQuery = true)
    public List<Playlist> findPlaylistByOwnerUsername(@Param("keyword") String keyword);

}
