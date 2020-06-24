package bgu.spl181.net.impl.MRSP;

import bgu.spl181.net.impl.MRSP.pojoBase.Movie;
import bgu.spl181.net.impl.MRSP.pojoBase.User;
import bgu.spl181.net.impl.MRSP.pojoBase.userMovie;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MRSP extends UTSBP {
  //  MRSharedData sharedData;

    public MRSP(MRSharedData sharedData) {
        super(sharedData);
    }

    //add broadcasting from
    @Override
    protected String requestHandle(String msg) {

        String name;
        String parameters;

        String[] cmd = msg.split(" ");
        name = cmd[0];
        parameters="";

        if(cmd.length>1)
            parameters = msg.substring(msg.indexOf(" ") + 1);

        User client = getSharedData().getLoggedUsers().get(getConnectionId());


        String output = "";

        if(!isLogged())
            return "ERROR request" + name + " failed";

        switch (name)
        {
            case "info":
                {
                if (!parameters.equals(""))
                    parameters = parameters.substring(1,parameters.length()-1);
                output = info(client, parameters);
                break;
                }
            case "balance":
                {
                    String[] param = parameters.split(" +");

                    if (param[0].equals("info"))
                        output = balanceInfo(client, "");
                    if(param[0].equals("add"))
                    {
                        if(param.length<2)
                        break;

                    output = balanceAdd(client, param[1]);
                    }
                break;
                }
            case "balance add":
                output = balanceAdd(client, parameters);
                break;
            case "rent":
                output = rent(client, parameters.substring(1,parameters.length()-1));
                break;
            case "return":
                output = returnMovie(client,parameters.substring(1,parameters.length()-1));
                break;
            case "addmovie":
                output = addMovie(client, parameters);
                break;
            case "remmovie":
                output = remMovie(client, parameters.substring(1,parameters.length()-1));
                break;
            case "changeprice":
                output = changePrice(client, parameters);
                break;

        }
        if (output == null)
            return "ERROR request " + name + " failed";

        if (output .equals(""))
            return "ERROR request" + name + " invalid command";

        return output;
    }

    private MRSharedData getSharedData(){
        return (MRSharedData) getData();
    }


    public String balanceInfo(User client, String empty){
        return "ACK balance "+client.getBalance();
    }

    public String balanceAdd(User client,String toAdd){

        int cuurrent_balance = Integer.parseInt(client.getBalance());
        int add = Integer.parseInt(toAdd);
        int newBalance = cuurrent_balance+add;

        client.setBalance(Integer.toString(newBalance));
        getSharedData().updateUsersJson();//update

        return "ACK balance "+newBalance+" added "+add;
    }

    public String info(User client,String movieName){

        String output ="ACK info";

        if (movieName.equals("") )
        {
            for(Movie movie: getSharedData().movies){
                output=output+" \""+movie.getName()+"\"";
            }
        }
        else
            {
                for(Movie movie: getSharedData().movies)
                {
                    if(movie.getName().equals(movieName))
                    {
                        String bannedCountrys="";

                        for(String country:movie.getBannedCountries()){
                            bannedCountrys=bannedCountrys+" \""+country+"\"";
                        }
                        output+=" \""+movie.getName()+"\" "+movie.getAvailableAmount()
                                +" "+movie.getPrice()+bannedCountrys;

                        return output;
                    }

                }
                return null;
            }
        return output;
    }

    public String rent(User client,String movieName){

        Movie reqMovie = getSharedData().findMovie(movieName);

        String err = "ERROR request rent failed";
        Integer balance = Integer.parseInt(client.getBalance());

        if(reqMovie==null || Integer.parseInt(reqMovie.getAvailableAmount())==0
            || reqMovie.getBannedCountries().contains(client.getCountry())
                ||client.getMovies().contains(reqMovie))
            return err;

        if(Integer.parseInt(reqMovie.getPrice())>(balance))return err;

        reqMovie.incOrDec(false);//decrement movie quantity

        String new_balance = Integer.toString(balance-Integer.parseInt(reqMovie.getPrice()));
        client.setBalance(new_balance);
        userMovie movie = new userMovie(reqMovie.getId(),reqMovie.getName());
        client.getMovies().add(movie);

        getSharedData().updateUsersJson();//update Json
        getSharedData().updateMoviesJson();

        broadCast.add("BROADCAST movie \""+movieName+"\" "+reqMovie.getAvailableAmount()+" "+reqMovie.getPrice());
        toBroadCast=true;

        return "ACK rent \""+movieName+"\" success";
    }

    public String returnMovie(User client,String movieName){

        for(Movie movie:getSharedData().movies)
        {
            if (movie.getName().equals(movieName))
            {
                if (!client.removeByName(movieName)) return null;

                movie.incOrDec(true);//increment movie quantity

                getSharedData().updateMoviesJson();//update Json
                getSharedData().updateUsersJson();

                broadCast.add("BROADCAST movie \""+movie.getName()
                        +"\" "+movie.getAvailableAmount()+" "+movie.getPrice());//add broadcast to be sent
                toBroadCast=true;//notify the server to perform a broadcast

                return "ACK return \""+movie.getName()+"\" success";
            }
        }
        return null;
    }

    public String addMovie(User client,String addCmd){

        String error = "ERROR request addmovie failed";

        if(!client.getType().equals("admin"))
            return error;

        List<String> args = splitMovie(addCmd);

        if (args.size()<3)
            return error;

        if(getSharedData().findMovie(args.get(0).trim())!=null)
            return error;

        if(Integer.parseInt(args.get(1))<=0||Integer.parseInt(args.get(2))<=0)
            return error;

        List<String> bannedCountries = new LinkedList<>();

        for(int i=3;i<args.size();i++)
            bannedCountries.add(args.get(i).trim());

        Movie movie = new Movie(getSharedData().getNextId(),args.get(0),args.get(1),bannedCountries,args.get(2));
        getSharedData().addMovie(movie);

        getSharedData().updateMoviesJson();//update Json

        broadCast.add("BROADCAST movie \""+args.get(0)+"\" "+args.get(1)+" "+args.get(2));
        toBroadCast = true;
        return "ACK addmovie \""+args.get(0)+"\" success";
    }

    public String remMovie(User client,String movieName){
        if(!client.getType().equals("admin"))
            return null;

        for(Movie movie:getSharedData().movies){
            if (movie.getName().equals(movieName))
            {
                if (!movie.getAvailableAmount().equals(movie.getTotalAmount()))
                    return null;

                getSharedData().movies.remove(movie);
                getSharedData().updateMoviesJson();//update Json

                broadCast.add("BROADCAST movie \""+movie.getName() +"\" removed");
                toBroadCast=true;
                return "ACK remmovie \""+movie.getName()+"\" success";
            }
        }
        return null;
    }

    public String changePrice(User client,String param){

        param = param.trim();
        String[] params = param.split("\"");
        String movieName = params[1].trim();

        int price = Integer.parseInt(params[2].trim());
        if (price <= 0 || !client.getType().equals("admin"))
            return null;

        for(Movie movie:getSharedData().movies)
        {
            if (movie.getName().equals(movieName) )
            {
                movie.setPrice(params[2].trim());
                getSharedData().updateMoviesJson();//update Json

                broadCast.add("BROADCAST movie \""+movie.getName()
                        +"\" "+movie.getAvailableAmount() +" "+movie.getPrice());//add broadcast to be sent
               toBroadCast=true;//notify the server to perform broadcast
                return "ACK changeprice \""+movie.getName()+"\" success";
            }
        }
        return null;
    }
    public List<String> splitMovie(String args){
        List<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(args);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }
        return matchList;
    }





}
