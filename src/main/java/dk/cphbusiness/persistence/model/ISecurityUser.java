package dk.cphbusiness.persistence.model;

/**
 * Purpose: to set methods for handling security with the user
 * Author: Thomas Hartmann
 */
public interface ISecurityUser {
    boolean verifyPassword(String pw);
    void addRole(Role role);
    boolean verifyRefreshToken(String token);
    RefreshToken addRefreshToken(String token);
    void invalidateRefreshToken(String token);
    void invokeRefreshToken(String token);
}
