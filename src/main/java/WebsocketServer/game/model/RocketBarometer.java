package WebsocketServer.game.model;

import lombok.Getter;

public class RocketBarometer {
    private final int MAX_ROCKET = 40;
    private final int ROCKETS_TO_COMPLETE = 32;
    @Getter
    private int rocketCount;

    public RocketBarometer(){
        rocketCount = 0;
    }

    public boolean addRockets(int rockets){
        if(rockets > 0){
            if(rocketCount + rockets <= MAX_ROCKET){
                rocketCount += rockets;
            }
        }
        return hasWon();
    }

    public boolean hasWon() {
        return rocketCount >= ROCKETS_TO_COMPLETE;
    }

    public int getPointsOfRocketBarometer(){
        int result;

        if( rocketCount < 5) result = 0;
        else if( rocketCount < 9) result = 15;
        else if( rocketCount < 12) result = 30;
        else if( rocketCount < 15) result = 45;
        else if( rocketCount < 18) result = 60;
        else if( rocketCount < 21) result = 75;
        else if( rocketCount < 24) result = 90;
        else if( rocketCount < 27) result = 105;
        else if( rocketCount < 29) result = 120;
        else if( rocketCount < 31) result = 135;
        else result = 150;

        return  result;
    }
}
