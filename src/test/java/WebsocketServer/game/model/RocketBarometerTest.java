package WebsocketServer.game.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RocketBarometerTest {
    private RocketBarometer barometer;

    @BeforeEach
    void setUp() {
        barometer = new RocketBarometer();
    }

    @Test
    void testNegativeRocketCount() {
        barometer.addRockets(-5);
        assertEquals(0, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testGetRocketCount() {
        assertEquals(0, barometer.getRocketCount());
    }

    @Test
    void testOverflowRocketCount() {
        barometer.addRockets(41);
        assertEquals(0, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testHasWonPositive() {
        barometer.addRockets(34);
        assertTrue(barometer.hasWon());
    }

    @Test
    void testHasWonNegative() {
        barometer.addRockets(31);
        assertFalse(barometer.hasWon());
    }


    @Test
    void testInitialRocketCount() {
        assertEquals(0, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testLowRocketCount() {
        barometer.addRockets(5);
        assertEquals(15, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testMediumRocketCount() {
        barometer.addRockets(9);
        assertEquals(30, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testModeratelyHighRocketCount() {
        barometer.addRockets(12);
        assertEquals(45, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testHighRocketCount() {
        barometer.addRockets(15);
        assertEquals(60, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testVeryHighRocketCount() {
        barometer.addRockets(18);
        assertEquals(75, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testUpperMediumRocketCount() {
        barometer.addRockets(21);
        assertEquals(90, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testUpperHighRocketCount() {
        barometer.addRockets(24);
        assertEquals(105, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testPeakRocketCount() {
        barometer.addRockets(27);
        assertEquals(120, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testNearlyMaxRocketCount() {
        barometer.addRockets(29);
        assertEquals(135, barometer.getPointsOfRocketBarometer());
    }

    @Test
    void testMaximumOrExceededRocketCount() {
        barometer.addRockets(31);
        assertEquals(150, barometer.getPointsOfRocketBarometer());
    }
}