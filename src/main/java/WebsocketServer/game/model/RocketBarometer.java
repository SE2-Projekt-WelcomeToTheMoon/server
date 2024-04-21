package WebsocketServer.game.model;

public class RocketBarometer {
    private final int MAX_ROCKET = 40;
    private int rocketCount;

    public RocketBarometer(){
        rocketCount = 0;
    }

    public void addRockets(int rockets){
        rocketCount += rockets;
    }

    public int getPointsOfRocketBarometer(){
        int result = 0;

        if(rocketCount >= 0 && rocketCount < 5){
            result = 0;
        }else if(rocketCount >= 5 && rocketCount < 9){
            result = 15;
        }else if(rocketCount >= 9 && rocketCount < 12){
            result = 30;
        }else if(rocketCount >= 12 && rocketCount < 15){
            result = 45;
        }else if(rocketCount >= 15 && rocketCount < 18){
            result = 60;
        }else if(rocketCount >= 18 && rocketCount < 21){
            result = 75;
        }else if(rocketCount >= 21 && rocketCount < 24){
            result = 90;
        }else if(rocketCount >= 24 && rocketCount < 27){
            result = 105;
        }else if(rocketCount >= 27 && rocketCount < 29){
            result = 120;
        }else if(rocketCount >= 29 && rocketCount < 31){
            result = 135;
        }else{
            result = 150;
        }

        return  result;
    }
}
