package websocketserver.game.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class RocketBarometer {
    private final int MAX_ROCKET = 40;

    @Getter
    private int rocketCount;

    public RocketBarometer() {
        rocketCount = 0;
    }

    public void addRockets(int rockets) {
        if (rockets > 0 && rocketCount + rockets <= MAX_ROCKET) {
            rocketCount += rockets;
        }
    }

    public int getPointsOfRocketBarometer() {
        int result = 0;

        List<Integer> borders = new ArrayList<>(List.of(5, 9, 12, 15, 18, 21, 24, 27, 29, 31));

        for (Integer border : borders) {
            if (rocketCount >= border) {
                result += 15;
            }
        }

        return result;
    }
}