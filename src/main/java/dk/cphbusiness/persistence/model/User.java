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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
@Entity
@Table(name = "users")
@NamedQueries(@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable, ISecurityUser, IIdProvider<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "username", length = 25)
    private String username;
    @Basic(optional = false)
    @Column(name = "password")
    private String password;

    @JoinTable(name = "user_roles", joinColumns = {
            @JoinColumn(name = "user_name", referencedColumnName = "username")}, inverseJoinColumns = {
            @JoinColumn(name = "role_name", referencedColumnName = "name")})
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    public Set<String> getRolesAsStrings() {
        if (roles.isEmpty()) {
            return null;
        }
        Set<String> rolesAsStrings = new HashSet<>();
        roles.forEach((role) -> {
            rolesAsStrings.add(role.getRoleName());
        });
        return rolesAsStrings;
    }

    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    public User(String userName, String userPass) {
        this.username = userName;
        this.password = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }

    public User(String userName, Set<Role> roleEntityList) {
        this.username = userName;
        this.roles = roleEntityList;
    }

    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    @Override
    public RefreshToken addRefreshToken(String refreshToken) {
        RefreshToken token = new RefreshToken(refreshToken);
        refreshTokens.add(token);
        return token;
    }

    @Override
    public void invalidateRefreshToken(String token) {
        refreshTokens.stream()
                .filter(refreshToken -> refreshToken.getId().equals(token))
                .findFirst()
                .ifPresent(refreshToken -> {
                    refreshToken.invalidateRefreshToken(token);
                });
    }

    public RefreshToken getValidRefreshToken(){
        return refreshTokens.stream()
                .filter(refreshToken -> !refreshToken.isInvalidated() && refreshToken.getExpires().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean verifyRefreshToken(String token) {
        RefreshToken found = this.refreshTokens.stream()
                .filter(refreshToken -> refreshToken.getId().equals(token))
                .findFirst()
                .get();
        return found.verifyRefreshToken(token);
    }

    @Override
    public void invokeRefreshToken(String token) {
        refreshTokens.stream().filter(refreshToken -> refreshToken.getId().equals(token))
                .findFirst()
                .ifPresent(refreshToken -> {
                    refreshToken.invalidateRefreshToken(token);
                });
    }


    public void removeRole(String userRole) {
        roles.stream()
                .filter(role -> role.getRoleName().equals(userRole))
                .findFirst()
                .ifPresent(role -> {
                    roles.remove(role);
                    role.getUsers().remove(this);
                });
    }

    public String getId() {
        return username;
    }
}
