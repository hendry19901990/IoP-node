/*
 * @#DatabaseManager.java - 2016
 * Copyright Fermat.org, All rights reserved.
 */
package org.iop.version_1.structure.database.jpa;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.iop.version_1.structure.database.jpa.entities.*;
import org.iop.version_1.structure.util.ProviderResourcesFilesPath;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.ProtectionDomain;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.DatabaseManager</code> are the
 * database manager class
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 22/07/16
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class DatabaseManager {

    /**
     * Represent the LOG
     */
    private static final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(DatabaseManager.class));

    /**
     * Represent the value of DIR_NAME
     */
    public static final String DIR_NAME = "database";

    /**
     * Represent the value of DIR_NAME
     */
    public static final String DATA_BASE_NAME = "network_node.odb";

    /**
     * Represent the value of CONNECTION_URL
     */
    public static final String CONNECTION_URL = "objectdb://localhost/"+DATA_BASE_NAME+";user=admin;password=admin";

    /**
     * Represent the entityManagerFactory instance
     */
    private static EntityManagerFactory entityManagerFactory;

    /**
     * Represent the executorService instance
     */
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Get a new instance to a Entity manager that
     * represent a connection with the data base.
     *
     * @return EntityManager
     */
    public static EntityManager getConnection() {

        if (entityManagerFactory != null){
            return entityManagerFactory.createEntityManager();
        }else {
            throw new RuntimeException("Cant get Connection, entityManagerFactory = "+ null);
        }

    }

    /**
     * Close de data base
     */
    public static void closeDataBase(){

        if (entityManagerFactory != null && entityManagerFactory.isOpen()){
            entityManagerFactory.close();
        }
    }

    /**
     * Get the EntityManagerFactory value
     *
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    /**
     * Get the path of the objectdb.jar
     * @return path
     */
    public static String getObjectDbJarPath(){
        ProtectionDomain domain = com.objectdb.Utilities.class.getProtectionDomain();
        return domain.getCodeSource().getLocation().getFile();
    }

    /**
     * Get the path of the objectdb configuration
     * @return path
     */
    public static String getObjectDbConfigurationFilePath(){
        ProtectionDomain domain = com.objectdb.Utilities.class.getProtectionDomain();
        return domain.getCodeSource().getLocation().getFile().replace(".jar", ".conf");
    }

    public static void start(){

        /*
         * Configure environment
         */
        String path = ProviderResourcesFilesPath.createNewFilesPath(DIR_NAME);
        System.setProperty("objectdb.conf", getObjectDbConfigurationFilePath());

        executorService.execute(() -> {

            LOG.info("Initializing objectdb database in server mode");
            try {
                Runtime.getRuntime().exec("java -Dobjectdb.temp.avoid-page-recycle=true -Dobjectdb.home="+path+" -cp "+ getObjectDbJarPath() +" com.objectdb.Server start");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        try {
            System.out.println("Waiting 5 seconds to the database server start");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOG.warn(e);
        }

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("javax.jdo.option.MinPool", "50");
        properties.put("javax.jdo.option.MaxPool", "100");
        properties.put("javax.persistence.sharedCache.mode", "DISABLE_SELECTIVE");

        LOG.info("Open a database connection (create a new database if it doesn't exist yet)");
        entityManagerFactory = Persistence.createEntityManagerFactory("node-pu");

        /*
         * Create tables at start up
         */
        entityManagerFactory.createEntityManager().getMetamodel().entity(ActorCatalog.class);
        entityManagerFactory.createEntityManager().getMetamodel().entity(Client.class);
        entityManagerFactory.createEntityManager().getMetamodel().entity(GeoLocation.class);
        entityManagerFactory.createEntityManager().getMetamodel().entity(NetworkService.class);
        entityManagerFactory.createEntityManager().getMetamodel().entity(NodeCatalog.class);

    }
}
