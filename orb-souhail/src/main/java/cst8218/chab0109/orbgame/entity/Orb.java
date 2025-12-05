package cst8218.chab0109.orbgame.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Entity class representing an Orb in the Bouncing Orb Game.
 * Each Orb has position (x, y), size, and velocity (xSpeed, ySpeed).
 * Orbs move in straight lines, bounce off walls, and experience decay on bouncing.
 * Wrapper types are used to allow nulls for REST API support.
 */
/*
 * Name : Souhail chabli
 * Student id: 041124852
 * course:CST8218 
 * LAb section:302
*/
@Entity
public class Orb implements Serializable {

    private static final long serialVersionUID = 1L;

    // -------------------- Constants --------------------
    public static final int X_MAX = 800;         // Maximum X coordinate
    public static final int Y_MAX = 600;         // Maximum Y coordinate
    public static final int INITIAL_SIZE = 20;   // Default Orb size
    public static final int SIZE_MAX = 100;      // Maximum allowed size
    public static final int MAX_SPEED = 500;      // Maximum absolute speed
    public static final int DECAY_RATE = 1;      // Speed decay on bouncing

    // -------------------- Properties --------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Orb ID, auto-generated, non-editable

    @NotNull
    @Min(0)
    @Max(X_MAX)
    private Integer x; // X coordinate

    @NotNull
    @Min(0)
    @Max(Y_MAX)
    private Integer y; // Y coordinate
//hello
    @NotNull
    @Min(1)
    @Max(SIZE_MAX)
    private Integer size = INITIAL_SIZE; // Orb size

    @NotNull
    @Min(-MAX_SPEED)
    @Max(MAX_SPEED)
    private Integer xSpeed; // Speed along X axis (can be negative)

    @NotNull
    @Min(-MAX_SPEED)
    @Max(MAX_SPEED)
    private Integer ySpeed; // Speed along Y axis (can be negative)

    // -------------------- Constructors --------------------
    public Orb() {
        // Default constructor required by JPA
    }

    public Orb(Integer x, Integer y, Integer size, Integer xSpeed, Integer ySpeed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    // -------------------- Getters & Setters --------------------
    public Long getId() {
        return id; // no setter -> id is non-editable
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getXSpeed() {
        return xSpeed;
    }

    public void setXSpeed(Integer xSpeed) {
        this.xSpeed = xSpeed;
    }

    public Integer getYSpeed() {
        return ySpeed;
    }

    public void setYSpeed(Integer ySpeed) {
        this.ySpeed = ySpeed;
    }

    // -------------------- Methods --------------------

    /**
     * Updates the properties to simulate one unit of time.
     * Moves the orb and handles bouncing off walls with decay.
     */
    public void timeStep() {
       // System.out.println("  Orb.timeStep() START - ID: " + id + 
                    //     ", x: " + x + ", y: " + y + 
                    //     ", xSpeed: " + xSpeed + ", ySpeed: " + ySpeed);
        //
        // Move the orb
        x += xSpeed;
        y += ySpeed;
       // System.out.println("  After movement - x: " + x + ", y: " + y);

        // Bounce off left wall
        if (x <= 0 && xSpeed < 0) {
            //System.out.println("  Bouncing off LEFT wall");
            x = 0;
            xSpeed = -xSpeed;  // First reverse direction
            xSpeed = Math.max(0, xSpeed - DECAY_RATE);  // Then apply decay
            //System.out.println("  After left bounce - xSpeed: " + xSpeed);
        }

        // Bounce off top wall
        if (y <= 0 && ySpeed < 0) {
           // System.out.println("  Bouncing off TOP wall");
            y = 0;
            ySpeed = -ySpeed;  // First reverse direction  
            ySpeed = Math.max(0, ySpeed - DECAY_RATE);  // Then apply decay
            //System.out.println("  After top bounce - ySpeed: " + ySpeed);
        }

        // Bounce off right wall
        if (x >= X_MAX - size && xSpeed > 0) {
           // System.out.println("  Bouncing off RIGHT wall");
            x = X_MAX - size;
            xSpeed = -xSpeed;  // First reverse direction
            // For negative speeds, we need to make them less negative
            if (xSpeed < 0) {
                xSpeed = Math.min(0, xSpeed + DECAY_RATE);
            }
           // System.out.println("  After right bounce - xSpeed: " + xSpeed);
        }

        // Bounce off bottom wall
        if (y >= Y_MAX - size && ySpeed > 0) {
            //System.out.println("  Bouncing off BOTTOM wall");
            y = Y_MAX - size;
            ySpeed = -ySpeed;  // First reverse direction
            // For negative speeds, we need to make them less negative
            if (ySpeed < 0) {
                ySpeed = Math.min(0, ySpeed + DECAY_RATE);
            }
           // System.out.println("  After bottom bounce - ySpeed: " + ySpeed);
        }
        
       // System.out.println("  Orb.timeStep() END - ID: " + id + 
                 //        ", x: " + x + ", y: " + y + 
                  //       ", xSpeed: " + xSpeed + ", ySpeed: " + ySpeed);
    }

    @Override
    public String toString() {
        return "Orb{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", size=" + size +
                ", xSpeed=" + xSpeed +
                ", ySpeed=" + ySpeed +
                '}';
    }
    // Add this method to your Orb class to debug the JSON mapping
public void debugValues() {
    System.out.println("=== ORB DEBUG ===");
    System.out.println("id: " + id);
    System.out.println("x: " + x);
    System.out.println("y: " + y); 
    System.out.println("size: " + size);
    System.out.println("xSpeed: " + xSpeed);
    System.out.println("ySpeed: " + ySpeed);
    System.out.println("=== END DEBUG ===");
}
public void setSpeedDirectly(Integer xSpeed, Integer ySpeed) {
    this.xSpeed = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, xSpeed));
    this.ySpeed = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, ySpeed));
}

}