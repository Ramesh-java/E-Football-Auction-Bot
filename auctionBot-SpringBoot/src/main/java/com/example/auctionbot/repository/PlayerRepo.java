package com.example.auctionbot.repository;

import com.example.auctionbot.model.Player;
import com.example.auctionbot.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepo extends JpaRepository<Player, Long> {
List<Player> findAllByTeam(Team team);

Player findPlayerByName(String name);
}
