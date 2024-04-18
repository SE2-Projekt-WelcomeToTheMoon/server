package WebsocketServer.game.model;
import WebsocketServer.game.enums.RewardCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RewardTest {
    @Test
    void testRewardConstruction() {
        Reward reward = new Reward(RewardCategory.ROCKET);
        assertEquals(RewardCategory.ROCKET, reward.getCategory());
        assertFalse(reward.isRocketsFilled());
        assertFalse(reward.isSystemErrorClaimed());
        assertEquals(0, reward.getNumberRockets());
        assertEquals(0, reward.getUnfilledRockets());
    }

    @Test
    void testRewardConstructionWithNumberRockets() {
        Reward reward = new Reward(RewardCategory.ROCKET, 5);
        assertEquals(RewardCategory.ROCKET, reward.getCategory());
        assertFalse(reward.isRocketsFilled());
        assertFalse(reward.isSystemErrorClaimed());
        assertEquals(5, reward.getNumberRockets());
        assertEquals(0, reward.getUnfilledRockets());
    }

    @Test
    void testRewardConstructionWithUnfilledRockets() {
        Reward reward = new Reward(RewardCategory.UNFILLEDROCKET, 3);
        assertEquals(RewardCategory.UNFILLEDROCKET, reward.getCategory());
        assertFalse(reward.isRocketsFilled());
        assertFalse(reward.isSystemErrorClaimed());
        assertEquals(0, reward.getNumberRockets());
        assertEquals(3, reward.getUnfilledRockets());
    }

    @Test
    void testRewardConstructionWithInvalidCategory() {
        assertThrows(IllegalArgumentException.class,() -> new Reward(null));
    }

    @Test
    void testFillRockets() {
        Reward reward = new Reward(RewardCategory.ROCKET, 3);
        assertFalse(reward.isRocketsFilled());
        reward.fillRockets();
        assertTrue(reward.isRocketsFilled());
    }

    @Test
    void testClaimSystemError() {
        Reward reward = new Reward(RewardCategory.PLANING); // Any valid category
        assertFalse(reward.isSystemErrorClaimed());
        reward.claimSystemError();
        assertTrue(reward.isSystemErrorClaimed());
    }

    @Test
    void testRewardCategoryEnumeration() {
        for (RewardCategory category : RewardCategory.values()) {
            Reward reward = new Reward(category);
            assertEquals(category, reward.getCategory());
        }
    }

    @Test
    void testDefaultFieldValues() {
        Reward reward = new Reward(RewardCategory.ROCKETFILLING);
        assertFalse(reward.isRocketsFilled());
        assertFalse(reward.isSystemErrorClaimed());
        assertEquals(0, reward.getNumberRockets());
        assertEquals(0, reward.getUnfilledRockets());
    }
}
