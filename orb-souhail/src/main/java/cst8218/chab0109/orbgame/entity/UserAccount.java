package cst8218.chab0109.orbgame.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.security.enterprise.identitystore.PasswordHash;
import java.io.Serializable;
import java.util.HashMap;

/**
 * JPA Entity representing a user account stored in the "users" table.
 * 
 * This entity includes fields for:
 *  - id: primary key
 *  - userid: username (unique)
 *  - password: hashed password
 *  - roleName: user role (e.g., admin, user)
 * 
 * The password is securely hashed using PBKDF2 and never returned in plain text.
 */
@Entity
@Table(name = "users")
public class UserAccount implements Serializable {

    /** Primary key with auto-increment (IDENTITY) */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username column, cannot be null and must be unique */
    @Column(nullable=false, unique=true)
    private String userid;

    /** Hashed password stored in DB; 512 chars is enough for PBKDF2 hash */
    @Column(nullable=false, length=512)
    private String password;

    /** Role name (e.g., "admin", "user") used for authorization */
    @Column(nullable=false)
    private String roleName;

    /** Returns database ID */
    public Long getId() { return id; }

    /** Returns username */
    public String getUserid() { return userid; }

    /** Sets username */
    public void setUserid(String userid) { this.userid = userid; }

    /** Returns role name */
    public String getRoleName() { return roleName; }

    /** Sets role name */
    public void setRoleName(String roleName) { this.roleName = roleName; }

    /**
     * Getter intentionally DOES NOT return the password hash.
     * Always return an empty string to avoid exposing secure data.
     */
    public String getPassword() { return ""; }    // don’t expose hash

    /**
     * Sets password by hashing it using PBKDF2.
     *
     * - If pw is blank, do nothing (prevents overwriting existing hash).
     * - CDI is used to obtain Pbkdf2PasswordHash instance.
     * - Hash is generated and stored instead of plain password.
     */
    public void setPassword(String pw) {
        if (pw == null || pw.isBlank()) return;

        // Obtain PBKDF2 password hasher from CDI
        var hash = CDI.current().select(Pbkdf2PasswordHash.class).get();

        // Initialize hasher with default parameters
        hash.initialize(new HashMap<>());

        // Generate hash and store it
        this.password = hash.generate(pw.toCharArray());
    }
}
