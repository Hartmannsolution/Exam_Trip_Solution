package dk.cphbusiness.security;
import dk.bugelhartmann.UserDTO;
import dk.cphbusiness.persistence.model.User;
import dk.cphbusiness.exceptions.ValidationException;

/**
 * Purpose: To handle security with the database
 * Author: Thomas Hartmann
 */
public interface ISecurityDAO {
    UserDTO getVerifiedUser(String username, String password) throws ValidationException;
    User createUser(String username, String password);
    User addRoleToUser(String username, String role);
    User removeRoleFromUser(String username, String role);
}
