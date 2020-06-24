
package bgu.spl181.net.impl.MRSP.pojoBase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Movie {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("bannedCountries")
    @Expose
    private List<String> bannedCountries = null;
    @SerializedName("availableAmount")
    @Expose
    private String availableAmount;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;

    public Movie(String id,String name,String availableAmount,List<String> bannedCountries,String price){
        this.id=id;
        this.name=name;
        this.price=price;
        if(bannedCountries!=null)this.bannedCountries=bannedCountries;
        else this.bannedCountries = new ArrayList<>();
        this.availableAmount=availableAmount;
        this.totalAmount=availableAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }

    public void setBannedCountries(List<String> bannedCountries) {
        this.bannedCountries = bannedCountries;
    }

    public String getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(String availableAmount) {
        this.availableAmount = availableAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public synchronized void incOrDec(boolean inc){
        if (inc) {
            String amount = getAvailableAmount();
            int newAmount = Integer.parseInt(amount) + 1;
            setAvailableAmount(Integer.toString(newAmount));
        }
        else{
            String amount = getAvailableAmount();
            int newAmount = Integer.parseInt(amount) -1;
            setAvailableAmount(Integer.toString(newAmount));
        }
    }

}
