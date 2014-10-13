package com.cinema.processor;

import com.cinema.entity.Movie;
import com.cinema.entity.Status;
import com.gigaspaces.client.ChangeSet;
import org.openspaces.core.GigaSpace;
import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.polling.Polling;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Anna_Babich on 12.08.2014.
 *
*/
@EventDriven @Polling
public class ProcessorV1 {

    @Autowired
    GigaSpace gigaSpace;

    @EventTemplate
    public Movie newMovieTemplate(){
        Movie template = new Movie();
        template.setStatus(Status.NEW);
        return template;
    }

    @SpaceDataEvent
    public Movie setDirector(Movie movie){
        if (movie.getDirector() == null){
            movie.setDirector("Allen");
        }
        movie.setStatus(Status.VALID);
        return movie;
    }


}
