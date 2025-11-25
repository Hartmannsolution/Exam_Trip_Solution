package dk.cphbusiness.persistence.model;

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
@Entity
@Table(name = "refresh_tokens")
@NamedQueries(@NamedQuery(name = "RefreshToken.deleteAllRows", query = "DELETE from RefreshToken"))
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RefreshToken implements Serializable, IIdProvider<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "token", length = 100)
    private String token;
    @Basic(optional = false)
    @Column(name = "expires")
    private LocalDateTime expires;

    @Basic(optional = false)
    @Column(name = "invalidated")
    private boolean invalidated;

    @Override
    public String getId() {
        return token;
    }
    public RefreshToken(String token) {
        this.token = token;
        this.expires = LocalDateTime.now().plusDays(10); // 10 days till user has to login again
        this.invalidated = false;
    }

    public boolean verifyRefreshToken(String token) {
        System.out.println("this Token: " + this.token);
        System.out.println("that Token: " + token);
        return this.token.equals(token) && !invalidated && expires.isAfter(LocalDateTime.now());
    }

    public void invalidateRefreshToken(String token) {
        if (this.token.equals(token)) {
            invalidated = true;
        }
    }


}
