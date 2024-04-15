package WebsocketServer.game.model;

import WebsocketServer.game.enums.RewardCategory;

public class Reward {
    public RewardCategory category;
    public Floor floor;
    public Chamber chamber;
    public int numberRockets;
    public boolean rocketsFilled;

    public Reward(RewardCategory category, Floor floor, Chamber chamber) {
        this.category = category;
        this.floor = floor;
        this.chamber = chamber;
    }

    public Reward(RewardCategory category, Floor floor, Chamber chamber, int numberRockets) {
        this.category = category;
        this.floor = floor;
        this.chamber = chamber;
        this.numberRockets = numberRockets;
    }

    public Reward(RewardCategory category, Floor floor, Chamber chamber, int numberRockets, boolean rocketsFilled) {
        this.category = category;
        this.floor = floor;
        this.chamber = chamber;
        this.numberRockets = numberRockets;
        this.rocketsFilled = rocketsFilled;
    }

}
