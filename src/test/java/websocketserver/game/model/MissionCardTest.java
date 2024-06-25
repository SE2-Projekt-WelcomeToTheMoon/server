package websocketserver.game.model;

import websocketserver.game.enums.RewardCategory;
import websocketserver.game.enums.MissionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissionCardTest {
    private MissionCard missionCard;
    private Reward reward;

    @BeforeEach
    void setUp() {
        reward = new Reward(RewardCategory.ROCKET, 3);
        missionCard = new MissionCard(MissionType.A1, reward);
    }

    @Test
    void testInitialMissionType() {
        assertEquals(MissionType.A1, missionCard.getMissionType(), "The mission type should match the initialized value.");
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
    void testFlipCardSetsRewardDecreaseNextRound() {
        missionCard.flipCard();
        assertTrue(missionCard.isFlipped(), "Flipping the card should set isFlipped to true.");
        missionCard.applyRewardDecrease();
        assertEquals(2, missionCard.getReward().getNumberRockets(), "The number of rockets should decrease by 1 when the reward decrease is applied.");
    }

    @Test
    void testApplyRewardDecrease() {
        missionCard.flipCard();
        missionCard.applyRewardDecrease();
        assertEquals(2, missionCard.getReward().getNumberRockets(), "The number of rockets should decrease by 1 after applying reward decrease.");
    }

    @Test
    void testApplyRewardDecreaseWithoutFlipping() {
        missionCard.applyRewardDecrease();
        assertEquals(3, missionCard.getReward().getNumberRockets(), "The number of rockets should not change if reward decrease is applied without flipping.");
    }

    @Test
    void testFlipCardOnlyOnce() {
        missionCard.flipCard();
        missionCard.applyRewardDecrease();
        int rocketsAfterFirstDecrease = missionCard.getReward().getNumberRockets();
        missionCard.applyRewardDecrease();
        assertEquals(rocketsAfterFirstDecrease, missionCard.getReward().getNumberRockets(), "The number of rockets should only decrease once, subsequent reward decreases should have no effect.");
        assertTrue(missionCard.isFlipped(), "Card should remain flipped after multiple flip attempts.");
    }
}
