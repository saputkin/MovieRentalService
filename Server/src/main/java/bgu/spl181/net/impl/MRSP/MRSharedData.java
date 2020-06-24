package bgu.spl181.net.impl.MRSP;

import bgu.spl181.net.impl.MRSP.pojoBase.Movie;
import bgu.spl181.net.impl.MRSP.pojoBase.usersAndMoviespojo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MRSharedData extends SharedData {
    protected ConcurrentLinkedQueue<Movie> movies;
    private int current_id;
    private final Object lockMovies;

    public MRSharedData() {
        super();
        lockMovies= new Object();
        readMoviesJson("Database/Movies.json");//read from database
        current_id=findId();
    }

    public void addMovie(Movie movie){
        movies.add(movie);
    }

    public String getNextId(){//increments id for movies

        current_id++;
        return Integer.toString(current_id);
    }

    public Movie findMovie(String name){

        for(Movie movie : movies) {
            if (movie.getName().equals(name))
                return movie;
        }

        return null;
    }

    private int findId(){

        String type ="0";

        for(Movie movie:movies)
        {
            if(movie.getId().compareTo(type)>=0)
                type=movie.getId();
        }
        return Integer.parseInt(type);
    }

    public void updateMoviesJson(){//used to keep database synced

        synchronized (lockMovies) {

            ArrayList<Movie> arrayList = new ArrayList<>(movies);

            Gson gson;
            gson = new GsonBuilder().setPrettyPrinting().create();

            JsonObject userObj = new JsonObject();
            JsonArray usersArr = gson.toJsonTree(movies).getAsJsonArray();

            userObj.add("movies", usersArr);
            String json = gson.toJson(userObj);
            FileWriter writer = null;

            try
            {
                writer = new FileWriter("Database/Movies.json");
                writer.write(json.toString());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    public void readMoviesJson(String path){
        Gson gson = new Gson();
        JsonReader reader=null;

        try {
            reader = new JsonReader(new FileReader(path));
        } catch (FileNotFoundException e) {}

        usersAndMoviespojo lists;
        lists = gson.fromJson(reader,usersAndMoviespojo .class);
        movies= new ConcurrentLinkedQueue<>(lists.getMovies());

    }



}
