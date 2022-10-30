package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.dto.song.ResponseSongDTO;
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
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findAllByUploader(User uploader);
    List<Song> findAllByGenre(String genre);
    Song findSongByTitle(String title);
    @Query(value = "SELECT * FROM songs AS S WHERE S.title LIKE %:charSequence%", nativeQuery = true)
    public List<Song> findSongByCharSequence(@Param("charSequence") String charSequence);

    @Query(value = "SELECT * FROM songs WHERE songs.title like %:keyword% or songs.artist like %:keyword% or songs.genre like %:keyword%", nativeQuery = true)
    public Page<Song> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM songs\n" +
            "JOIN users_listened_songs ON(songs.id = users_listened_songs.song_id)\n" +
            "WHERE user_id = :userId\n" +
            "group by users_listened_songs.listened\n" +
            "order by users_listened_songs.listened desc limit 5", nativeQuery = true)
    public List<Song> findFiveMostListenedForUser(@Param("userId") long userId);

    @Query(value = "SELECT * FROM songs\n" +
            "JOIN users_like_songs ON(songs.id = users_like_songs.song_id)\n" +
            "WHERE user_id = :userId\n" +
            "order by song_id desc limit 5;", nativeQuery = true)
    public List<Song> findFiveMostLikedForUser(@Param("userId") long userId);

    @Query(value = "select * from songs AS s\n" +
            "Where s.genre = (SELECT genre FROM songs\n" +
            "JOIN users_like_songs ON(songs.id = users_like_songs.song_id)\n" +
            "WHERE users_like_songs.user_id = :userId\n" +
            "group by songs.genre\n" +
            "order by users_like_songs.song_id desc limit 1) \n" +
            "order by s.id desc limit 5", nativeQuery = true)
    List<Song> findFiveSuitableForUser(@Param("userId") long userId);

    @Query(value = "SELECT *, COUNT(s.id) total_listens FROM songs As s\n" +
            "JOIN users_listened_songs AS usl ON(s.id = usl.song_id)\n" +
            "GROUP BY s.id\n" +
            "ORDER BY total_listens desc limit 5", nativeQuery = true)
    List<Song> FindFiveMostListenedAtAll();

    @Query(value = "SELECT *, COUNT(s.id) total_liked FROM songs As s\n" +
            "JOIN users_like_songs AS usl ON(s.id = usl.song_id)\n" +
            "GROUP BY s.id\n" +
            "ORDER BY total_liked desc limit 5", nativeQuery = true)
    List<Song> FindFiveMostLikedAtAll();

    @Query(value = "SELECT *, COUNT(s.id) total_comments FROM songs As s\n" +
            "JOIN comments AS c ON(s.id = c.song_id)\n" +
            "GROUP BY s.id\n" +
            "ORDER BY total_comments desc limit 5", nativeQuery = true)
    List<Song> FindFiveMostCommentedAtAll();
}
