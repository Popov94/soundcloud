package com.example.soundcloud.models.repositories;

import com.example.soundcloud.models.entities.listeners.Listened;
import com.example.soundcloud.models.entities.listeners.ListenedKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListenedRepository extends JpaRepository<Listened, ListenedKey> {

}
