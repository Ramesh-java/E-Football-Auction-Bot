package com.example.auctionbot.service;

import com.example.auctionbot.DTO.Data;
import com.example.auctionbot.DTO.Message;
import com.example.auctionbot.model.Player;
import com.example.auctionbot.model.Team;
import com.example.auctionbot.repository.TeamRepo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TeamService {

    private final TeamRepo teamRepo;
    private final PlayerService playerService;

    public TeamService(TeamRepo teamRepo, @Lazy PlayerService playerService) {
        this.playerService = playerService;
        this.teamRepo = teamRepo;
    }

    public Message sell(Data data){
        String playerName=data.getName();//player playerName
        int price=data.getPrice();// player price
        String team=data.getTeam();// team playerName
        Message message=new Message();

        if (playerService.isSold(playerName)){
            message.setContent(playerName+" already sold");
            return message;
        }
        StringBuilder response=new StringBuilder();
        if (isValid(team,price)){

            Player player=playerService.sold(playerName,price,getTeamByName(team));
            if (player==null){
                response.append("Maximum limit reached for "+team);
                message.setContent(response.toString());
                return message;
            }
            response.append(playerName).append(" is sold to ").append(team).append(" for ").append(price).append("M");
            message.setContent(response.toString());
            return message;
        }
        response.append("Insufficient Balance for ").append(team).append(" so cant able to register ").append(playerName);
        message.setContent(response.toString());
        return message;
    }

    public Team findById(String name) {
        return teamRepo.findById(name).orElse(null);
    }

    public Team getTeamByName(String name) {
        return teamRepo.findById(name).orElse(null);
    }


    public boolean isValid(String name, int price) {
        Team team = this.teamRepo.findById(name).orElse(null);

        if (price == 0 || price < 0 || team == null) {
            return false;
        }
        int balance = team.getBalance();
        return (balance - price) >= 0;
    }

    public Message getBalance(String name) {
        Team team = this.teamRepo.findById(name).orElse(null);

        StringBuilder response = new StringBuilder();
        Message message = new Message();

        if (team == null) {
            response.append(name).append(" Not found , check the spelling");
            message.setContent(response.toString());
            return message;
        }
        int balance = team.getBalance();
        response.append(team.getName()).append(" Current Balance ").append(balance);
        message.setContent(response.toString());
        return (message);


    }


    public Message getAllBalance() {
        StringBuilder response = new StringBuilder();
        Message message = new Message();
        List<Team> teams = teamRepo.findAll();
        for (Team team : teams) {
            response.append(team.getName()).append(" ").append(team.getBalance()).append("M Balance").append("\n\n");
        }

        message.setContent(response.toString());
        return message;

    }

    public void save(Team team) {
        this.teamRepo.save(team);
    }

    public StringBuilder getTeamDetails(String team) {
        StringBuilder response = new StringBuilder();
        response.append("Team : ").append(team.toUpperCase()).append("\n\n");
        Team currentTeam = getTeamByName(team);
        List<Player> list = playerService.getPlayersByTeam(team);
        int index = 1;
        for (Player player : list) {
            response.append(index++).append(") ").append(player.getName()).append(" ").append(player.getPrice()).append("M").append("\n");
        }
        int maxRemainingPlayersToBuy = Math.max((25 - currentTeam.getSize()), 0);
        int minRemainingPlayersToBuy = Math.max(23 - currentTeam.getSize(), 0);
        response.append("\n").append("Minimum you need  ").append(minRemainingPlayersToBuy).append(" more players")
                .append("\n").append("Maximum You can buy ").append(maxRemainingPlayersToBuy).append(" more players")
                .append("\n")
                .append("Your Current Balance ").append(currentTeam.getBalance());
        return response;
    }

    public Message retention(Map<String,List<String>>map){
        List<String> list=map.get("items");

        String teamName=list.get(0).split(" ")[2];
        System.out.println(list+" "+list.size()+" "+teamName.replaceAll(" ",""));

        Team team=getTeamByName(teamName);
        StringBuilder response = new StringBuilder();
        Message message = new Message();
        if (team!=null) {
            for (int i = 1; i < list.size(); i++) {
                if (list.get(i).isEmpty())continue;
                Player player = playerService.sold(list.get(i), 2, team);

                if (player == null) {
                    response.append("some error has occurred ");
                    message.setContent(response.toString());
                    return message;
                }

                response.append(player.getName()).append("sold").append("\n");
            }

            message.setContent(response.toString());
            return message;
        }
        message.setContent("Team Not Found");
        return message;
    }



}