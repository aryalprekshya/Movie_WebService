/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prekshya.moviewebservice.services;

import com.prekshya.moviewebservice.controllers.GenreJpaController;
import com.prekshya.moviewebservice.controllers.MoviesJpaController;
import com.prekshya.moviewebservice.models.Genre;
import com.prekshya.moviewebservice.models.Movies;
import com.prekshya.moviewebservice.services.exceptions.NonexistentEntityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author prekshya
 */

@WebService
@HandlerChain(file = "/MovieServiceService_handler.xml")
public class MovieService {
    
     @WebMethod(operationName="add")
    public boolean add(@WebParam(name="name") String name, @WebParam(name="duration") double duration,
            @WebParam(name="genre") int genreId,@WebParam(name="director") String director,
            @WebParam(name="producer") String producer, @WebParam(name="image") byte[] image) throws Exception
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MOVIES_PU");
        MoviesJpaController movieRepo = new MoviesJpaController(emf);
        
        GenreJpaController genreRepo = new GenreJpaController(emf);
        
        Genre genre = genreRepo.findGenre(genreId);
        
         Movies movie = new Movies();
         
         movie.setName(name);
         movie.setDuration(duration);
         
         movie.setGenreId(genre);
         movie.setDirector(director);
         movie.setProducer(producer);
         movie.setImage(image);
         
         movieRepo.create(movie);
        
        return true;
    }
    
    @WebMethod(operationName="GetAll")
    public List<Movies> getAll(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MOVIES_PU");
        MoviesJpaController movieRepo = new MoviesJpaController(emf);
        List <Movies> movieList = movieRepo.findMoviesEntities();
        return movieList;  
    }
    
    @WebMethod(operationName="GetById")
    public Movies getById(@WebParam(name="id") int id){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MOVIES_PU");
        MoviesJpaController movieRepo = new MoviesJpaController(emf);
        return movieRepo.findMovies(Long.valueOf(id));  
    }
    
    @WebMethod(operationName="DeleteById")
    public boolean deleteById(@WebParam(name="id") int id){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MOVIES_PU");
        MoviesJpaController movieRepo = new MoviesJpaController(emf);
        try {  
            movieRepo.destroy(Long.valueOf(id));
            return true;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MovieService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    @WebMethod(operationName="update")
    public boolean update(@WebParam(name="id") int id,@WebParam(name="name") String name, @WebParam(name="duration") double duration,
            @WebParam(name="genre") int genre,@WebParam(name="director") String director,
            @WebParam(name="producer") String producer, @WebParam(name="image") byte[] image) throws Exception
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MOVIES_PU");
        MoviesJpaController movieRepo = new MoviesJpaController(emf);
        
         Movies movie = new Movies();
         movie.setId(Long.valueOf(id));
         movie.setImage(image);
         movie.setName(name);
         movie.setDirector(director);
         movie.setDuration(duration);
         Genre gen = new Genre();
         gen.setId(genre);
         movieRepo.edit(movie);
        
        return true;
    }
    
     @WebMethod(operationName="getGenre")
     public List<Genre> getGenre(){
         EntityManagerFactory emf = Persistence.createEntityManagerFactory("MOVIES_PU");
         GenreJpaController genreRepo = new GenreJpaController(emf);
         
         return genreRepo.findGenreEntities();
    }
    
}
