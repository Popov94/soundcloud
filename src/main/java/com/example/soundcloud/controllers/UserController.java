package com.example.soundcloud.controllers;

import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public User register(@RequestBody User u) throws SQLException{
        if(exist(u)) {
            userRepository.save(u);
        }else {
            System.out.println("Syshtestvuva");
        }
        return u;
    }

    public boolean exist(User u) throws SQLException {

        Connection c = DBManager.getInstance().getConnection();
        PreparedStatement ps = c.prepareStatement("SELECT email FROM users where email=(?)");
        ps.setString(1,u.getEmail());
        ResultSet rs = ps.executeQuery();
        boolean exist = true;
        exist = rs.next();
        return exist;

    }



}
