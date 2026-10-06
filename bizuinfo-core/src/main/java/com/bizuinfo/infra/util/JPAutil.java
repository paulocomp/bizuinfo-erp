package com.bizuinfo.infra.util;

import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class JPAutil {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bizuinfo-pu");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
