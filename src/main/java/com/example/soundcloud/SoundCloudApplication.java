package com.example.soundcloud;

import com.example.soundcloud.models.dto.song.ResponseGetSongInfoDTO;
import com.example.soundcloud.models.entities.Song;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.Map;
import java.util.Properties;
import java.util.Set;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@EnableAsync
public class SoundCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoundCloudApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ModelMapper songModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(Song.class, ResponseGetSongInfoDTO.class)
                .addMappings(new PropertyMap<Song, ResponseGetSongInfoDTO>() {
                    @Override
                    protected void configure() {
                        using(new LikesListToLikesCountConverter()).map(source.getLikers(), destination.getLikes());
                        using(new LikesListToLikesCountConverter()).map(source.getDislikers(), destination.getDislikes());
                        using(new LikesListToLikesCountConverter()).map(source.getCommenters(), destination.getComments());
                    }
                });
        return modelMapper;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(150 * 1024 * 1024);
        return multipartResolver;
    }

    @Bean
    public StandardServletMultipartResolver multipartResolver1() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("soundcloudtests14@gmail.com");
        mailSender.setPassword("wvquwixcgqfwxemf");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
