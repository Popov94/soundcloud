package com.example.soundcloud.models.dto.user;

import com.example.soundcloud.models.dto.comment.CommentWithoutOwner;
import lombok.Data;

import java.util.List;

@Data
public class UserWithoutPWithCommentDTO extends UserWithoutPDTO{

    private List<CommentWithoutOwner> comments;

}
