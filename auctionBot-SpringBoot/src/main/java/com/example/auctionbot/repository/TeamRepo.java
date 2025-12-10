package com.example.auctionbot.repository;

import com.example.auctionbot.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepo extends JpaRepository<Team,String> {
}
