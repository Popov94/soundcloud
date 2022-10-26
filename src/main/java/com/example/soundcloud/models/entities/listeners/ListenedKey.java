package com.example.soundcloud.models.entities.listeners;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class ListenedKey implements Serializable {
    @Column(name = "user_id")
    Long userId;
    @Column(name = "song_id")
    Long songId;
}
