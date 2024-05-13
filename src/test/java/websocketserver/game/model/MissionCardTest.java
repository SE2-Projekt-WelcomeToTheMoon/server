package websocketserver.game.model;

import websocketserver.game.enums.RewardCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissionCardTest {
    private MissionCard missionCard;
    private Reward reward;

    @BeforeEach
    void setUp() {
        reward = new Reward(RewardCategory.ROCKET, 3);
        missionCard = new MissionCard("Mission A1", reward);
    }

    @Test
    void testInitialMissionDescription() {
        assertEquals("Mission A1", missionCard.getMissionDescription(), "The mission description should match the initialized value.");
    }

    @Test
    void testInitialReward() {
        assertSame(reward, missionCard.getReward(), "The reward should be the same object as initialized.");
    }

    @Test
    void testInitialIsFlipped() {
        assertFalse(missionCard.isFlipped(), "Newly created mission card should not be flipped.");
    }

    @Test
    void testFlipCardChangesFlippedState() {
        missionCard.flipCard();
        assertTrue(missionCard.isFlipped(), "Flipping the card should set isFlipped to true.");
    }

    @Test
    void testFlipCardUpdatesReward() {
        missionCard.flipCard();
        assertNotNull(missionCard.getReward(), "Reward should not be null after flipping.");
        assertEquals(2, missionCard.getReward().getNumberRockets(), "The number of rockets should decrease by 1 when the card is flipped.");
    }
}