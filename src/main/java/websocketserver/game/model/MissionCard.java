package websocketserver.game.model;

import websocketserver.game.enums.MissionType;

public class MissionCard {
    private final MissionType missionType;
    private Reward reward;
    private boolean isFlipped;
    private boolean rewardDecreaseNextRound;

    public MissionCard(MissionType missionType, Reward reward) {
        this.missionType = missionType;
        this.reward = reward;
        this.isFlipped = false;
        this.rewardDecreaseNextRound = false;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public Reward getReward() {
        return reward;
    }

    public void flipCard() {
        if (!isFlipped) {
            this.isFlipped = true;
            this.rewardDecreaseNextRound = true;
        }
    }

    public void applyRewardDecrease() {
        if (rewardDecreaseNextRound) {
            this.reward = new Reward(reward.getCategory(), reward.getNumberRockets() - 1);
            this.rewardDecreaseNextRound = false;
        }
    }
}
