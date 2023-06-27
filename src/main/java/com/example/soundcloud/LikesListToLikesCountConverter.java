package com.example.soundcloud;

import com.example.soundcloud.models.entities.User;
import org.modelmapper.AbstractConverter;


import java.util.List;

public class LikesListToLikesCountConverter extends AbstractConverter<List<User>, Integer> {
    @Override
    protected Integer convert(List<User> likers) {
        if(likers != null) {
            return likers.size();
        } else {
            return 0;
        }
    }


}
