package com.example.onlinecourse.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Hibernate utility class for managing SessionFactory.
 * Provides singleton pattern with lazy initialization to prevent deployment failures.
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static final Object lock = new Object();

    /**
     * Get the SessionFactory instance (lazy initialization).
     * This prevents Hibernate from initializing during deployment.
     *
     * @return SessionFactory instance
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (lock) {
                if (sessionFactory == null) {
                    try {
                        // Build session factory from hibernate.cfg.xml
                        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                                .configure("hibernate.cfg.xml")
                                .build();

                        Metadata metadata = new MetadataSources(standardRegistry)
                                .getMetadataBuilder()
                                .build();

                        sessionFactory = metadata.getSessionFactoryBuilder().build();
                    } catch (Exception e) {
                        System.err.println("SessionFactory creation failed: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Failed to create SessionFactory", e);
                    }
                }
            }
        }
        return sessionFactory;
    }

    /**
     * Close the SessionFactory.
     * Should be called when application shuts down.
     */
    public static void shutdown() {
        synchronized (lock) {
            if (sessionFactory != null) {
                try {
                    sessionFactory.close();
                    sessionFactory = null;
                } catch (Exception e) {
                    System.err.println("Error closing SessionFactory: " + e.getMessage());
                }
            }
        }
    }
}

