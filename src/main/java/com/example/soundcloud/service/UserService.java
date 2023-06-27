package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.song.ResponseSongDTO;
import com.example.soundcloud.models.dto.song.SongWithoutUserDTO;
import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.MethodNotAllowedException;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private boolean ckeckLog = true;
    private AtomicInteger countFailedLo = new AtomicInteger(0);

    @Transactional
    public UserWithoutPDTO register(RegisterDTO userDTO, String siteURL) {
        if (utility.validateRegistration(userDTO)) {
            User user = modelMapper.map(userDTO, User.class);
            user.setCreatedAt(LocalDateTime.now());
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            String verificationCode = RandomString.make(64);
            user.setVerificationCode(verificationCode);
            sendVerificationEmail(user, siteURL);
            userRepository.save(user);
            return modelMapper.map(user, UserWithoutPDTO.class);
        } else {
            throw new BadRequestException("Problem with data validation");
        }
    }

    private void sendVerificationEmail(User user, String siteURL) {
        new Thread(() -> {
            try {
                String toAddress = user.getEmail();
                String fromAddress = "soundcloudtests14@gmail.com";
                String senderName = "Sound Cloud";
                String subject = "Please verify your registration";
                String content = "Dear [[name]],<br>"
                        + "Please click the link below to verify your registration:<br>"
                        + "<h3><a href=\"[[URL]]\" target=\"_self\">CLICK HERE TO VERIFY</a></h3>"
                        + "Thank you,<br>"
                        + "Sound Cloud.";
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom(fromAddress, senderName);
                helper.setTo(toAddress);
                helper.setSubject(subject);
                content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
                helper.setText(content, true);
                String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
                content = content.replace("[[URL]]", verifyURL);
                helper.setText(content, true);
                mailSender.send(message);
            }  catch (MessagingException e) {
                throw new BadRequestException(e.getMessage(), e);
            } catch (UnsupportedEncodingException e) {
                throw new BadRequestException(e.getMessage(), e);
            }
        }).start();
    }

    @Scheduled(cron = "0 0 12 15 * *")
    @Async
    public void remindingEmailToConfirm() throws MessagingException, UnsupportedEncodingException {
        List<User> users = userRepository.findAllNonVerifiedUser();
        for (int i = 0; i < users.size(); i++) {
            String toAddress = users.get(i).getEmail();
            String fromAddress = "soundcloudtests14@gmail.com";
            String senderName = "Sound Cloud";
            String subject = "Remind you to verify your registration";
            String content = "Dear [[name]], <br><br> \n" +
                    "          Thank you for joining Sound Cloud! We want to" +
                    " <b>remind you that you need to verify your account to confirm your identity.</b> \n" +
                    "\n" + "<br><br> You will be able to use our services if you confirm your registration." +
                    " In addition, your account would be deleted soon, after this email if you dont verify it</b>," +
                    " Sound Cloud Team. \n";
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            content = content.replace("[[name]]", users.get(i).getFirstName() + " " + users.get(i).getLastName());
            helper.setText(content, true);
            mailSender.send(message);
        }
    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Async
    public void deleteNonVerifiedUsers() {
        List<User> users = userRepository.findAllNonVerifiedUserForDelete();
        for (int i = 0; i < users.size(); i++) {
            userRepository.delete(users.get(i));
        }
        System.out.println(users);
    }

    public UserWithoutPDTO login(LoginDTO dto) {
        if (!utility.userExist(dto.getUsername())) {
            throw new UnauthorizedException("Wrong username or password!");
        }
        String password = dto.getPassword();
        String username = dto.getUsername();
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return modelMapper.map(user, UserWithoutPDTO.class);
            } else {
                throw new UnauthorizedException("Wrong username or password!");
            }
        } else {
            throw new UnauthorizedException("Wrong username or password!");
        }
    }

    public UserWithoutPDTO getUserById(long id) {
        User user = findUserById(id);
        UserWithoutPDTO dto = modelMapper.map(user, UserWithoutPDTO.class);
        return dto;
    }

    @Transactional
    public UserWithoutPDTO editProfile(EditDTO dto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new MethodNotAllowedException("User initials are wrong!"));
        if (utility.editProfileValidation(dto, userId)) {
            if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                setEdit(dto, user);
                userRepository.save(user);
                return modelMapper.map(user, UserWithoutPDTO.class);
            } else {
                throw new MethodNotAllowedException("Password is wrong");
            }
        } else {
            throw new BadRequestException("Invalid input!");
        }
    }

    private void setEdit(EditDTO dto, User user) {
        user.setEmail(dto.getEmail());
        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setDateOfBirthday(dto.getDateOfBirthday());
        user.setGender(dto.getGender());
        user.setUsername(dto.getUsername());
    }

    public String logOut(long userId) {
        User user = findUserById(userId);
        if (user.getGender().equals("Male")) {
            return "Mr. " + user.getLastName() + " you have been logged out successfully!";
        } else {
            return "Mrs. " + user.getLastName() + " you have been logged out successfully!";
        }
    }

    public String deleteUser(long userId, DeleteDTO dto) {
        User user = findUserById(userId);
        if (bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            userRepository.deleteById(user.getId());
            return "User: " + user.getUsername() + " has been successfully deleted";
        } else {
            throw new BadRequestException("Password does not match!");
        }
    }

    public String changePW(ChangePDTO dto, long userId) {
        User user = findUserById(userId);
        if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            if (dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
                userRepository.save(user);
                return "Your password have been changed successfully!";
            } else {
                throw new BadRequestException("New passwords does not match!");
            }
        } else {
            throw new BadRequestException("Password is incorrect!");
        }
    }

    public String uploadProfileImage(MultipartFile file, long userId) {
        if (utility.profileImageValidation(file)) {
            try {
                User user = findUserById(userId);
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                String name = "uploads" + File.separator + System.nanoTime() + userId + "." + extension;
                File f = new File(name);
                if (!f.exists()) {
                    Files.copy(file.getInputStream(), f.toPath());
                } else {
                    throw new BadRequestException("The file already exist. This should never happen. Call the service!");
                }
                if (user.getProfileImageUrl() != null) {
                    File oldFile = new File(user.getProfileImageUrl());
                    oldFile.delete();
                }
                user.setProfileImageUrl(name);
                userRepository.save(user);
                return name;
            } catch (IOException exception) {
                throw new BadRequestException(exception.getMessage(), exception);
            }
        } else {
            throw new BadRequestException("Only images can be uploaded, and they must be under 5MB!");
        }
    }

    public String deleteProfileImage(long userId) {
        User user = findUserById(userId);
        File profileImage = new File(user.getProfileImageUrl());
        profileImage.delete();
        user.setProfileImageUrl(null);
        userRepository.save(user);
        return "Your profile picture has been removed!";
    }

    public UserWithoutPWithSongsDTO getUserSongsById(long id) {
        User user = findUserById(id);
        UserWithoutPWithSongsDTO dto = modelMapper.map(user, UserWithoutPWithSongsDTO.class);
        dto.setSongs(user.getSongs().stream().map(song -> modelMapper.map(song, SongWithoutUserDTO.class)).collect(Collectors.toList()));
        return dto;
    }

    public String verifyAccount(String code) {
        Optional<User> optionalUser = userRepository.findUserByVerificationCode(code);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return "You have been verified successfully";
        } else {
            throw new BadRequestException("You are already verified!");
        }
    }

    public List<UserWithoutPWithSongsDTO> getUserByName(String userName) {
        List<User> users = userRepository.findByKeyword(userName).stream().collect(Collectors.toList());
        List<UserWithoutPWithSongsDTO> dto = users.
                stream().
                map(user -> modelMapper.map(user, UserWithoutPWithSongsDTO.class)).
                collect(Collectors.toList());
        return dto;
    }

    public boolean isUserVerified(long userId) {
        User user = findUserById(userId);
        if (user.isVerified()) {
            return true;
        } else {
            return false;
        }
    }

    public String followUser(long followerId, long followedId) {
        User followedUser = findUserById(followedId);
        User follower = findUserById(followerId);
        if (followedId == followerId) {
            throw new BadRequestException("You can not follow yourself!");
        }
        boolean isHere = false;
        for (Long l : userRepository.getFollowingUsersIds(followerId)) {
            if (l.equals(followedId)) {
                isHere = true;
                break;
            }
        }
        if (isHere) {
            throw new BadRequestException("You already followed this user!");
        }
        follower.getFollowing().add(followedUser);
        userRepository.save(follower);
        return "You followed successfully " + followedUser.getFirstName() + " " + followedUser.getLastName();
    }

    public String unfollowUser(long followerId, long followedId) {
        User followedUser = findUserById(followedId);
        User follower = findUserById(followerId);
        if (followedId == followerId) {
            throw new BadRequestException("You can not unfollow yourself!");
        }
        boolean isHere = false;
        for (Long l : userRepository.getFollowingUsersIds(followerId)) {
            if (l.equals(followedId)) {
                isHere = true;
                break;
            }
        }
        if (!isHere) {
            throw new BadRequestException("You can not unfollow user that u are not following");
        }
        follower.getFollowing().remove(followedUser);
        userRepository.save(follower);
        return "You have unfollowed " + followedUser.getFirstName() + " " + followedUser.getLastName();
    }

    public Page<UserWithoutPDTO> findAllUsersWithPagination(int offset, int pageSize) {
        Page<UserWithoutPDTO> users = userRepository.findAll(PageRequest.of(offset, pageSize)).
                map(user -> modelMapper.map(user, UserWithoutPDTO.class));
        return users;
    }

    public Page<UserWithoutPDTO> findAllUsersWithPaginationAndSorting(int offset, int pageSize, String sortedBy) {
        Page<UserWithoutPDTO> users = userRepository.findAll(PageRequest.of(offset, pageSize).
                        withSort(Sort.by(sortedBy))).
                        map(user -> modelMapper.map(user, UserWithoutPDTO.class));
        return users;

    }

    public Page<UserWithoutPDTO> findAllUsersWithPaginationAndSortingDesc(int offset, int pageSize, String sortedBy) {
        Page<UserWithoutPDTO> users = userRepository.findAll(PageRequest.of(offset, pageSize).
                        withSort(Sort.by(sortedBy).descending())).
                        map(user -> modelMapper.map(user, UserWithoutPDTO.class));
        return users;
    }

    public HashMap<String, List<ResponseSongDTO>> homePageForLogged(long userId) {
        User user = findUserById(userId);
        HashMap<String, List<ResponseSongDTO>> suitForUser = new HashMap<>();
        suitForUser.put("Five of most played songs", new ArrayList<>());
        suitForUser.put("Last five liked songs", new ArrayList<>());
        suitForUser.put("Suitable for user", new ArrayList<>());
        List<ResponseSongDTO> mostListened = songRepository.findFiveMostListenedForUser(userId)
                .stream()
                .map(song -> modelMapper.map(song, ResponseSongDTO.class))
                .collect(Collectors.toList());
        suitForUser.put("Five of most played songs", mostListened);
        List<ResponseSongDTO> mostLiked = songRepository.findFiveMostLikedForUser(userId)
                .stream()
                .map(song -> modelMapper.map(song, ResponseSongDTO.class))
                .collect(Collectors.toList());
        suitForUser.put("Last five liked songs", mostLiked);
        String mostListenedGenreForUser = songRepository.mostListenedGenreForUser(userId);
        List<ResponseSongDTO> suitableForUser = songRepository.findFiveSuitableForUser(mostListenedGenreForUser)
                .stream()
                .map(song -> modelMapper.map(song, ResponseSongDTO.class))
                .collect(Collectors.toList());
        suitForUser.put("Suitable for user", suitableForUser);

        return suitForUser;
    }

    public HashMap<String, List<ResponseSongDTO>> homePageForNonLoged() {
        HashMap<String, List<ResponseSongDTO>> responseForNonLoggedUsers = new HashMap<>();
        responseForNonLoggedUsers.put("Most listened songs at all", new ArrayList<>());
        List<ResponseSongDTO> mostListened = songRepository.FindFiveMostListenedAtAll()
                .stream()
                .map(song -> modelMapper.map(song, ResponseSongDTO.class))
                .collect(Collectors.toList());
        responseForNonLoggedUsers.put("Most listened songs at all", mostListened);
        responseForNonLoggedUsers.put("Most liked songs at all", new ArrayList<>());
        List<ResponseSongDTO> mostLiked = songRepository.FindFiveMostLikedAtAll()
                .stream()
                .map(song -> modelMapper.map(song, ResponseSongDTO.class))
                .collect(Collectors.toList());
        responseForNonLoggedUsers.put("Most liked songs at all", mostLiked);
        responseForNonLoggedUsers.put("Most commented songs at all", new ArrayList<>());
        List<ResponseSongDTO> mostCommented = songRepository.FindFiveMostCommentedAtAll()
                .stream()
                .map(song -> modelMapper.map(song, ResponseSongDTO.class))
                .collect(Collectors.toList());
        responseForNonLoggedUsers.put("Most commented songs at all", mostCommented);
        return responseForNonLoggedUsers;
    }

    public boolean checkLog(LoginDTO dto) {
        Optional<User> optionalUser = userRepository.findUserByUsername(dto.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
                return true;
            }
        }
        return false;
    }

    public String forgotPassword(UserInfoDTO dto, String URL) {
        System.out.println(dto.getEmail());
        Optional<User> user = userRepository.findUserByEmail(dto.getEmail());
        if (user.isPresent()){
            User user1 = user.get();
            String code = RandomString.make(64);
            user1.setVerificationCode(code);
            userRepository.save(user1);
            sendEmailForNewPassword(user1,URL);
            return "We've sent instructions how to change your password at your email.";
        } throw  new NotFoundException("User does not exist!");
    }



    private void sendEmailForNewPassword(User user, String siteURL) {
        new Thread(() -> {
            try {
                String toAddress = user.getEmail();
                String fromAddress = "soundcloudtests14@gmail.com";
                String senderName = "Sound Cloud";
                String subject = "Request to change forgotten password";
                String content = "Dear [[name]],<br>"
                        + "Please click the link below to change your password:<br>"
                        + "<h3><a href=\"[[URL]]\" target=\"_self\">CLICK HERE</a></h3>"
                        + "If you didn’t request a password change," +
                        " you can ignore this message and continue to use your current password.,<br>"
                        + "Sound Cloud.";
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom(fromAddress, senderName);
                helper.setTo(toAddress);
                helper.setSubject(subject);
                content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
                helper.setText(content, true);
                String verifyURL = siteURL + "/change_password?code=" + user.getVerificationCode();
                content = content.replace("[[URL]]", verifyURL);
                helper.setText(content, true);
                mailSender.send(message);
            }  catch (MessagingException e) {
                throw new BadRequestException(e.getMessage(), e);
            } catch (UnsupportedEncodingException e) {
                throw new BadRequestException(e.getMessage(), e);
            }
        }).start();
    }

    public String ChangeForgottenPassword(ChangeForgottenPWDTO dto, String code) {
        Optional<User> user = userRepository.findUserByVerificationCode(code);
        if (user.isPresent()){
            User user1 = user.get();
            if (utility.userPasswordValidation(dto.getNewPassword(),dto.getConfirmNewPassword())){
                user1.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
                user1.setVerificationCode(null);
                userRepository.save(user1);
                return "You have changed your password successfully";
            }
        } throw new BadRequestException("Something went wrong. If you see this please call the service");
    }
}
