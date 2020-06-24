package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.impl.MRSP.MRSP;
import bgu.spl181.net.impl.MRSP.MRSharedData;
import bgu.spl181.net.impl.MessageEncoderDecoderImpl;


public class ReactorMain {

    public static void main(String[] args){
        MRSharedData sharedData = new MRSharedData(); //change the constructor to be empty do all the dirtywork
        new BBreactor<>(8,Integer.parseInt(args[0]),
                ()->new MRSP(sharedData), MessageEncoderDecoderImpl::new).serve();


    }

}
