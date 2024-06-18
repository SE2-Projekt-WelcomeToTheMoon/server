package websocketserver.services;

import websocketserver.services.json.GenerateJSONObjectService;
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

        JSONObject result = new GenerateJSONObjectService(action, username, success, message, error).generateJSONObject();

        assertEquals(action, result.get("action"));
        assertEquals(username, result.get("username"));
        assertEquals(success, result.get("success"));
        assertEquals(message, result.get("message"));
        assertEquals(error, result.get("error"));
    }

    @Test
    void testGenerateJSONObjectWithNullValues() {
        JSONObject result = new GenerateJSONObjectService(null, null, null, null, null).generateJSONObject();

        assertFalse(result.has("action"));
        assertFalse(result.has("username"));
        assertFalse(result.has("success"));
        assertFalse(result.has("message"));
        assertFalse(result.has("error"));
    }

    @Test
    void testGenerateJSONObjectWithEmptyStrings() {
        JSONObject result = new GenerateJSONObjectService("", "", null, "", "").generateJSONObject();

        assertTrue(result.has("action"));
        assertTrue(result.has("username"));
        assertFalse(result.has("success"));
        assertFalse(result.has("message"));
        assertFalse(result.has("error"));
    }
}
