package com.boot.sseemitter.service;

import com.boot.sseemitter.model.Flight;
import com.boot.sseemitter.repository.FlightRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class FlightService {

    @Autowired
    protected FlightRepository repo;

    public long getTotalFlights() {
        log.info("Finding total no of total flights from database");
        return repo.count();
    }

    public List<Flight> getFlights() {
        log.info("fetching all flights available in database");
       /* Integer[] empIds = { 1, 2, 3 };
        Stream.of(Arrays.asList(empIds)).map(repo::findAllById).filter(e->e != null).collect(Collectors.toList());*/
        return repo.findAll();
    }

}
