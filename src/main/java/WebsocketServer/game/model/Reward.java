package WebsocketServer.game.model;

import WebsocketServer.game.enums.RewardCategory;

public class Reward {
    public RewardCategory category;
    public int numberRockets;
    public boolean rocketsFilled=false;
    public int unfilledRockets;
    public boolean systemErrorClaimed=false;

    public Reward(RewardCategory category) {
        if(category==null)throw new IllegalArgumentException("Reward Category may not be null");
        this.category = category;

    }

    public Reward(RewardCategory category,  int numberRockets) {
        if(category==null)throw new IllegalArgumentException("Reward Category may not be null");
        this.category = category;
        if(category.equals(RewardCategory.ROCKET))this.numberRockets = numberRockets;
        if(category.equals(RewardCategory.UNFILLEDROCKET))this.unfilledRockets=numberRockets;

    }
    public void fillRockets(){
        this.rocketsFilled=true;
    }
    public void claimSystemError(){
        this.systemErrorClaimed=true;
    }
}
