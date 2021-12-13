package com.ipiecoles.audiotheque.controller;

import com.ipiecoles.audiotheque.model.Album;
import com.ipiecoles.audiotheque.model.Artist;
import com.ipiecoles.audiotheque.repository.AlbumRepository;
import com.ipiecoles.audiotheque.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/artists")
public class ArtistController {
    @Autowired private ArtistRepository artistRepository;
    @Autowired private AlbumRepository albumRepository;

    //1 - Afficher un artiste
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Artist findArtistById(@PathVariable(value = "id")Long id){
        Optional<Artist> artist = artistRepository.findById(id);
        if(artist.isPresent()){
            return artist.get();
        }
        throw new EntityNotFoundException("L'artist d'identifiant " + id + " n'existe pas !");
    }
    //2 - Recherche par nom
    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            params = "name"
    )
    public List<Artist> findEmployeByName(@RequestParam("name") String name){
        List<Artist> artists = artistRepository.findByNameContainingIgnoreCase(name);
        return  artists;
    }

    //3 - Liste des artistes
    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Page<Artist> pageAllArtistsSortByProperty(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortProperty,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection){
        if(page < 0 || size <= 0 || size > 50){
            throw new IllegalArgumentException("page ou size ne peuvent pas être négatif, size ne peut être supérieur à 50 !");
        }
        List<String> properties = Arrays.asList("id", "name");
        if(!properties.contains(sortProperty)){
            throw new IllegalArgumentException("La propriété de tri " + sortProperty + " est incorrecte !");
        }
        PageRequest pageRequest = PageRequest.of(page, size, sortDirection, sortProperty);
        Page<Artist> artists = artistRepository.findAll(pageRequest);
        if(artists.isEmpty()){
            throw new IllegalArgumentException("La valeur de la page est trop élevé par rapport au nombre de page généré !");
        }

        return artists;
    }

    //4 - Création d'un artiste
    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.CREATED)
    public Artist createNewArtist(@RequestBody() Artist artist){
        //Si le champ Nom est vide BAD REQUEST
        if (artist.getName() == "" || artist.getName() == null){
            throw new IllegalArgumentException("Veuillez remplir le champ nom !");
        }
        if(artist.getId() != null && artistRepository.existsById(artist.getId()) ||
                artistRepository.existsByName(artist.getName())){
            throw new EntityExistsException("Il existe déjà un artiste identique en base !");
        }
        try {
            return artistRepository.save(artist);
        }
        catch(Exception e){
            throw new IllegalArgumentException("Problème lors de la sauvegarde de l'artiste !");
        }
    }

    //5 - Modification d'un artiste
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Artist modifyArtist(
            @RequestBody() Artist artist
    ){
        //Si le champ Nom est vide BAD REQUEST
        if (artist.getName() == ""){
            throw new IllegalArgumentException("Veuillez remplir le champ nom !");
        }
        return artistRepository.save(artist);
    }

    //6 - Suppression d'un artiste
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteArtist(@PathVariable(value = "id") Long id){
        Optional<Artist> artist = artistRepository.findById(id);
        //boucle de suppression des albums de l'artiste
        for (Album album:artist.get().getAlbums()
             ) {
            albumRepository.deleteById(album.getId());
        }
        artistRepository.deleteById(id);

    }

}
