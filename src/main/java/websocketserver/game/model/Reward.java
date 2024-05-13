package websocketserver.game.model;

import websocketserver.game.enums.RewardCategory;
import lombok.Getter;

@Getter
public class Reward {
    private RewardCategory category;
    private int numberRockets;
    private boolean rocketsFilled=false;
    private int unfilledRockets;
    private boolean systemErrorClaimed=false;

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
