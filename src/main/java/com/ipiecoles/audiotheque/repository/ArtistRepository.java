package com.ipiecoles.audiotheque.repository;

import com.ipiecoles.audiotheque.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}
