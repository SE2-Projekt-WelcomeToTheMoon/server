package websocketserver.game.model;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import websocketserver.game.enums.MissionType;
import websocketserver.game.enums.RewardCategory;

public class MissionCard {
    @Getter
    private MissionType missionType;
    @Getter
    private Reward reward;
    private boolean isFlipped;

    public MissionCard(MissionType missionType, Reward reward) {
        this.missionType = missionType;
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

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("missionType", missionType.name());
        json.put("newReward", reward.getNumberRockets());
        json.put("flipped", isFlipped);
        return json;
    }

    public static MissionCard fromJson(JSONObject json) throws JSONException {
        MissionType missionType = MissionType.valueOf(json.getString("missionType"));
        Reward reward = new Reward(RewardCategory.ROCKET, json.getInt("newReward"));
        MissionCard card = new MissionCard(missionType, reward);
        if (json.getBoolean("flipped")) {
            card.flipCard();
        }
        return card;
    }
}
