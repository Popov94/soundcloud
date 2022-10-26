package com.example.soundcloud.models.dao;

import com.example.soundcloud.models.dto.song.ResponseSongFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SongDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<ResponseSongFilterDTO> filter(String searchTitle, String filterBy, String orderBy, int page, int songsPerPage) throws SQLException {
        List<ResponseSongFilterDTO> songs = new ArrayList<>();

            String sql ="SELECT S.title, " +
                "S.id AS songId, " +
                "U.username AS uploadedBy, " +
                "S.listened, " +
                "COUNT(DISTINCT C.id) AS comments, " +
                "COUNT(DISTINCT ULS.user_id) AS likes, " +
                "COUNT(DISTINCT UDS.user_id) AS dislikes, " +
                "S.created_at AS upload_date " +
                "FROM songs S " +
                "LEFT JOIN comments C ON S.id = C.song_id " +
                "LEFT JOIN users U ON S.uploader_id = U.id " +
                "LEFT JOIN users_like_songs ULS ON S.id = ULS.song_id " +
                "LEFT JOIN users_dislike_songs UDS ON S.id = UDS.song_id " +
                "LEFT JOIN playlists_songs PS ON S.id = PS.songs_id " +
                "WHERE S.title LIKE \"%%%s%%\" " +
                "GROUP BY S.id " +
                "ORDER BY %s %s " +
                "LIMIT %d OFFSET %d";

        sql = String.format(sql, searchTitle, filterBy, orderBy, songsPerPage,(songsPerPage * (page - 1)));
        DataSource dataSource = jdbcTemplate.getDataSource();
        if(dataSource != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    long songId = rs.getInt("songId");
                    String title = rs.getString("title");
                    int listened = rs.getInt("listened");
                    int comments = rs.getInt("comments");
                    int likes = rs.getInt("likes");
                    int dislikes = rs.getInt("dislikes");
                    LocalDateTime createdAt = rs.getTimestamp("upload_date").toLocalDateTime();
                    String uploadedBy = rs.getString("uploadedBy");
                    songs.add(new ResponseSongFilterDTO(title, uploadedBy, songId, listened, likes, dislikes, createdAt,comments));
                }
            } catch (SQLException e) {
                throw new SQLException(e.getMessage(),"Problem with connection to DB!");
            }
        }
        return songs;
    }

    public List<ResponseSongFilterDTO> findSongsByGenreForUser(long uid, int page, int songsPerPage) throws SQLException {
        List<ResponseSongFilterDTO> songs = new ArrayList<>();

        String sql = "SELECT S.title, \n" +
                "S.id AS songId, \n" +
                "U.username AS uploadedBy, \n" +
                "S.listened, \n" +
                "COUNT(DISTINCT C.id) AS comments,\n" +
                "COUNT(DISTINCT ULS.user_id) AS likes, \n" +
                "COUNT(DISTINCT UDS.user_id) AS dislikes, \n" +
                "S.created_at AS upload_date \n" +
                "FROM songs S \n" +
                "LEFT JOIN comments C ON S.id = C.song_id \n" +
                "LEFT JOIN users U ON S.uploader_id = U.id \n" +
                "LEFT JOIN users_like_songs ULS ON S.id = ULS.song_id\n" +
                "LEFT JOIN users_dislike_songs UDS ON S.id = UDS.song_id \n" +
                "LEFT JOIN playlists_songs PS ON S.id = PS.songs_id \n" +
                "WHERE genre = (SELECT \n" +
                "S.genre AS GENRE\n" +
                "FROM\n" +
                "songs AS S\n" +
                "JOIN\n" +
                "users_like_songs AS ULS ON (ULS.song_id = S.id)\n" +
                "WHERE\n" +
                "ULS.user_id = %d\n" +
                "GROUP BY genre\n" +
                "ORDER BY genre DESC\n" +
                "LIMIT 1)\n" +
                "GROUP BY S.id\n" +
                "ORDER BY listened DESC\n" +
                "LIMIT %d OFFSET %d";

        sql = String.format(sql, uid, songsPerPage, (songsPerPage * (page - 1)));
        DataSource dataSource = jdbcTemplate.getDataSource();
        if(dataSource != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    long songId = rs.getInt("songId");
                    String title = rs.getString("title");
                    int listened = rs.getInt("listened");
                    int comments = rs.getInt("comments");
                    int likes = rs.getInt("likes");
                    int dislikes = rs.getInt("dislikes");
                    LocalDateTime createdAt = rs.getTimestamp("upload_date").toLocalDateTime();
                    String uploadedBy = rs.getString("uploadedBy");
                    songs.add(new ResponseSongFilterDTO(title, uploadedBy, songId, listened, likes, dislikes, createdAt,comments));
                }
            } catch (SQLException e) {
                throw new SQLException(e.getMessage(),"Problem with connection to DB!");
            }
        }
        return songs;
    }

    public List<ResponseSongFilterDTO> findTopListened(int page, int songsPerPage) throws SQLException {
        List<ResponseSongFilterDTO> songs = new ArrayList<>();

        String sql = "SELECT S.title, \n" +
                "S.id AS songId, \n" +
                "U.username AS uploadedBy, \n" +
                "S.listened, \n" +
                "COUNT(DISTINCT C.id) AS comments,\n" +
                "COUNT(DISTINCT ULS.user_id) AS likes, \n" +
                "COUNT(DISTINCT UDS.user_id) AS dislikes, \n" +
                "S.created_at AS upload_date \n" +
                "FROM songs S \n" +
                "LEFT JOIN comments C ON S.id = C.song_id \n" +
                "LEFT JOIN users U ON S.uploader_id = U.id \n" +
                "LEFT JOIN users_like_songs ULS ON S.id = ULS.song_id\n" +
                "LEFT JOIN users_dislike_songs UDS ON S.id = UDS.song_id \n" +
                "LEFT JOIN playlists_songs PS ON S.id = PS.songs_id \n" +
                "GROUP BY S.id\n" +
                "ORDER BY listened DESC\n" +
                "LIMIT %d OFFSET %d";

        sql = String.format(sql, songsPerPage, (songsPerPage * (page - 1)));
        DataSource dataSource = jdbcTemplate.getDataSource();
        if(dataSource != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    long songId = rs.getInt("songId");
                    String title = rs.getString("title");
                    int listened = rs.getInt("listened");
                    int comments = rs.getInt("comments");
                    int likes = rs.getInt("likes");
                    int dislikes = rs.getInt("dislikes");
                    LocalDateTime createdAt = rs.getTimestamp("upload_date").toLocalDateTime();
                    String uploadedBy = rs.getString("uploadedBy");
                    songs.add(new ResponseSongFilterDTO(title, uploadedBy, songId, listened, likes, dislikes, createdAt,comments));
                }
            } catch (SQLException e) {
                throw new SQLException(e.getMessage(),"Problem with connection to DB!");
            }
        }
        return songs;
    }

    public List<ResponseSongFilterDTO> findSongsByGenre(int page, int songsPerPage) throws SQLException {
        List<ResponseSongFilterDTO> songs = new ArrayList<>();

        String sql = "SELECT S.title, \n" +
                "S.id AS songId, \n" +
                "U.username AS uploadedBy, \n" +
                "S.listened, \n" +
                "COUNT(DISTINCT C.id) AS comments,\n" +
                "COUNT(DISTINCT ULS.user_id) AS likes, \n" +
                "COUNT(DISTINCT UDS.user_id) AS dislikes, \n" +
                "S.created_at AS upload_date \n" +
                "FROM songs S \n" +
                "LEFT JOIN comments C ON S.id = C.song_id \n" +
                "LEFT JOIN users U ON S.uploader_id = U.id \n" +
                "LEFT JOIN users_like_songs ULS ON S.id = ULS.song_id\n" +
                "LEFT JOIN users_dislike_songs UDS ON S.id = UDS.song_id \n" +
                "LEFT JOIN playlists_songs PS ON S.id = PS.songs_id \n" +
                "WHERE genre = (SELECT \n" +
                "S.genre AS GENRE\n" +
                "FROM songs AS S\n" +
                "JOIN users_like_songs AS ULS ON (ULS.song_id = S.id)\n" +
                "GROUP BY genre\n" +
                "ORDER BY COUNT(DISTINCT ULS.user_id) DESC\n" +
                "LIMIT 1\n)\n" +
                "GROUP BY S.id\n" +
                "ORDER BY listened DESC\n" +
                "LIMIT %d OFFSET %d";

        sql = String.format(sql, songsPerPage, (songsPerPage * (page - 1)));
        DataSource dataSource = jdbcTemplate.getDataSource();
        if(dataSource != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    long songId = rs.getInt("songId");
                    String title = rs.getString("title");
                    int listened = rs.getInt("listened");
                    int comments = rs.getInt("comments");
                    int likes = rs.getInt("likes");
                    int dislikes = rs.getInt("dislikes");
                    LocalDateTime createdAt = rs.getTimestamp("upload_date").toLocalDateTime();
                    String uploadedBy = rs.getString("uploadedBy");
                    songs.add(new ResponseSongFilterDTO(title, uploadedBy, songId, listened, likes, dislikes, createdAt,comments));
                }
            } catch (SQLException e) {
                throw new SQLException(e.getMessage(),"Problem with connection to DB!");
            }
        }
        return songs;
    }

}
