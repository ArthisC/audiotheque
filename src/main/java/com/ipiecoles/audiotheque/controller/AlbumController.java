package com.ipiecoles.audiotheque.controller;

import com.ipiecoles.audiotheque.model.Album;
import com.ipiecoles.audiotheque.model.Artist;
import com.ipiecoles.audiotheque.repository.AlbumRepository;
import com.ipiecoles.audiotheque.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/albums")
public class AlbumController {
    @Autowired
    private AlbumRepository albumRepository;

    //7 - Ajout d'un album
    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.CREATED)
    public Album createNewAlbum(@RequestBody() Album album){
        try {
            return albumRepository.save(album);
        }
        catch(Exception e){
            throw new IllegalArgumentException("Problème lors de la sauvegarde de l'artiste !");
        }
    }

    //8 - Suppression d'un album
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable(value = "id") Long id){
        Optional<Album> album = albumRepository.findById(id);
        albumRepository.deleteById(id);
    }

}
