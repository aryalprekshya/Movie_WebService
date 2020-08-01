/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prekshya.moviewebservice.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.prekshya.moviewebservice.models.Genre;
import com.prekshya.moviewebservice.models.Movies;
import com.prekshya.moviewebservice.services.exceptions.NonexistentEntityException;
import com.prekshya.moviewebservice.services.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author prekshya
 */
public class MoviesJpaController implements Serializable {

    public MoviesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movies movies) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Genre genreId = movies.getGenreId();
            if (genreId != null) {
                genreId = em.getReference(genreId.getClass(), genreId.getId());
                movies.setGenreId(genreId);
            }
            em.persist(movies);
            if (genreId != null) {
                genreId.getMoviesCollection().add(movies);
                genreId = em.merge(genreId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMovies(movies.getId()) != null) {
                throw new PreexistingEntityException("Movies " + movies + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Movies movies) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movies persistentMovies = em.find(Movies.class, movies.getId());
            Genre genreIdOld = persistentMovies.getGenreId();
            Genre genreIdNew = movies.getGenreId();
            if (genreIdNew != null) {
                genreIdNew = em.getReference(genreIdNew.getClass(), genreIdNew.getId());
                movies.setGenreId(genreIdNew);
            }
            movies = em.merge(movies);
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getMoviesCollection().remove(movies);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getMoviesCollection().add(movies);
                genreIdNew = em.merge(genreIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = movies.getId();
                if (findMovies(id) == null) {
                    throw new NonexistentEntityException("The movies with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movies movies;
            try {
                movies = em.getReference(Movies.class, id);
                movies.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movies with id " + id + " no longer exists.", enfe);
            }
            Genre genreId = movies.getGenreId();
            if (genreId != null) {
                genreId.getMoviesCollection().remove(movies);
                genreId = em.merge(genreId);
            }
            em.remove(movies);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Movies> findMoviesEntities() {
        return findMoviesEntities(true, -1, -1);
    }

    public List<Movies> findMoviesEntities(int maxResults, int firstResult) {
        return findMoviesEntities(false, maxResults, firstResult);
    }

    private List<Movies> findMoviesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movies.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Movies findMovies(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movies.class, id);
        } finally {
            em.close();
        }
    }

    public int getMoviesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movies> rt = cq.from(Movies.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
