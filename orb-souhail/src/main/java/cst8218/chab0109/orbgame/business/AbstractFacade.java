/*
 * Copyright (c), Eclipse Foundation, Inc. and its licensors.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v1.0, which is available at
 * https://www.eclipse.org/org/documents/edl-v10.php
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package cst8218.chab0109.orbgame.business;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Generic JPA façade providing common CRUD operations for any entity.
 * 
 * @param <T> The entity type handled by this façade.
 *
 * This class is extended by specific façade classes (e.g., OrbFacade)
 * so they automatically inherit standard database operations.
 */
public abstract class AbstractFacade<T> {

    /** The class type of the entity managed by the façade */
    private Class<T> entityClass;

    /**
     * Initializes the façade with the given entity class.
     *
     * @param entityClass The JPA entity type (e.g., Orb.class).
     */
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Must be implemented by subclasses to return the correct EntityManager.
     * 
     * Example:
     * @PersistenceContext(unitName="myPU")
     * private EntityManager em;
     *
     * @Override
     * protected EntityManager getEntityManager() { return em; }
     *
     * @return EntityManager used for JPA persistence.
     */
    protected abstract EntityManager getEntityManager();

    /**
     * Persists a new entity into the database.
     *
     * @param entity The entity instance to save.
     */
    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    /**
     * Updates an existing entity in the database.
     * Uses merge() to attach a detached entity to the persistence context.
     *
     * @param entity The modified entity object.
     */
    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    /**
     * Removes an entity from the database.
     * merge() is used to ensure the entity is managed before removal.
     *
     * @param entity The entity object to delete.
     */
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     * Finds an entity by its primary key.
     *
     * @param id The primary key value.
     * @return The matching entity, or null if none exists.
     */
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Retrieves all records of the entity from the database.
     *
     * @return A List of all entities of type T.
     */
    public List<T> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Retrieves a specific range of entity records.
     * Useful for pagination (e.g., show items 10–20).
     *
     * @param range An int array [start, end)
     * @return A List of entities in the given range.
     */
    public List<T> findRange(int[] range) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     * Counts total number of entity records in the database.
     *
     * @return Integer count of rows.
     */
    public int count() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
