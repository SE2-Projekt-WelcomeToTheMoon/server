package websocketserver.game.model;

import lombok.Getter;

public class MissionCard {
    @Getter
    private String missionDescription;
    @Getter
    private Reward reward;
    private boolean isFlipped;

    public MissionCard(String missionDescription, Reward reward) {
        this.missionDescription = missionDescription;
        this.reward = reward;
        this.isFlipped = false;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void flipCard() {
        this.isFlipped = true;
        this.reward = new Reward(reward.getCategory(), reward.getNumberRockets() - 1); // Decrease rockets by 1 upon flipping (because all rewards in the first map are decreased by 1)
    }
}
