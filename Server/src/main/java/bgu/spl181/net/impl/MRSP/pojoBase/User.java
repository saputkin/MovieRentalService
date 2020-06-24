
package bgu.spl181.net.impl.MRSP.pojoBase;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("movies")
    @Expose
    private List<userMovie> movies = null;
    @SerializedName("balance")
    @Expose
    private String balance;

    public User(String username,String type,String password,String country,String balance){
        this.username=username;
        this.type=type;
        this.password=password;
        this.country=country;
        this.balance=balance;
        movies = new ArrayList<>();
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<userMovie> getMovies() {
        return movies;
    }

    public void setMovies(List<userMovie> movies) {
        this.movies = movies;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public boolean removeByName(String name){
        for(userMovie movie: movies){
            if(movie.getName().equals(name)){
                movies.remove(movie);
                return true;
            }
        }
        return false;
    }

}
