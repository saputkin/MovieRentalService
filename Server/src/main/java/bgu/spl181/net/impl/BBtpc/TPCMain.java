package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.MRSP.MRSP;
import bgu.spl181.net.impl.MRSP.MRSharedData;
import bgu.spl181.net.impl.MessageEncoderDecoderImpl;

public class TPCMain {

    public static void main(String[] args){


        MRSharedData sharedData = new MRSharedData();//builder have to add the json lists to this object
        new BBtpc<String>(Integer.parseInt(args[0]),()-> new MRSP(sharedData), MessageEncoderDecoderImpl::new).serve();


    }
}
