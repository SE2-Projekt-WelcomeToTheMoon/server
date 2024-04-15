package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.RewardCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RewardTest {
    @Test
    public void testRewardInitializationWithCategoryFloorAndChamber() {
        Reward reward = new Reward(RewardCategory.ROCKET, new Floor(FieldCategory.ROBOTER), new Chamber(FieldCategory.ROBOTER));
        assertNotNull(reward);
        assertEquals(RewardCategory.ROCKET, reward.category);
        assertEquals(0, reward.numberRockets);
        assertFalse(reward.rocketsFilled);
    }
    @Test
    public void testRewardInitializationWithCategoryFloorChamberAndRockets() {
        Reward reward = new Reward(RewardCategory.ROCKET, new Floor(FieldCategory.ROBOTER), new Chamber(FieldCategory.ROBOTER), 5);
        assertNotNull(reward);
        assertEquals(RewardCategory.ROCKET, reward.category);
        assertEquals(5, reward.numberRockets);
        assertFalse(reward.rocketsFilled);
    }
    @Test
    public void testRewardInitializationWithAllParameters() {
        Reward reward = new Reward(RewardCategory.ROCKET, new Floor(FieldCategory.ROBOTER), new Chamber(FieldCategory.ROBOTER), 3, true);
        assertNotNull(reward);
        assertEquals(RewardCategory.ROCKET, reward.category);
        assertEquals(3, reward.numberRockets);
        assertTrue(reward.rocketsFilled);
    }
}
