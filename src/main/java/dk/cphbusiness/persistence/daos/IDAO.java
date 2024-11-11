package dk.cphbusiness.persistence.daos;

import jakarta.persistence.EntityNotFoundException;

import java.util.Set;

/**
 * Purpose: This is an interface for making a DAO (Data Access Object) that can be used to perform CRUD operations on any entity.
 * Author: Thomas Hartmann
 * @param <T>
 */
public interface IDAO<T> {

    T findById(Object id) throws EntityNotFoundException;

    Set<T> getAll();

    T create(T t) throws Exception;

    T update(T t) throws EntityNotFoundException;

    void delete(T t) throws EntityNotFoundException;

}