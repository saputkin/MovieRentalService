/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl181.net.srv;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author bennyl
 */
/*Refactor the Thread-Per-Client server to support the new interfaces. The
ConnectionHandler should implement the new interface. Add calls for the new
Connections<T> interface. Notice that the ConnectionHandler<T> should now
work with the BidiMessagingProtocol<T> interface instead of
MessagingProtocol<T>*/
public interface ConnectionHandler<T> extends Closeable{

    void send(T msg) ;

}
