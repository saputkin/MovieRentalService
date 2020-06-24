package bgu.spl181.net.impl.MRSP;

import bgu.spl181.net.impl.MRSP.pojoBase.User;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.ConnectionsImpl;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class UTSBP implements BidiMessagingProtocol<String> {

    static ConnectionsImpl<String> connections; //list of connected clients
    protected ConcurrentLinkedQueue<String> broadCast;//could be String, queue is more useful

    private int connectionId;//id of client
    private boolean shouldTerminate = false;

    private SharedData sharedData;//shared data all protocols have

    private boolean loggedIn;
    private String userName;

    protected boolean toBroadCast;

    public UTSBP(SharedData sharedData) {
        this.sharedData = sharedData;

    }


    @Override
    public void start(int connectionId, Connections<String> connections) {

        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl<String>) connections;

        loggedIn = false;
        broadCast=new ConcurrentLinkedQueue<>();
        toBroadCast=false;

    }


    @Override
    public void process(String message) {

        message = message.trim();
        String[] msg = message.split(" ");
        String parameters="";

        if (msg.length>1) {//the first word of the command describes its type

            parameters = message.substring(message.indexOf(" ") + 1);//the rest are parameters
        }

        String output="";

        switch (msg[0])
        {

            case "REGISTER":
                {
                    output =registerHandle(parameters) ;
                    break;
                }

            case "REQUEST":
                {
                    output = requestHandle(parameters);
                    break;
                }
            case "LOGIN":
                {
                    output = loginHandle(parameters);
                    break;
                }
            case "SIGNOUT":
                {
                    output=signOutHandle();
                    break;
                }
                default:{output ="Error";
                break;
                }
        }
        connections.send(this.connectionId,output);

        while(broadCast.size()>0){
            broadCaster(broadCast.poll());
        }

    }

    public ConnectionsImpl<String> getConnections() {
        return connections;
    }

    public SharedData getData(){
        return sharedData;
    }

    public boolean isLogged() {
        return loggedIn;
    }


    /**
     * @return true if the connection should be terminated
     */
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


    private String loginHandle(String msg) {

        String[] args = msg.split(" +");
        String error = "ERROR login failed";

        if(args.length < 2 ||isLogged()||sharedData.containLoggedUser(args[0]))
            return error;

        User logging = sharedData.containUserName(args[0]);

        if(logging==null)
            return error;

        if(!logging.getPassword().equals(args[1]))
            return error;

        loggedIn=true;
        userName=args[0].trim();//fix String
        sharedData.loggedUsers.put(connectionId,logging);

        return "ACK login succeeded";
    }

    private String registerHandle(String msg) {

        String[] splited = msg.split(" +");
        String error = "ERROR registration failed";

        if(splited.length<3 || isLogged())
            return error;

        if(sharedData.containUserName(splited[0])!=null)
            return error;

        String country = chekcDataBlock(splited[2].trim());

        if(country==null)
            return error;

        User newUser = new User(splited[0],"normal",splited[1].trim(),country,"0");

        userName=splited[0].trim();
        sharedData.addUser(newUser);
        sharedData.updateUsersJson();//update Json

        return "ACK registration succeeded";
    }

    private String chekcDataBlock(String block){//tests validity of client country

        if(!block.contains("country="))
            return null;

        String[] splitblock = block.split("\"");

        if(splitblock.length<2)
            return null;

        else
            return splitblock[1];
    }

    private String signOutHandle() {

        if(!sharedData.removeLogged(userName))
            return "ERROR signout failed";

        loggedIn=false;
        shouldTerminate=true;//program terminates after signout

        return "ACK signout succeeded";
    }


    protected abstract String requestHandle(String msg);

    public int getConnectionId() {
        return connectionId;
    }

    protected void broadCaster(String msg){//broadcasts to all connections

        Iterator it = sharedData.loggedUsers.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            getConnections().send((int)pair.getKey(),msg);
        }
    }
}