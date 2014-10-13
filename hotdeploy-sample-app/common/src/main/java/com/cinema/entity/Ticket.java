package com.cinema.entity;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;


import java.io.Serializable;


@SpaceClass
public class Ticket implements Serializable{

	private static final long serialVersionUID = -8221052342081762308L;


	private Integer idTicket;


	private Integer idMovie;


	private Integer seatNumber;

    private Status status;

    public Ticket() {
	}
	
	public Ticket(Integer idTicket, Integer idMovie, Integer seatNumber) {
		this.idTicket = idTicket;
		this.idMovie = idMovie;
		this.seatNumber = seatNumber;
	}

	@SpaceId
	public Integer getIdTicket() {
		return idTicket;
	}
	public void setIdTicket(Integer idTicket) {
		this.idTicket = idTicket;
	}
    @SpaceRouting
	public Integer getIdMovie() {
		return idMovie;
	}
	public void setIdMovie(Integer idMovie) {
		this.idMovie = idMovie;
	}
	public Integer getSeatNumber() {
		return seatNumber;
	}
	public void setSeatNumber(Integer seatNumber) {
		this.seatNumber = seatNumber;
	}
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "idTicket=" + idTicket +
                ", idMovie=" + idMovie +
                ", seatNumber=" + seatNumber +
                ", status=" + status +
                '}';
    }
}
