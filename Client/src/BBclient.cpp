//
// Created by Artsap on 04/01/2018.
//
#include <stdlib.h>
#include <boost/thread.hpp>
#include "../include/connectionHandler.h"



class Task{
private:
    ConnectionHandler& connectionHandler;
    std::atomic_bool& isLogged;

public:
     Task(ConnectionHandler& handler,std::atomic_bool &isLogged):connectionHandler(handler),isLogged(isLogged){}

    void operator()(){
        while(1){
            int len;
            std::string answer;
            if (!connectionHandler.getLine(answer)) {
                std::cout << "Disconnected. Exiting...\n " << std::endl;
                break;
            }

            len=answer.length();
            answer.resize(len-1);
            std::cout <<answer<< std::endl;
            if(answer == "ACK login succeeded")
                isLogged=true;
            if (answer == "ACK signout succeeded") {
                std::cout << "Exiting...\n" << std::endl;
                connectionHandler.close();
                return;
            }

        }}
};
int main (int argc, char *argv[]) {
	
	 if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    
   std::string host = argv[1];
   short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::atomic_bool isLogged(false);
    Task run(connectionHandler,isLogged);
    boost::thread th1(run);

    while (1) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if (!connectionHandler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if(isLogged==true && line=="SIGNOUT"){
            break;
        }
    }
    th1.join();
    return 0;
}




