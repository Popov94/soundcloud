package com.example.soundcloud.models.dto.user;

import lombok.Data;

@Data
public class ChangePDTO {

    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
