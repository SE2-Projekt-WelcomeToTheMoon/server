package WebsocketServer.game.model;

import WebsocketServer.game.enums.RewardCategory;

public class Reward {
    public RewardCategory category;
    public int numberRockets;
    public boolean rocketsFilled;
    public int unfilledRockets;

    public Reward(RewardCategory category) {
        this.category = category;
        this.rocketsFilled=false;
    }

    public Reward(RewardCategory category,  int numberRockets) {
        this.category = category;
        if(category.equals(RewardCategory.ROCKET))this.numberRockets = numberRockets;
        if(category.equals(RewardCategory.UNFILLEDROCKET))this.unfilledRockets=numberRockets;
        this.rocketsFilled=false;
    }

}
