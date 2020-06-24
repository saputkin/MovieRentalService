package bgu.spl181.net.srv.bidi;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<String> {
    private int connectionId;
    private Connections<String> connections;
    private boolean shouldTerminate = false;

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId=connectionId;
        this.connections=connections;

    }

    @Override
    public void process(String message) {

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
