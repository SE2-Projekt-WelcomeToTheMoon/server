package WebsocketServer.game.model;
import WebsocketServer.game.enums.RewardCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RewardTest {
    @Test
    public void testRewardConstruction() {
        Reward reward = new Reward(RewardCategory.ROCKET);
        assertEquals(RewardCategory.ROCKET, reward.category);
        assertFalse(reward.rocketsFilled);
        assertFalse(reward.systemErrorClaimed);
        assertEquals(0, reward.numberRockets);
        assertEquals(0, reward.unfilledRockets);
    }

    @Test
    public void testRewardConstructionWithNumberRockets() {
        Reward reward = new Reward(RewardCategory.ROCKET, 5);
        assertEquals(RewardCategory.ROCKET, reward.category);
        assertFalse(reward.rocketsFilled);
        assertFalse(reward.systemErrorClaimed);
        assertEquals(5, reward.numberRockets);
        assertEquals(0, reward.unfilledRockets);
    }

    @Test
    public void testRewardConstructionWithUnfilledRockets() {
        Reward reward = new Reward(RewardCategory.UNFILLEDROCKET, 3);
        assertEquals(RewardCategory.UNFILLEDROCKET, reward.category);
        assertFalse(reward.rocketsFilled);
        assertFalse(reward.systemErrorClaimed);
        assertEquals(0, reward.numberRockets);
        assertEquals(3, reward.unfilledRockets);
    }

    @Test
    public void testRewardConstructionWithInvalidCategory() {
        assertThrows(IllegalArgumentException.class,() -> new Reward(null));
    }

    @Test
    public void testFillRockets() {
        Reward reward = new Reward(RewardCategory.ROCKET, 3);
        assertFalse(reward.rocketsFilled);
        reward.fillRockets();
        assertTrue(reward.rocketsFilled);
    }

    @Test
    public void testClaimSystemError() {
        Reward reward = new Reward(RewardCategory.PLANING); // Any valid category
        assertFalse(reward.systemErrorClaimed);
        reward.claimSystemError();
        assertTrue(reward.systemErrorClaimed);
    }

    @Test
    public void testRewardCategoryEnumeration() {
        for (RewardCategory category : RewardCategory.values()) {
            Reward reward = new Reward(category);
            assertEquals(category, reward.category);
        }
    }

    @Test
    public void testDefaultFieldValues() {
        Reward reward = new Reward(RewardCategory.ROCKETFILLING);
        assertFalse(reward.rocketsFilled);
        assertFalse(reward.systemErrorClaimed);
        assertEquals(0, reward.numberRockets);
        assertEquals(0, reward.unfilledRockets);
    }
}
