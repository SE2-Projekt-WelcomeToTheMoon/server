package WebsocketServer.websocket.handler;

import WebsocketServer.services.userServices.CreateUserService;
import WebsocketServer.services.GenerateJSONObjectService;
import WebsocketServer.services.LobbyService;
import WebsocketServer.services.userServices.UserListService;
import org.json.JSONObject;
import org.springframework.web.socket.*;

public class WebSocketHandlerImpl implements WebSocketHandler {

    private LobbyService lobbyService;
    private JSONObject messageJson;
    private JSONObject responseMessage;

    public WebSocketHandlerImpl(){
        this.lobbyService = new LobbyService();
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Verbindung erfolgreich hergestellt: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // TODO handle the messages here
        System.out.println("Nachricht erhalten: " + message.getPayload());
//        session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));

        if(message.getPayload().equals("Test message")){
            session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));
        }

        messageJson = new JSONObject(message.getPayload().toString());
        responseMessage = GenerateJSONObjectService.generateJSONObject();

        String action = (String) messageJson.getString("Action");

        switch (action) {
            case "registerUser":
                System.out.println("Setting Username...");
                UserListService.userList.addUser(new CreateUserService(messageJson.getString("username")));

                session.sendMessage(new TextMessage(responseMessage.toString()));
                break;
            case "joinLobby":
                System.out.println("Versuchen zur Lobby hinzufügen : " + session.getId() + " testUser ");
                  lobbyService.handleJoinLobby(session, messageJson);
                System.out.println("Erfolgreich zur Lobby hinzugefügt :  "+ session.getId() + " testUser ");
                  break;
            default:
                JSONObject response = GenerateJSONObjectService.generateJSONObject();
                response.put("Error", "Unbekannte Aktion");
                response.put("Action", action);
                session.sendMessage(new TextMessage(response.toString()));
                System.out.println("Unbekannte Aktion erhalten: " + action + " von " + "testUser");
                break;

        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Verbindung getrennt: "+ session.getId() + " " + closeStatus.toString());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}