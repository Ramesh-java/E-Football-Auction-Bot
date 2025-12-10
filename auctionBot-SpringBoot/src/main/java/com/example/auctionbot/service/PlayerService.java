package com.example.auctionbot.service;

import com.example.auctionbot.DTO.Data;
import com.example.auctionbot.DTO.Message;
import com.example.auctionbot.model.Player;
import com.example.auctionbot.model.Team;
import com.example.auctionbot.repository.PlayerRepo;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepo playerRepo;
    private final TeamService teamService;

    public PlayerService(PlayerRepo repo,@Lazy TeamService teamService){
        this.teamService=teamService;
        this.playerRepo =repo;
    }

    @Transactional(rollbackOn = Exception.class)
    public Player sold(String name,int price, Team team){//player name, price of the player, team
        if (team.getSize()>25){
            return null;
        }
        Player player=new Player();
        player.setName(name);
        player.setTeam(team);
        player.setPrice(price);



        team.setBalance(team.getBalance()-price);
        team.setSize(team.getSize()+1);
        teamService.save(team);

        return playerRepo.save(player);
    }






    @Transactional(rollbackOn = Exception.class)
    public String rollback(Player lastPlayer) {

         playerRepo.deleteById(playerRepo.findPlayerByName(lastPlayer.getName()).getId());
         Team team=lastPlayer.getTeam();
         team.setBalance(team.getBalance()+lastPlayer.getPrice());
         team.setSize(team.getSize()-1);
         teamService.save(team);

         return lastPlayer.getName();

    }

    public List<Player> getPlayersByTeam(String team) {
        Team team1=teamService.findById(team);
        return playerRepo.findAllByTeam(team1);
    }


    public Player getPlayerByName(String player) {
        return playerRepo.findById(playerRepo.findPlayerByName(player).getId()).orElse(null);

    }


    @Transactional(rollbackOn = Exception.class)
    public void remove(String playerName, String teamName) {
        Player player=getPlayerByName(playerName);
        Team team=teamService.getTeamByName(teamName);

        team.setBalance(team.getBalance()+player.getPrice());
        team.setSize(team.getSize()-1);
        teamService.save(team);
        playerRepo.deleteById(playerRepo.findPlayerByName(playerName).getId());
    }



    public boolean isUnsold(String name){
        Player player=playerRepo.findPlayerByName(name);
        return !playerRepo.existsById(player.getId());
    }


    public Message unsold(List<String>list){
        StringBuilder response=new StringBuilder();
        int index=0;
        for (String player:list){
                response.append(++index).append(" ").append(player).append("\n");
        }
        Message message=new Message();
        message.setContent(response.toString());
        return message;
    }


    public boolean isSold(String playerName){
        Player player=playerRepo.findPlayerByName(playerName);
        if (player==null)return false;
        return playerRepo.existsById(player.getId());
    }

}
