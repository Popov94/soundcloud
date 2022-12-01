package com.example.soundcloud.models.dto.user;

import lombok.Data;

@Data
public class ChangeForgottenPWDTO {
    private String newPassword;
    private String confirmNewPassword;
}
