package com.example.auctionbot.controller;

import com.example.auctionbot.DTO.Data;
import com.example.auctionbot.DTO.Message;
import com.example.auctionbot.model.Player;
import com.example.auctionbot.model.Team;
import com.example.auctionbot.service.PlayerService;
import com.example.auctionbot.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auction")
@CrossOrigin("*")
public class BotController {
    /*
    allCommands- view all endpoints

    /balance- gets team name as param and returns balance  !next

    /getAllBalance -returns all balance

    /rollback

    /getPlayers{team} - returns players of a single team
     */

    @GetMapping("/allCommands")
    public Message showAllCommands() {
        StringBuilder commands = new StringBuilder();

        commands.append("📌 *Available Auction Commands* 📌\n\n");
        commands.append("1️⃣  *!balance <team_name>*\n")
                .append("     ➝ Check balance of a particular team\n\n");
        commands.append("2️⃣  *!allBalance*\n")
                .append("     ➝ View balance of all teams\n\n");
        commands.append("3️⃣  *!view <team_name>*\n")
                .append("     ➝ View players of a particular team\n\n");

        Message message = new Message();
        message.setContent(commands.toString());

        return message;
    }

    private final TeamService teamService;
    private final PlayerService playerService;

    Player lastPlayer=null;

    public BotController(TeamService teamService, PlayerService playerService){
        this.teamService=teamService;
        this.playerService=playerService;
    }


    @PostMapping("/bid")
    public Message sold(@RequestBody Data data){//!sell
        Message message=teamService.sell(data);
        if (data.getName().startsWith(String.valueOf(message.getContent().charAt(0)))){
            lastPlayer=playerService.getPlayerByName(data.getName());
        }
        return message;
    }

    @GetMapping("/balance")
    public Message getBalance(@RequestParam("team") String team){//!balance
        return teamService.getBalance(team);
    }

    @GetMapping("/getAllBalance")
    public Message getAllBalance(){

        return teamService.getAllBalance();
    }

    @GetMapping("rollback")
    public Message rollBack(){//!rollback
        StringBuilder response=new StringBuilder();
        Message message=new Message();
        if (lastPlayer!=null){
            response.append(playerService.rollback(lastPlayer));
        }

        if (lastPlayer==null){
            response.append("player already rolled back");
            message.setContent(response.toString());
        }else {
            response.append(" rolled back");
            message.setContent(response.toString());
            lastPlayer=null;
        }
        return message;
    }

    @GetMapping("/getPlayers/{team}")// !view
    public Message getAll(@PathVariable String team){
        StringBuilder response=teamService.getTeamDetails(team);
        Message message=new Message();
        message.setContent(response.toString());
        return message;
    }
    static LinkedList<String> sets = new LinkedList<>();
    static LinkedList<String> unsold =new LinkedList<>();

    static int soldCount=0;
    @GetMapping("/next")
    public Message nextPlayer(){//!next
        StringBuilder response=new StringBuilder();
        Message message=new Message();

        if (!sets.isEmpty()){//next
            if (soldCount==10){
                response.append("SET FINISHED");
                message.setContent(response.toString());
                soldCount=0;

                return message;
            }
            String playerName= sets.removeFirst();
            soldCount+=1;
            
            if (!playerService.isSold(playerName)){
                response.append("Player name : ").append(playerName).append(" Base price 2M ");
                message.setContent(response.toString());

                return message;
            }else{
                response.append(playerName).append(" already sold!");
                message.setContent(response.toString());

                return message;
            }
        }else {
            response.append(" SETS FINISHED!");
            message.setContent(response.toString());
            return message;

        }
    }

    @PatchMapping("/delete")
    public void delete(@RequestBody Map<String,Object> values){//delete
       playerService.remove(values.get("player").toString(),values.get("team").toString());

    }

    @PostMapping("/addAll")
    public Message retention(@RequestBody Map<String,List<String>> map){//retention !addAll

        return teamService.retention(map);
    }

    @GetMapping("/unsold/{name}")
    public Message unsold(@PathVariable String name){// !unsold list
        unsold.add(name);
        StringBuilder response=new StringBuilder();
        response.append(name).append(" ").append("UNSOLD");
        Message message= new Message();
        message.setContent(response.toString());
        return message;
    }

    @GetMapping("/getUnsold")
    public Message listOfUnsold(){// !getUnsold players
        return playerService.unsold(unsold);
    }


