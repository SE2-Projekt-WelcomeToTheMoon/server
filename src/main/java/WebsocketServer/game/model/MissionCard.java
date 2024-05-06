package WebsocketServer.game.model;

import WebsocketServer.game.enums.RewardCategory;

public class MissionCard {
    private String missionDescription;
    private Reward reward;
    private boolean isFlipped;

    public MissionCard(String missionDescription, Reward reward) {
        this.missionDescription = missionDescription;
        this.reward = reward;
        this.isFlipped = false;
    }

    public String getMissionDescription() {
        return missionDescription;
    }

    public Reward getReward() {
        return reward;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void flipCard() {
        this.isFlipped = true;
        this.reward = new Reward(RewardCategory.PLANING, this.reward.getNumberRockets() / 2); // Adjust reward based on game rules
    }
}
