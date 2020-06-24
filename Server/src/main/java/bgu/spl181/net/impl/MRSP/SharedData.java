package bgu.spl181.net.impl.MRSP;

import bgu.spl181.net.impl.MRSP.pojoBase.User;
import bgu.spl181.net.impl.MRSP.pojoBase.usersAndMoviespojo;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedData {
   protected ConcurrentHashMap<Integer,User> loggedUsers;
   protected ConcurrentLinkedQueue<User> users;

   private final Object lockUsers;//for synchronization


    public SharedData(){

        loggedUsers = new ConcurrentHashMap<>();
        lockUsers = new Object();

      readUsersJson("Database/Users.json");
    }

    public ConcurrentLinkedQueue<User> getUsers() {
        return users;
    }

    public ConcurrentHashMap<Integer, User> getLoggedUsers() {
        return loggedUsers;
    }

    public void addUser(User user){
        users.add(user);
    }

    public User containUserName(String username){//find user in user list
        username = username.trim();
        for(User user : users){
            if(user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean containLoggedUser(String username){//finds logged user in loggedusers
        Iterator it = loggedUsers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(( (User)pair.getValue()).getUsername().equals(username))
               return true;
        }
        return false;
    }

    public boolean removeLogged(String username){//logs out
        Iterator it = loggedUsers.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();

            if(((User)pair.getValue()).getUsername().equals(username)){
                loggedUsers.remove(pair.getKey());
                return true;
            }
        }
        return false;
    }

    private void readUsersJson(String path){
        Gson gson = new Gson();
        JsonReader reader=null;

            try {
                reader = new JsonReader(new FileReader(path));
                }
                catch (FileNotFoundException e) { }
                usersAndMoviespojo lists;

            if(reader!=null){
                lists = gson.fromJson(reader,usersAndMoviespojo.class);
                users= new ConcurrentLinkedQueue<>(lists.getUsers());
            }

    }

    public  void updateUsersJson(){

        synchronized (lockUsers) {
            Gson gson;
            gson = new GsonBuilder().setPrettyPrinting().create();

            JsonObject userObj = new JsonObject();
            JsonArray usersArr = gson.toJsonTree(users).getAsJsonArray();

            userObj.add("users", usersArr);
            String json = gson.toJson(userObj);

            FileWriter writer = null;
            try {
                writer = new FileWriter("Database/Users.json");
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
}
