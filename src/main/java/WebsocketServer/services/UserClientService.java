package WebsocketServer.services;

import java.util.HashMap;

public class UserClientService {
    private static HashMap<Short, String> userClients;
    private static short clientID;
    public UserClientService(){
        this.clientID = 0;
    }
    public static boolean setUsername(String usrName){
        if (!userClients.containsValue(usrName)){
            userClients.put(clientID++, usrName);
            return true;
        }
        else return false;
    }
    public String getUserClients(short key) {
        return this.userClients.get(key);
    }
    public short getClientID() {
        return this.clientID;
    }
}
