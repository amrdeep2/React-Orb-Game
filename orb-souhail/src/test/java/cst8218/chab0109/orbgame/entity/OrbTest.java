package cst8218.chab0109.orbgame.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrbTest {

    @Test
    public void testTimeStepMovement() {
        Orb o = new Orb(100, 100, 20, 5, 5);
        o.timeStep();
        assertEquals(105, o.getX());
        assertEquals(105, o.getY());
    }

    @Test
    public void testBounceLeftWall() {
        Orb o = new Orb(0, 100, 20, -5, 0);
        o.timeStep();
        assertEquals(0, o.getX());
        assertTrue(o.getXSpeed() >= 0);
    }

    @Test
    public void testBounceRightWall() {
        Orb o = new Orb(Orb.X_MAX - 20, 100, 20, 5, 0);
        o.timeStep();
        assertEquals(Orb.X_MAX - 20, o.getX());
        assertTrue(o.getXSpeed() <= 0);
    }

    @Test
    public void testSetSpeedDirectlyLimit() {
        Orb o = new Orb(100, 100, 20, 0, 0);
        o.setSpeedDirectly(10000, -10000);
        assertEquals(Orb.MAX_SPEED, o.getXSpeed());
        assertEquals(-Orb.MAX_SPEED, o.getYSpeed());
    }
}
