package bgu.spl181.net.srv;

import bgu.spl181.net.api.bidi.Connections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/*
Implement Connections<T> to hold a list of the new ConnectionHandler interface
for each active client. Use it to implement the interface functions. Notice that
given a connections implementation, any protocol should run. This means that
you keep your implementation of Connections on T.
 */

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler> activeClients = new ConcurrentHashMap<>();
    private int counter = 0;


    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler client = activeClients.get(connectionId);
        if(client==null) return false;
        client.send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {
        for(Map.Entry<Integer,ConnectionHandler> entry:activeClients.entrySet()){
            entry.getValue().send(msg);
        }

    }

    @Override
    public void disconnect(int connectionId) {
        activeClients.remove(connectionId);

    }
    public int addToActiveClients(ConnectionHandler ch){
        activeClients.put(counter, ch);
        int tmp = counter;
        counter++;
        return tmp;
    }
}
