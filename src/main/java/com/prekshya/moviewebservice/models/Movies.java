/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prekshya.moviewebservice.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author prekshya
 */
@Entity
@Table(name = "MOVIES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Movies.findAll", query = "SELECT m FROM Movies m"),
    @NamedQuery(name = "Movies.findByName", query = "SELECT m FROM Movies m WHERE m.name = :name"),
    @NamedQuery(name = "Movies.findByYear", query = "SELECT m FROM Movies m WHERE m.year = :year"),
    @NamedQuery(name = "Movies.findByDirector", query = "SELECT m FROM Movies m WHERE m.director = :director"),
    @NamedQuery(name = "Movies.findByProducer", query = "SELECT m FROM Movies m WHERE m.producer = :producer"),
    @NamedQuery(name = "Movies.findByDuration", query = "SELECT m FROM Movies m WHERE m.duration = :duration"),
    @NamedQuery(name = "Movies.findById", query = "SELECT m FROM Movies m WHERE m.id = :id")})
public class Movies implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "NAME")
    private String name;
    @Column(name = "YEAR")
    @Temporal(TemporalType.TIMESTAMP)
    private Date year;
    @Size(max = 50)
    @Column(name = "DIRECTOR")
    private String director;
    @Size(max = 50)
    @Column(name = "PRODUCER")
    private String producer;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DURATION")
    private Double duration;
    @Lob
    @Column(name = "IMAGE")
    private byte[] image;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "GENRE_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Genre genreId;

    public Movies() {
    }

    public Movies(Long id) {
        this.id = id;
    }

    public Movies(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getYear() {
        return year;
    }

    public void setYear(Date year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Genre getGenreId() {
        return genreId;
    }

    public void setGenreId(Genre genreId) {
        this.genreId = genreId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movies)) {
            return false;
        }
        Movies other = (Movies) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.prekshya.moviewebservice.models.Movies[ id=" + id + " ]";
    }
    
}
