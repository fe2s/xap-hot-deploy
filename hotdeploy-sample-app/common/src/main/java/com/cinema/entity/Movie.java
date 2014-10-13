package com.cinema.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.io.Serializable;
import java.util.Date;


@SpaceClass
public class Movie implements Serializable{

	private Integer idMovie;
	private String title;

    private String director;

	private Integer totalSeats;

	private Integer freeSeats;

    private Status status;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="EET")
	private Date date;
	
	public Movie(){
	}

    public Movie(Integer id, String title, Integer totalSeats, Integer freeSeats, Date date) {
		this.idMovie = id;
		this.title = title;
		this.totalSeats = totalSeats;
		this.freeSeats = freeSeats;
		this.date = date;
	}
	
	@SpaceId
    @SpaceRouting
	public Integer getIdMovie() {
		return idMovie;
	}
	public void setIdMovie(Integer id) {
		this.idMovie = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public Integer getTotalSeats() {
		return totalSeats;
	}
	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}
	public Integer getFreeSeats() {
		return freeSeats;
	}
	public void setFreeSeats(Integer freeSeats) {
		this.freeSeats = freeSeats;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "idMovie=" + idMovie +
                ", title='" + title + '\'' +
                ", totalSeats=" + totalSeats +
                ", freeSeats=" + freeSeats +
                ", status=" + status +
                ", date=" + date +
                '}';
    }
}
