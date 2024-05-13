package websocketserver.game.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemErrorsTest {
    SystemErrors systemErrors;
    @BeforeEach
    void setUp() {
        systemErrors = new SystemErrors();
    }

    @Test
    void testGetCurrentErrors(){
        for(int i = 0; i < 8; i++){
            assertEquals(i, systemErrors.getCurrentErrors());
            systemErrors.increaseCurrentErrors();
        }
    }
}