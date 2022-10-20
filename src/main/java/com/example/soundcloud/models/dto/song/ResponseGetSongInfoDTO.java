package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.entities.Song;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class ResponseGetSongInfoDTO {

    private int id;
    private String title;
    private String genre;
    private String artist;
    private String url;
    private LocalDateTime createdAt;
    private int listened;
    private String description;
//    private List<UserDTO?> likedUsers;
//    private List<UserDTO?> dislikedUsers;
//    private List<CommentDTO?> comments;

        // TODO creating UserDTO and CommentDTO.


    public ResponseGetSongInfoDTO(Song songById) {
        this.id = (int) songById.getId();
        this.title = songById.getTitle();
        this.genre = songById.getGenre();
        this.artist = songById.getArtist();
        this.url = songById.getUrl();
        this.createdAt = songById.getCreatedAt();
        this.listened = songById.getListened();
        this.description = songById.getDescription();
//        this.likedUsers = new ArrayList<>();
//        this.dislikedUsers = new ArrayList<>();
//        this.comments = new ArrayList<>();

//         TODO iterating likes,dislikes and comments and add in the lists.
    }

}
