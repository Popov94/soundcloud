package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.UserFullDTO;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class UserController extends GlobalUserController {

    @PostMapping("/register")
    public UserFullDTO register(@RequestBody User u) {
        if (validateRegistration(u)) {
            u.setCreatedAt(LocalDateTime.now());
            userRepository.save(u);
            User user = u;
            UserFullDTO u1 = modelMapper.map(user, UserFullDTO.class);
            return u1;
        }
        return null;
    }

    @GetMapping("/users")
    public List<UserFullDTO> getAll() {
        List<User> u1 = userRepository.findAll();
        List<UserFullDTO> u2 = u1.stream().map(user -> modelMapper.map(user, UserFullDTO.class)).collect(Collectors.toList());
        return u2;
    }

    @GetMapping("/users/{id}")
    public UserFullDTO getUserById(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            UserFullDTO dto = modelMapper.map(user, UserFullDTO.class);
            return dto;
        } else {
            throw new BadRequestException("Wrong id dude..Tryy it again");
        }
    }

    private boolean validateRegistration(User u) {
        if (userExist(u.getEmail(), u.getUsername()) &&
                userPasswordValidation(u.getPassword(), u.getConfirmPassword())
                && firstNValidation(u.getFirstName()) &&
                lastNValidation(u.getLastName()) &&
                userNameValidation(u.getUsername())
                && birthDayValidation(u.getDateOfBirthday()) &&
                genderValidation(u.getGender()) && emailValidation(u.getEmail())
                && addressValidation(u.getAddress()) &&
                cityValidation(u.getCity()) &&
                countryValidation(u.getCountry())) {
            return true;
        } else {
            throw new BadRequestException("Invalid input!");
        }
    }


    private boolean firstNValidation(String firstN) {
        if (firstN == null) {
            return true;
        }
        String regex = "^[A-Za-z]{2,29}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(firstN);
        boolean isMatching = m.matches();
        if (!firstN.isBlank() && isMatching) {
            return true;
        } else {
            throw new BadRequestException("First name is invalid!");
        }
    }

    private boolean lastNValidation(String lastN) {
        if (lastN == null) {
            return true;
        }
        String regex = "^[A-Za-z]{2,29}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(lastN);
        boolean isMatching = m.matches();
        if (!lastN.isBlank() && isMatching) {
            return true;
        } else {
            throw new BadRequestException("Last name is invalid!");
        }
    }

    private boolean birthDayValidation(LocalDate localDate) {
        Pattern p = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
        Matcher m = p.matcher(localDate.toString());
        boolean isMatching = m.matches();
        if (isMatching && localDate.compareTo(LocalDate.now()) < 0) {
            return true;
        } else {
            throw new BadRequestException("Invalid birthday! Check it out!");
        }
    }

    private boolean genderValidation(String gender) {
        String[] genders = {"Female", "Male", "Other"};
        if (gender.equalsIgnoreCase(genders[0]) || gender.equalsIgnoreCase(genders[1]) || gender.equalsIgnoreCase(genders[2])) {
            return true;
        } else {
            throw new BadRequestException("Invalid gender. Please choose again!");
        }
    }

    private boolean userNameValidation(String username) {
        String regex = "^[a-zA-Z0-9_.]{5,30}$";
        char[] numbers = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        boolean hasNumbers = false;
        for (int i = 0; i < numbers.length; i++) {
            if (username.charAt(0) == numbers[i]) {
                hasNumbers = true;
            }
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);
        boolean isMatching = m.matches();
        if (username != null && !username.isBlank() && isMatching && !username.startsWith(".") &&
                !username.startsWith("_") && !hasNumbers) {
            return true;
        } else {
            throw new BadRequestException("Username field is not filled up correctly! " +
                    "Username can consist only letters, numbers and special symbos ``.`` , ``_``! " +
                    "Usernane must start with letter!");
        }
    }

    private boolean userPasswordValidation(String ps1, String ps2) {
        String regex = "^(?=\\P{Ll}*\\p{Ll})(?=\\P{Lu}*\\p{Lu})(?=\\P{N}*\\p{N})(?=[\\p{L}\\p{N}]*[^\\p{L}\\p{N}])[\\s\\S]{8,50}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(ps1);
        boolean isMatching = m.matches();
        if (ps1 != null && ps2 != null && isMatching) {
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

    private boolean userExist(String email, String username) {
        if (userRepository.findAllByEmail(email).size() > 0 && userRepository.findAllByUsername(username).size() > 0) {
            throw new BadRequestException("User already exist!");
        } else {
            return true;
        }
    }

    private boolean emailValidation(String email) {
        Pattern p = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        Matcher m = p.matcher(email);
        boolean isMatching = m.matches();
        if (isMatching) {
            return true;
        } else {
            throw new BadRequestException("Email is invalid! Check it out!");
        }
    }

    private boolean addressValidation(String address) {
        if (address == null) {
            return true;
        }
        Pattern p = Pattern.compile("^[#.0-9a-zA-Z\\s,-]+$");
        Matcher m = p.matcher(address);
        boolean isMatching = m.matches();
        if (isMatching) {
            return true;
        } else {
            throw new BadRequestException("Invalid address!");
        }
    }

    private boolean countryValidation(String country) {
        if (country == null) {
            return true;
        }
        Pattern pattern = Pattern.compile("String regex = \"^[A-Za-z]{2,57}$\";");
        Matcher matcher = pattern.matcher(country);
        boolean isMatching = matcher.matches();
        if (isMatching) {
            return true;
        } else {
            throw new BadRequestException("Invalid country");
        }
    }

    private boolean cityValidation(String city) {
        if (city == null) {
            return true;
        }
        Pattern pattern = Pattern.compile("String regex = \"^[A-Za-z]{2,80}$\";");
        Matcher matcher = pattern.matcher(city);
        boolean isMatching = matcher.matches();
        if (isMatching) {
            return true;
        } else {
            throw new BadRequestException("Invalid city");
        }
    }
}
