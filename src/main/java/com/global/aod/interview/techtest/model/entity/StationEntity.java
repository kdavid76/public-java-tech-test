package com.global.aod.interview.techtest.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "station")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationEntity {

    @Id
    @GeneratedValue(generator = "station_entity_generator")
    @SequenceGenerator(name = "station_entity_generator", sequenceName = "station_entity_seq", allocationSize = 1)
    private Long id;

    private String name;

    @Version
    private Integer version;
}
