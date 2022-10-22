package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.user.EditDTO;
import com.example.soundcloud.models.dto.user.RegisterDTO;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.repositories.PlaylistRepository;
import com.example.soundcloud.models.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Utility {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    protected PlaylistRepository playlistRepository;

    protected boolean validateRegistration(RegisterDTO u) {
        if (userExist(u.getEmail(), u.getUsername()) &&
                userPasswordValidation(u.getPassword(), u.getConfirmPassword()) &&
                firstNValidation(u.getFirstName()) &&
                lastNValidation(u.getLastName()) &&
                userNameValidation(u.getUsername()) &&
                birthDayValidation(u.getDateOfBirthday()) &&
                genderValidation(u.getGender()) &&
                emailValidation(u.getEmail()) &&
                addressValidation(u.getAddress()) &&
                cityValidation(u.getCity()) &&
                countryValidation(u.getCountry())) {
            return true;
        } else {
            throw new BadRequestException("Invalid input!");
        }
    }

    protected boolean editProfileValidation(EditDTO u, long id) {
        if (usernameExist(u, id) &&
                emailExist(u, id) &&
                firstNValidation(u.getFirstName()) &&
                lastNValidation(u.getLastName()) &&
                userNameValidation(u.getUsername()) &&
                birthDayValidation(u.getDateOfBirthday()) &&
                genderValidation(u.getGender()) &&
                emailValidation(u.getEmail()) &&
                addressValidation(u.getAddress()) &&
                cityValidation(u.getCity()) &&
                countryValidation(u.getCountry())) {
            return true;
        } else {
            throw new BadRequestException("Invalid input!");
        }
    }

    protected boolean PlaylistNameValidation(String tittle) {
        if (validationWithRegex("^(\\s)*[A-Za-z]+((\\s)?((\\'|\\-|\\.)?([A-Za-z])+))*(\\s)*$", tittle)) {
            return true;
        } else {
            throw new BadRequestException("Title is invalid!");
        }
    }

    protected boolean isPlaylistExist(String name){
        if (playlistRepository.findAllByName(name).size() > 0){
            throw new BadRequestException("Playlist with this name already exist!");
        }else {
            return false;
        }
    }

    private boolean validationWithRegex(String regex, String object) {
        String regex1 = regex;
        Pattern pattern = Pattern.compile(regex1);
        Matcher matcher = pattern.matcher(object);
        return matcher.matches();
    }


    protected boolean firstNValidation(String firstN) {
        if (firstN == null) {
            return true;
        }
        if (validationWithRegex("^[A-Za-z]{2,29}$", firstN)) {
            return true;
        } else {
            throw new BadRequestException("First name is invalid!");
        }
    }

    protected boolean lastNValidation(String lastN) {
        if (lastN == null) {
            return true;
        }
        if (validationWithRegex("^[A-Za-z]{2,29}$", lastN)) {
            return true;
        } else {
            throw new BadRequestException("Last name is invalid!");
        }
    }

    protected boolean birthDayValidation(LocalDate localDate) {
        if (validationWithRegex("([0-9]{4})-([0-9]{2})-([0-9]{2})", localDate.toString()) && localDate.compareTo(LocalDate.now()) < 0) {
            return true;
        } else {
            throw new BadRequestException("Invalid birthday! Check it out!");
        }
    }

    protected boolean genderValidation(String gender) {
        String[] genders = {"Female", "Male", "Other"};
        if (gender.equalsIgnoreCase(genders[0]) || gender.equalsIgnoreCase(genders[1]) || gender.equalsIgnoreCase(genders[2])) {
            return true;
        } else {
            throw new BadRequestException("Invalid gender. Please choose again!");
        }
    }

    protected boolean userNameValidation(String username) {
        char[] numbers = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        boolean hasNumbers = false;
        for (int i = 0; i < numbers.length; i++) {
            if (username.charAt(0) == numbers[i]) {
                hasNumbers = true;
            }
        }
        if (validationWithRegex("^[a-zA-Z0-9_.]{5,30}$", username) && !username.startsWith(".") &&
                !username.startsWith("_") && !hasNumbers) {
            return true;
        } else {
            throw new BadRequestException("Username field is not filled up correctly! " +
                    "Username can consist only letters, numbers and special symbos ``.`` , ``_``! " +
                    "Usernane must start with letter!");
        }
    }

    protected boolean userPasswordValidation(String ps1, String ps2) {
        if (validationWithRegex("^(?=\\P{Ll}*\\p{Ll})(?=\\P{Lu}*\\p{Lu})(?=\\P{N}*\\p{N})(?=[\\p{L}\\p{N}]*[^\\p{L}\\p{N}])[\\s\\S]{8,50}$", ps1)) {
            if (ps1.equals(ps2)) {
                return true;
            } else {
                throw new BadRequestException("Your passwords does not matching!");
            }
        } else {
            throw new BadRequestException("Your password is not correct! Your password must have at least one lowercase letter" +
                    " or at least one uppercase letter! " +
                    "Also password must have at least one number character and one special symbol." +
                    " Characters at all must be between 8 and 50!");
        }
    }

    protected boolean userExist(String email, String username) {
        if (userRepository.findAllByEmail(email).size() > 0 || userRepository.findAllByUsername(username).size() > 0) {
            throw new BadRequestException("User already exist!");
        } else {
            return true;
        }
    }

    protected boolean userExist(String username) {
        if (userRepository.findAllByUsername(username).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean usernameExist(EditDTO dto, long id) {
        if (userRepository.findById(id).get().getUsername().equals(dto.getUsername())) {
            return true;
        }
        if (userRepository.findAllByUsername(dto.getUsername()).size() > 0) {
            throw new BadRequestException("This username already exist!");
        } else {
            return true;
        }
    }

    protected boolean emailExist(EditDTO dto, long id) {
        if (userRepository.findById(id).get().getEmail().equals(dto.getEmail())) {
            return true;
        }
        if (userRepository.findAllByEmail(dto.getEmail()).size() > 0) {
            throw new BadRequestException("This email already exist!");
        } else {
            return true;
        }
    }

    protected boolean emailValidation(String email) {
        if (validationWithRegex("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", email)
                && email.startsWith("_") && email.startsWith("-")) {
            return true;
        } else {
            throw new BadRequestException("Email is invalid! Check it out!");
        }
    }

    protected boolean addressValidation(String address) {
        if (address == null) {
            return true;
        }
        if (validationWithRegex("^[#.0-9a-zA-Z\\s,-]+$", address)) {
            return true;
        } else {
            throw new BadRequestException("Invalid address!");
        }
    }

    protected boolean countryValidation(String country) {
        if (country == null) {
            return true;
        }
        if (validationWithRegex("^[a-zA-Z][a-zA-Z\\s-]+[a-zA-Z]$", country)) {
            return true;
        } else {
            throw new BadRequestException("Invalid country");
        }
    }

    protected boolean cityValidation(String city) {
        if (city == null) {
            return true;
        }
        if (validationWithRegex("^[a-zA-Z][a-zA-Z\\s-]+[a-zA-Z]$", city)) {
            return true;
        } else {
            throw new BadRequestException("Invalid city");
        }
    }

    protected boolean profileImageValidation(MultipartFile file) {
        if (file.getSize() > (5 * 1024 * 1024)) { //5MB
            throw new BadRequestException("Image can not be more then 5MB!");
        } else {
            if (!file.getContentType().startsWith("image/")) {
                throw new BadRequestException("Only images are allowed here!");
            } else {
                return true;
            }

        }
    }
}
