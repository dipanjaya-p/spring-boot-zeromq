package com.boot.sseemitter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "flight")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int FLIGHT_ID;
    @Column
    protected String OPERATING_AIRLINES;
    @Column
    protected String ARRIVAL_CITY;
    @Column
    protected String DEPARTURE_CITY;
    @Column
    protected Date DATE_OF_DEPARTURE;

}
