package bgu.spl181.net.srv.bidi;

import bgu.spl181.net.api.MessagingProtocol;

public class RequestProtocol implements MessagingProtocol<String> {
    private boolean shouldTerminate = false;
    private boolean success =false;
    private boolean broadcast = false;
    private String msg;
    private String name;
    private MessagingProtocol<String> cmdHandler;


    @Override
    public String process(String msg) {
        this.msg = msg;
        this.name = msg.substring(0,msg.indexOf(' ')-1);
        String output = cmdHandler.process(msg);
        return "1";
      //  if output
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

}
