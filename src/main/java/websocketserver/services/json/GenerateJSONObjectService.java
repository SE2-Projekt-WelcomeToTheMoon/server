package websocketserver.services.json;

import org.json.JSONObject;


public class GenerateJSONObjectService{
    private String action;
    private String username;
    private Boolean success = false;
    private String message;
    private String error;

    /**
     * @param action    Aktion die ausgeführt werden soll
     * @param username  Benutzername
     * @param success   Aktion erfolgreich oder nicht
     * @param message   Nachricht
     * @param error     Fehlermeldung
     */
    public GenerateJSONObjectService(String action, String username, Boolean success, String message, String error){
        this.action = action;
        this.username = username;
        this.success = success;
        this.message = message;
        this.error = error;
    }

    /**
     * Methode um ein JSONObject zu generieren.
     * @return JSONObject
     */
    public JSONObject generateJSONObject() {
        JSONObject response = new JSONObject();

        if (this.action != null) {
            response.put("action", this.action);
        }

        if (this.username != null) {
            response.put("username", this.username);
        }

        if(this.success != null){
            response.put("success", this.success);
        }

        //nur wenn String leer ist
        if (this.message != null && !message.isEmpty()) {
            response.put("message", this.message);
        }

        if (this.error != null && !error.isEmpty()) {
            response.put("error", this.error);
        }

        return response;
    }
}