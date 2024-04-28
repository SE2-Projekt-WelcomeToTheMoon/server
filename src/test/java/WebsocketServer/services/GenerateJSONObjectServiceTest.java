package WebsocketServer.services;

import WebsocketServer.services.json.GenerateJSONObjectService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenerateJSONObjectServiceTest {

    @Test
    void testGenerateJSONObject(){
        String action = "testAction";
        String username = "testUser";
        Boolean success = true;
        String message = "testMessage";
        String error = "testError";

        JSONObject result = GenerateJSONObjectService.generateJSONObject(action, username, success, message, error);

        assertEquals(action, result.get("action"));
        assertEquals(username, result.get("username"));
        assertEquals(success, result.get("success"));
        assertEquals(message, result.get("message"));
        assertEquals(error, result.get("error"));
    }

    @Test
    void testGenerateJSONObjectWithNullValues() {
        JSONObject result = GenerateJSONObjectService.generateJSONObject(null, null, null, null, null);

        assertFalse(result.has("action"));
        assertFalse(result.has("username"));
        assertFalse(result.has("success"));
        assertFalse(result.has("message"));
        assertFalse(result.has("error"));
    }

    @Test
    void testGenerateJSONObjectWithEmptyStrings() {
        JSONObject result = GenerateJSONObjectService.generateJSONObject("", "", null, "", "");

        assertTrue(result.has("action"));
        assertTrue(result.has("username"));
        assertFalse(result.has("success"));
        assertFalse(result.has("message"));
        assertFalse(result.has("error"));
    }
}
