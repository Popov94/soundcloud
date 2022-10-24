package com.example.soundcloud.service;

import com.example.soundcloud.models.dao.SongDAO;
import com.example.soundcloud.models.dto.DislikeDTO;
import com.example.soundcloud.models.dto.LikeDTO;
import com.example.soundcloud.models.dto.song.*;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.MethodNotAllowedException;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SongService extends AbstractService {

    private static final long MAX_FILESIZE = 100*1024*1024;

    private final SongDAO songDAO;
    public SongService(SongDAO songDAO) {
        this.songDAO = songDAO;
    }


    public List<ResponseGetSongDTO> searchByGenre(String genre){
        List<Song> songs = this.songRepository.findAllByGenre(genre);
        if(songs.size() == 0) {
            throw new NotFoundException("There is no musical content for genre: " + genre + ".");
        }
        return songs.stream().map(song -> modelMapper.map(song, ResponseGetSongDTO.class)).collect(Collectors.toList());
    }

    public List<ResponseGetSongByUsernameDTO> searchByUploader(String username) {
        if(utility.userNameValidation(username)) {
            Optional<User> optionalUploader = this.userRepository.findUserByUsername(username);
            if (optionalUploader.isPresent()) {
                User user = optionalUploader.get();
                List<Song> songs = songRepository.findAllByUploader(user);
                if (songs.size() == 0) {
                    throw new NotFoundException("This user has not uploaded any songs!");
                }
                return songs.stream().map(song -> modelMapper.map(song, ResponseGetSongByUsernameDTO.class)).collect(Collectors.toList());
            } else {
                throw new NotFoundException("User: " + username + " doesnt exist!");
            }
        }
        else {
            throw new BadRequestException("Username field is not filled up correctly! " +
                    "Username can consist only letters, numbers and special symbos ``.`` , ``_``! " +
                    "Usernane must start with letter!");
        }
    }

    public List<ResponseGetSongDTO> searchLikedSongsByUser(String username) {
        if(utility.userNameValidation(username)) {
            Optional<User> optionalUser = this.userRepository.findUserByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                List<Song> songs = user.getLikedSongs();
                if (songs.size() == 0) {
                    throw new NotFoundException("This user has not liked any songs!");
                }
                return songs.stream().map(song -> modelMapper.map(song, ResponseGetSongDTO.class)).collect(Collectors.toList());
            } else {
                throw new NotFoundException("User: " + username + " doesnt exist!");
            }
        }
        else {
            throw new BadRequestException("Username field is not filled up correctly! " +
                    "Username can consist only letters, numbers and special symbos ``.`` , ``_``! " +
                    "Usernane must start with letter!");
        }
    }

    public ResponseGetSongDTO searchByTitle(String title) {
        if (titleValidation(title)) {
            Song song = songRepository.findSongByTitle(title);
            return modelMapper.map(song, ResponseGetSongDTO.class);
        }
       throw new BadRequestException("The title is invalid!");
    }

    public List<ResponseSongFilterDTO> filterSongs(RequestSongFilterDTO filterType) throws SQLException {

        String title = filterType.getTitle();
        if (title == null) {
            throw new BadRequestException("You can not search without a title!");
        }

        String filterBy = filterType.getFilterBy();
        if (filterBy == null) {
            filterBy = "likes";
        } else {
            filterBy = filterBy.toLowerCase().trim();
        }

        String orderBy = filterType.getOrderBy();
        if (orderBy == null) {
            orderBy = "asc";
        } else {
            orderBy = orderBy.toLowerCase().trim();
            if (!orderBy.equals("asc") && !orderBy.equals("desc")) {
                throw new BadRequestException("Invalid type of ordering!");
            }
        }

        Integer page = filterType.getPage();
        if (page == null || page == 0) {
            page = 1;
        }

        switch (filterBy) {
            case "likes":
            case "dislikes":
            case "date":
            case "listened":
            case "comments":
                return songDAO.filter(title, filterBy, orderBy, page, 6);
            default:
                throw new BadRequestException("Invalid type of filter!");
        }
    }

    public LikeDTO like(long sid, long uid) {
        Song song = findSongById(sid);
        User user = findUserById(uid);
        if (user.getLikedSongs().contains(song)) {
            user.getLikedSongs().remove(song);
            userRepository.save(user);
            return new LikeDTO("Your like was successfully removed!", song.getLikers().size());
        } else {
            user.getLikedSongs().add(song);
            userRepository.save(user);
            return new LikeDTO("Your like was successfully accepted!", song.getLikers().size());
        }
    }

    public DislikeDTO dislike(long sid, long uid) {
        Song song = findSongById(sid);
        User user = findUserById(uid);
        if (user.getDislikedSongs().contains(song)) {
            user.getDislikedSongs().remove(song);
            userRepository.save(user);
            return new DislikeDTO("Your dislike was successfully removed!", song.getDislikers().size());
        } else {
            user.getDislikedSongs().add(song);
            userRepository.save(user);
            return new DislikeDTO("Your dislike was successfully accepted!", song.getDislikers().size());
        }
    }

    public void isSongDisliked(long sid, long uid) {
        Song song = findSongById(sid);
        User currentUser = modelMapper.map(userRepository.findById(uid), User.class);
        if (song.getDislikers().contains(currentUser)){
            currentUser.getDislikedSongs().remove(song);
        }
    }
    public void isSongLiked(long sid, long uid) {
        Song song = findSongById(sid);
        User currentUser = modelMapper.map(userRepository.findById(uid), User.class);
        if (song.getLikers().contains(currentUser)){
            currentUser.getLikedSongs().remove(song);
        }
    }

    public ResponseSongDTO getSongWithUserById(long sid) {
        Song song = findSongById(sid);
        ResponseSongDTO dto = modelMapper.map(song, ResponseSongDTO.class);
        dto.setUploader(modelMapper.map(dto.getUploader(), UserWithoutPDTO.class));
        return dto;
    }

    public ResponseSongUploadDTO uploadSong(long uid,String title, String artist, String genre, String description, MultipartFile songFile) {

        User currentUser = findUserById(uid);
        String extension = FilenameUtils.getExtension(songFile.getOriginalFilename());
        String nameUrl = "uploadedSongs" + File.separator + System.nanoTime() + "." + extension;
        if (!extension.equals("mp3")) {
            throw new BadRequestException("You are trying to upload an invalid file. You have to select an mp3 file.");
        }
        if (songFile.getSize() > MAX_FILESIZE) {
            throw new BadRequestException("The size of the song is too large.");
        }
        Song uploadedSong = new Song();

        if (songUploadValidation(title, artist, genre)) {
            File f = new File(nameUrl);
            if (!f.exists()) {
                try {
                    Files.copy(songFile.getInputStream(), f.toPath());
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage(), e);
                }
            } else {
                throw new BadRequestException("The file already exists!");
            }

            uploadedSong.setUploader(currentUser);
            uploadedSong.setTitle(title);
            uploadedSong.setArtist(artist);
            uploadedSong.setGenre(genre);
            uploadedSong.setCreatedAt(LocalDateTime.now());
            uploadedSong.setListened(0);
            uploadedSong.setUrl(nameUrl);
            if (description != null) {
                uploadedSong.setDescription(description);
            }
        }
        songRepository.save(uploadedSong);
        return modelMapper.map(uploadedSong, ResponseSongUploadDTO.class);
    }


    public ResponseSongDeleteDTO deleteSong(long uid, long sid) {
        Song songToDelete = findSongById(sid);
        User currentUser = findUserById(uid);
        songRepository.delete(songToDelete);
        userRepository.save(currentUser);
        ResponseSongDeleteDTO dto = modelMapper.map(songToDelete, ResponseSongDeleteDTO.class);
        dto.setMessage("Song deleted successfully!");
        dto.setDeletedSongId(sid);
        return dto;
    }


    public ResponseGetSongInfoDTO editSong(RequestSongEditDTO dto, long userId, long songId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User does not exist!"));
        Song song = songRepository.findById(songId).orElseThrow(() -> new BadRequestException("Song does not exist!"));

        if(songEditValidation(dto)) {
            if (user.getSongs().contains(song)) {
                setSongEdit(dto, song);
                songRepository.save(song);
                return modelMapper.map(song, ResponseGetSongInfoDTO.class);
            } else {
                throw new MethodNotAllowedException("The song that you are trying to edit was not uploaded by you!");
            }
        } else {
            throw new BadRequestException("Invalid input!");
        }
    }

    private void setSongEdit (RequestSongEditDTO dto, Song song) {
        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());
        song.setGenre(dto.getGenre());
        song.setDescription(dto.getDescription());
    }



    protected boolean songUploadValidation(String title, String artist, String genre) {
        if (titleValidation(title) &&
                artistValidation(artist) &&
                genreValidation(genre)){
            return true;
        } else {
            throw new BadRequestException("Invalid song data!");
        }
    }

    protected boolean songEditValidation(RequestSongEditDTO song) {
        if (titleValidation(song.getTitle()) &&
                artistValidation(song.getArtist()) &&
                genreValidation(song.getGenre())){
            return true;
        } else {
            throw new BadRequestException("Invalid song data!");
        }
    }

    protected boolean titleValidation(String title){
        String regex = "^[a-zA-Z0-9_ !$%^&*-`)(]{2,40}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(title);
        boolean isMatching = m.matches();
        if (!title.isBlank() && isMatching) {
            return true;
        } else {
            throw new BadRequestException("The title is invalid!");
        }
    }

    protected boolean genreValidation(String genre){
        String regex = "^[A-Za-z\\s-]{2,29}$"; // it allows to use upper/lower case spaces and -
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(genre);
        boolean isMatching = m.matches();
        if (!genre.isBlank() && isMatching) {
            return true;
        } else {
            throw new BadRequestException("The genre is invalid!");
        }
    }

    protected boolean artistValidation(String artist) {
        String regex = "^[a-zA-Z0-9_ !$%^&*)(]{2,40}$";// artist may contains only characters between A-Z/a-z, digits 0-9 and special symbols as space,!$%^&*() !
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(artist);
        boolean isMatching = m.matches();
        if (!artist.isBlank() && isMatching) {
            return true;
        } else {
            throw new BadRequestException("The artist is invalid! It may contains uppercase and lowercase letters, numbers and special characters as !$%^&*()!");
        }
    }
}
