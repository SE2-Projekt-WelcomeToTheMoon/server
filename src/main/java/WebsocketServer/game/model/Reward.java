package WebsocketServer.game.model;

import WebsocketServer.game.enums.RewardCategory;

public class Reward {
    public RewardCategory category;
    public int numberRockets;
    public boolean rocketsFilled;

    public Reward(RewardCategory category) {
        this.category = category;
        this.rocketsFilled=false;
    }

    public Reward(RewardCategory category,  int numberRockets) {
        this.category = category;
        this.numberRockets = numberRockets;
        this.rocketsFilled=true;
    }

    public Reward(RewardCategory category,  int numberRockets, boolean rocketsFilled) {
        this.category = category;
        this.numberRockets = numberRockets;
        this.rocketsFilled = rocketsFilled;
    }

}
