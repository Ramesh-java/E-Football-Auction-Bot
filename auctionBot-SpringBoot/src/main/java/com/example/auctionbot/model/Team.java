package com.example.auctionbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
public class Team {

    @Id
    private String name;

    private int balance;

    private int size;



}
