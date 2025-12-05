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
@Entity
@Table(name = "users")
public class UserAccount implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String userid;

    @Column(nullable=false, length=512)
    private String password;

    @Column(nullable=false)
    private String roleName;

    public Long getId() { return id; }
    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getPassword() { return ""; }    // don’t expose hash

    public void setPassword(String pw) {
        if (pw == null || pw.isBlank()) return;
        var hash = CDI.current().select(Pbkdf2PasswordHash.class).get();
        hash.initialize(new HashMap<>());
        this.password = hash.generate(pw.toCharArray());
    }
}
