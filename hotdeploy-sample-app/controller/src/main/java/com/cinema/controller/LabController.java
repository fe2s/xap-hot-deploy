package com.cinema.controller;

import com.cinema.entity.Movie;
import com.cinema.entity.Status;
import org.apache.bcel.generic.NEW;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lab")
public class LabController {

    @Autowired
    private GigaSpace gigaSpace;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Movie addMovie(@RequestBody Movie movie) {
        movie.setStatus(Status.NEW);
        gigaSpace.write(movie);
        System.out.println(gigaSpace.count(new Movie()));
        return movie;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Movie[] getAllMovies() {
        return gigaSpace.readMultiple(new Movie());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Movie getMovie(@PathVariable Integer id) {
        return gigaSpace.readById(Movie.class, id);
    }


}
