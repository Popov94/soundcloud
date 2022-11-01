package com.example.soundcloud.controllers;

import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class FileController extends GlobalController{
    @Autowired
    private SongService service;

    @GetMapping("/images/{fileName}")
    public void downloadProfileImage(@PathVariable String fileName, HttpServletResponse resp, HttpServletRequest req){
        File file = new File("uploads" + File.separator + fileName);
        if (!file.exists()){
            throw new NotFoundException("File does not exist");
        }else {
            try {
                resp.setContentType(Files.probeContentType(file.toPath()));
                Files.copy(file.toPath(), resp.getOutputStream());
            } catch (IOException e) {
                throw new BadRequestException("Problem with output stream. Call me afap!");
            }
        }
    }

    @GetMapping("songs/download/{songName}")
    public ResponseEntity<ByteArrayResource> downloadSong(@PathVariable String songName){
        String name = "uploadedSongs" + File.separator + songName;
        byte[] data = service.downloadSong(name);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + songName + "\"")
                .body(resource);
    }
}
