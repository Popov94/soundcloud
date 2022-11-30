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

    @Query(value = "SELECT * FROM songs WHERE songs.title LIKE %:keyword% OR songs.artist LIKE %:keyword% OR songs.genre LIKE %:keyword%", nativeQuery = true)
    public Page<Song> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM songs\n" +
            "JOIN users_listened_songs ON(songs.id = users_listened_songs.song_id)\n" +
            "WHERE user_id = :userId\n" +
            "GROUP BY users_listened_songs.song_id\n" +
            "ORDER BY users_listened_songs.listened DESC LIMIT 5", nativeQuery = true)
    public List<Song> findFiveMostListenedForUser(@Param("userId") long userId);

    @Query(value = "SELECT * FROM songs\n" +
            "JOIN users_like_songs ON(songs.id = users_like_songs.song_id)\n" +
            "WHERE user_id = :userId\n" +
            "ORDER BY song_id DESC LIMIT 5;", nativeQuery = true)
    public List<Song> findFiveMostLikedForUser(@Param("userId") long userId);

    @Query(value = "SELECT * from songs AS s\n" +
            "WHERE s.genre = :genre \n" +
            "ORDER BY s.id DESC LIMIT 5", nativeQuery = true)
    List<Song> findFiveSuitableForUser(String genre);

    @Query(value = "SELECT genre FROM songs\n" +
            "JOIN users_like_songs ON(songs.id = users_like_songs.song_id)\n" +
            "WHERE users_like_songs.user_id = :userId\n" +
            "GROUP BY songs.genre\n" +
            "ORDER BY COUNT(genre) DESC LIMIT 1", nativeQuery = true)
    String mostListenedGenreForUser(@Param("userId") long userId);

    @Query(value = "SELECT *, COUNT(s.id) total_listens FROM songs AS s\n" +
            "JOIN users_listened_songs AS usl ON(s.id = usl.song_id)\n" +
            "GROUP BY s.id\n" +
            "ORDER BY total_listens DESC LIMIT 5", nativeQuery = true)
    List<Song> FindFiveMostListenedAtAll();

    @Query(value = "SELECT *, COUNT(s.id) total_liked FROM songs AS s\n" +
            "JOIN users_like_songs AS usl ON(s.id = usl.song_id)\n" +
            "GROUP BY s.id\n" +
            "ORDER BY total_liked DESC limit 5", nativeQuery = true)
    List<Song> FindFiveMostLikedAtAll();

    @Query(value = "SELECT *, COUNT(s.id) total_comments FROM songs AS s\n" +
            "JOIN comments AS c ON(s.id = c.song_id)\n" +
            "GROUP BY s.id\n" +
            "ORDER BY total_comments DESC limit 5", nativeQuery = true)
    List<Song> FindFiveMostCommentedAtAll();
}
