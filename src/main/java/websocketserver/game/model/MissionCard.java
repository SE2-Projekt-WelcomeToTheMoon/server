package websocketserver.game.model;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import websocketserver.game.enums.RewardCategory;

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

    public String getMissionDescription() {
        return missionDescription;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void flipCard() {
        this.isFlipped = true;
        this.reward = new Reward(reward.getCategory(), reward.getNumberRockets() - 1); // Decrease rockets by 1 upon flipping (because all rewards in the first map are decreased by 1)
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("missionDescription", missionDescription);
        json.put("newReward", reward.getNumberRockets());
        json.put("flipped", isFlipped);
        return json;
    }

    public static MissionCard fromJson(JSONObject json) throws JSONException {
        String missionDescription = json.getString("missionDescription");
        Reward reward = new Reward(RewardCategory.ROCKET, json.getInt("newReward"));
        MissionCard card = new MissionCard(missionDescription, reward);
        if (json.getBoolean("flipped")) {
            card.flipCard();
        }
        return card;
    }
}
