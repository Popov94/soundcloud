package com.example.soundcloud.models.dto.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Data
public class UserInfoDTO {
    @Column
    private String username;

}