    @GetMapping("/addSets")
    public void addPlayers(){
        /*
        Professional A
        */
//        // Elite A
//        sets.add("Costacurta");
//        sets.add("Puyol");
//        sets.add("Virgil Van Dijk");
//        sets.add("Cannavaro");
//        sets.add("Baresi");
//        sets.add("Saliba");
//        sets.add("Cafu");
//        sets.add("Lilian Thuram");
//        sets.add("Lee Dixon");
//        sets.add("Silvestre");
//
//        // Elite B
//        sets.add("Maldini");
//        sets.add("Van Buyten");
//        sets.add("Ruben Dias");
//        sets.add("Aldair");
//        sets.add("Belleti");
//        sets.add("Bergomi");
//        sets.add("Albert Ferrer");
//        sets.add("Denis Irwin");
//        sets.add("Phillip Lahm");
//        sets.add("Masiamino Oddo");

//        sets.add("Ben White");
//        sets.add("Kyle Walker");
//        sets.add("Magalhaes");
//        sets.add("Kim min jae");
//        sets.add("Ibrahim Konate");
//        sets.add("Joao Cancelo");
//        sets.add("C Romero");
//        sets.add("Upamecano");
//        sets.add("The Hernandez");
//        sets.add("Marquinos");
//
//        // Professional B
//        sets.add("Alexander Arnold");
//        sets.add("Van de Ven");
//        sets.add("Bastoni");
//        sets.add("M Thiaw");
//        sets.add("J Timber");
//        sets.add("Nuno Mendes");
//        sets.add("Jerard Brathwaite");
//        sets.add("Lucas Hernandez");
//        sets.add("De vriji");
//        sets.add("Fikayo Tomori");
//
//        // Professional C
//        sets.add("Gerard Pique");
//        sets.add("A Christensen");
//        sets.add("Thiago Silva");
//        sets.add("Pau Torres");
//        sets.add("Levi Colwill");
//        sets.add("Jon Stones");
//        sets.add("N Schotterbeck");
//        sets.add("Lisandro Martinez");
//        sets.add("Antonio Silva");
//        sets.add("J Kiwior");
//
//        // Professional D
//        sets.add("Diego Dalot");
//        sets.add("Wan Bissaka");
//        sets.add("Eric Garcia");
//        sets.add("Inigo Martinez");
//        sets.add("Carvajal");
//        sets.add("Sergio Ramos");
//        sets.add("Otamendi");
//        sets.add("Laporte");
//        sets.add("Lenglet");
//        sets.add("Akanji");
//
//        // Professional E
//        sets.add("Dean Hujilsen");
//        sets.add("Pavard");
//        sets.add("Alex Witsel");
//        sets.add("A Rahamani");
//        sets.add("Rico Lewis");
//        sets.add("Lewis Skelly");
//        sets.add("Naco");
//        sets.add("Robin Le Normand");
//        sets.add("Tomiyashu");
//        sets.add("Danilo");

//        sets.add("Hakimi");
//
//        // Dinesh
//        sets.add("Rudiger");
//        sets.add("Tony Adams");
//
//        // Gowtham
//        sets.add("Eder Militao");
//
//        // Kaif
//        sets.add("Cubarsi");
//        sets.add("Beckenbaur");
//
//        // Ramesh
//        sets.add("Gvardiol");
//        sets.add("Alphonso Davies");
//        sets.add("Carvajal");
//
//        // Ranjith
//        sets.add("Ronald Araujo");
//        sets.add("De Ligt");
//        sets.add("Koulibaly");
//        sets.add("Raul Asencio");
//
//        // Strange
//        sets.add("Roberto Carlos");


        // All-Time Legends (Retired)
//        sets.add("Zico");
//        sets.add("Xavi");
//        sets.add("Lothar Matthaus");
//        sets.add("Pavel Nedved");
//        sets.add("Ronaldinho");
//        sets.add("Paul Scholes");
//        sets.add("Steven Gerrard");
//        sets.add("Rivaldo");
//        sets.add("Pep Guardiola");
//        sets.add("Pirlo");
//        sets.add("Beckham");
//        sets.add("Kaka");
//        sets.add("Serginho");

        // Retired greats / cult legends
//        sets.add("Gattuso");
//        sets.add("Edgar Davids");
//        sets.add("Gilberto Silva");
//        sets.add("Makelele");
//        sets.add("Rijkaard");
//        sets.add("Isco");          // practically retired
//        sets.add("Thomas Rosicky");
//        sets.add("Ambrosini");
//        sets.add("Guti");
//        sets.add("Zé Roberto");
//        sets.add("Serginho");
//        sets.add("Dragon Stojkovic");
//        sets.add("Jack Wilshere");
//        sets.add("Fletcher");
//        sets.add("Emanuel Petit");
//        sets.add("Lampard");
//        sets.add("Ribery");
//        sets.add("Sneijder");
//        sets.add("Rudd Gullit");
//        sets.add("Xabi Alonso");

        // ======================================
        //        2. ACTIVE / REMAINING
        // ======================================

        // Elite A (Active)
//        sets.add("Rodri");
//        sets.add("Kante");
//        sets.add("Honeb");
//        sets.add("Bellingham");
//        sets.add("Kimmich");
//        sets.add("Pedri");
//        sets.add("Moises Caicedo");
//        sets.add("Declan Rice");
//
//        // Active Stars
//        sets.add("De Bruyne");
//        sets.add("Modric");
//        sets.add("Kroos");
//        sets.add("Jorginho");
//        sets.add("Thiago Alcantara");
//        sets.add("Bruno Fernandez");
//        sets.add("Tonali");
//        sets.add("Casemiro");
//        sets.add("Frenkie de Jong");
//        sets.add("Jamal Musiala");
//        sets.add("Tchouameni");
//        sets.add("Dimarco");
//        sets.add("Odegaard");
//        sets.add("Palmer");
//        sets.add("Dani Olmo");
//        sets.add("Bernardo Silva");
//        sets.add("Vitinha");
//        sets.add("Ruben Neves");
//        sets.add("Calhanoglu");
//        sets.add("Nicolo Barella");
//        sets.add("Camavinga");
//        sets.add("Valverde");
//        sets.add("Alexis Mac Allister");
//        sets.add("O Kokcu");
//
//        // Young talents / pros
//        sets.add("Gravenberch");
//        sets.add("Wirtz");
//        sets.add("Joao Neves");
//        sets.add("Desire Doue");
//        sets.add("Dominik Szoboszlai");
//        sets.add("Arda Guler");
//        sets.add("Rique Puig");
//        sets.add("Takefusa Kubo");
//        sets.add("Mohammed Kudus");
//        sets.add("Enzo Fernandez");
//        sets.add("Ilkay Gundogan");
//        sets.add("Mkhitaryan");
//        sets.add("Guendouzi");
//
//        // Others
//        sets.add("De Paul");

//        sets.add("Cristiano Ronaldo");
//        sets.add("Lionel Messi");
//        sets.add("Neymar Jr");
//        sets.add("Kylian Mbappe");
//        sets.add("Erling Haaland");
//        sets.add("Michael Owen");
//        sets.add("Bukayo Saka");
//        sets.add("Viktor Gyokeres");
//        sets.add("zlatan");
//        sets.add("S Eto");
//        sets.add("Hristo Stoichkov");
//        sets.add("Karl-Heinz Rummenigge");
//        sets.add("Gabriel Batistuta");
//        sets.add("Wayne Rooney");
//
//        // ===== ELITE A =====
//        sets.add("Luis Suarez");
//        sets.add("Mohamed Salah");
//        sets.add("Sadio Mane");
//        sets.add("Karim Benzema");
//        sets.add("Gareth Bale");
//        sets.add("Antoine Griezmann");

        // ===== ELITE B =====
//        sets.add("Robert Lewandowski");
//        sets.add("Harry Kane");
//        sets.add("Vinicius Jr");
//        sets.add("Rodrygo");
//        sets.add("Diego Forlan");
//        sets.add("Gabriel Jesus");
//        sets.add("Ferran Torres");
//        sets.add("Pierre Aubameyang");
//        sets.add("Ousmane Dembele");

        // ===== ELITE C =====
//        sets.add("Zlatan Ibrahimovic");
//        sets.add("Didier Drogba");
//        sets.add("Samuel Eto’o");
//        sets.add("Lamine Yamal");
//        sets.add("Fernando Torres");
//        sets.add("Thomas Muller");
//        sets.add("Marco van Basten");
//        sets.add("Eden Hazard");
//
//        // ===== ELITE D =====
//        sets.add("Federico Chiesa");
//        sets.add("Raphinha");
//        sets.add("Son Heung-min");
//        sets.add("Bebeto");
//        sets.add("Adriano");l;
//        sets.add("Roberto Baggio");

    }
}