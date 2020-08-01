/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prekshya.moviewebservice.controllers;

import com.prekshya.moviewebservice.models.Genre;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.prekshya.moviewebservice.models.Movies;
import com.prekshya.moviewebservice.services.exceptions.IllegalOrphanException;
import com.prekshya.moviewebservice.services.exceptions.NonexistentEntityException;
import com.prekshya.moviewebservice.services.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Prekshya
 */
public class GenreJpaController implements Serializable {

    public GenreJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Genre genre) throws PreexistingEntityException, Exception {
        if (genre.getMoviesCollection() == null) {
            genre.setMoviesCollection(new ArrayList<Movies>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Movies> attachedMoviesCollection = new ArrayList<Movies>();
            for (Movies moviesCollectionMoviesToAttach : genre.getMoviesCollection()) {
                moviesCollectionMoviesToAttach = em.getReference(moviesCollectionMoviesToAttach.getClass(), moviesCollectionMoviesToAttach.getId());
                attachedMoviesCollection.add(moviesCollectionMoviesToAttach);
            }
            genre.setMoviesCollection(attachedMoviesCollection);
            em.persist(genre);
            for (Movies moviesCollectionMovies : genre.getMoviesCollection()) {
                Genre oldGenreIdOfMoviesCollectionMovies = moviesCollectionMovies.getGenreId();
                moviesCollectionMovies.setGenreId(genre);
                moviesCollectionMovies = em.merge(moviesCollectionMovies);
                if (oldGenreIdOfMoviesCollectionMovies != null) {
                    oldGenreIdOfMoviesCollectionMovies.getMoviesCollection().remove(moviesCollectionMovies);
                    oldGenreIdOfMoviesCollectionMovies = em.merge(oldGenreIdOfMoviesCollectionMovies);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findGenre(genre.getId()) != null) {
                throw new PreexistingEntityException("Genre " + genre + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Genre genre) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Genre persistentGenre = em.find(Genre.class, genre.getId());
            Collection<Movies> moviesCollectionOld = persistentGenre.getMoviesCollection();
            Collection<Movies> moviesCollectionNew = genre.getMoviesCollection();
            List<String> illegalOrphanMessages = null;
            for (Movies moviesCollectionOldMovies : moviesCollectionOld) {
                if (!moviesCollectionNew.contains(moviesCollectionOldMovies)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movies " + moviesCollectionOldMovies + " since its genreId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Movies> attachedMoviesCollectionNew = new ArrayList<Movies>();
            for (Movies moviesCollectionNewMoviesToAttach : moviesCollectionNew) {
                moviesCollectionNewMoviesToAttach = em.getReference(moviesCollectionNewMoviesToAttach.getClass(), moviesCollectionNewMoviesToAttach.getId());
                attachedMoviesCollectionNew.add(moviesCollectionNewMoviesToAttach);
            }
            moviesCollectionNew = attachedMoviesCollectionNew;
            genre.setMoviesCollection(moviesCollectionNew);
            genre = em.merge(genre);
            for (Movies moviesCollectionNewMovies : moviesCollectionNew) {
                if (!moviesCollectionOld.contains(moviesCollectionNewMovies)) {
                    Genre oldGenreIdOfMoviesCollectionNewMovies = moviesCollectionNewMovies.getGenreId();
                    moviesCollectionNewMovies.setGenreId(genre);
                    moviesCollectionNewMovies = em.merge(moviesCollectionNewMovies);
                    if (oldGenreIdOfMoviesCollectionNewMovies != null && !oldGenreIdOfMoviesCollectionNewMovies.equals(genre)) {
                        oldGenreIdOfMoviesCollectionNewMovies.getMoviesCollection().remove(moviesCollectionNewMovies);
                        oldGenreIdOfMoviesCollectionNewMovies = em.merge(oldGenreIdOfMoviesCollectionNewMovies);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = genre.getId();
                if (findGenre(id) == null) {
                    throw new NonexistentEntityException("The genre with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Genre genre;
            try {
                genre = em.getReference(Genre.class, id);
                genre.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The genre with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Movies> moviesCollectionOrphanCheck = genre.getMoviesCollection();
            for (Movies moviesCollectionOrphanCheckMovies : moviesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Genre (" + genre + ") cannot be destroyed since the Movies " + moviesCollectionOrphanCheckMovies + " in its moviesCollection field has a non-nullable genreId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(genre);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Genre> findGenreEntities() {
        return findGenreEntities(true, -1, -1);
    }

    public List<Genre> findGenreEntities(int maxResults, int firstResult) {
        return findGenreEntities(false, maxResults, firstResult);
    }

    private List<Genre> findGenreEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Genre.class));
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

    public Genre findGenre(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Genre.class, id);
        } finally {
            em.close();
        }
    }

    public int getGenreCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Genre> rt = cq.from(Genre.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
