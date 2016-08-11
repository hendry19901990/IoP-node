/*
 * @#FermatEmbeddedNodeServer.java - 2015
 * Copyright Fermat.org., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 */
package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.endpoinsts.clients.FermatWebSocketClientChannelServerEndpoint;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.endpoinsts.nodes.FermatWebSocketNodeChannelServerEndpoint;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.JaxRsActivator;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.security.AdminRestApiSecurityFilter;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.servlets.HomeServlet;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.util.ConfigurationManager;

import org.apache.commons.lang.ClassUtils;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.DispatcherType;
import javax.websocket.server.ServerContainer;
import java.util.EnumSet;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.FermatEmbeddedNodeServer</code>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 13/11/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class FermatEmbeddedNodeServer {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(FermatEmbeddedNodeServer.class));

    /**
     * Represent the APP_NAME
     */
    public static final String APP_NAME = "/fermat";

    /**
     * Represent the WAR_APP_NAME
     */
    public static final String WAR_APP_NAME = "FermatNetworkNode.war";

    /**
     * Represent the DEFAULT_PORT number
     */
    public static final int DEFAULT_PORT = 8080;

    /**
     * Represent the DEFAULT_IP number
     */
    public static final String DEFAULT_IP = "0.0.0.0";

    /**
     * Represent the server instance
     */
    private Server server;

    /**
     * Represent the web socket server container instance
     */
    private ServerContainer wsServerContainer;

    /**
     * Represent the ServletContextHandler instance
     */
    private ServletContextHandler servletContextHandler;

    /**
     * Represent the ServerConnector instance
     */
    private ServerConnector serverConnector;

    /**
     * Constructor
     */
    public FermatEmbeddedNodeServer(){
       super();

        LOG.info("Configure INTERNAL_IP  : " + ConfigurationManager.getValue(ConfigurationManager.INTERNAL_IP));
        LOG.info("Configure PORT: " + ConfigurationManager.getValue(ConfigurationManager.PORT));

    }

    /**
     * Method that create a InternalHandler
     */
//    private class InternalHandler implements HttpHandler {
//
//        @Override
//        public void handleRequest(final HttpServerExchange exchange) throws Exception {
//            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//            exchange.getResponseSender().send("Fermat - Network Node running....");
//        }
//    }

    /**
     * Method that create a HttpHandler for manage the resources of the
     * web app
     */
//    private static HttpHandler createWebAppResourceHandler() {
//
//        final ResourceManager staticResources = new ClassPathResourceManager(FermatEmbeddedNodeServer.class.getClassLoader(),"web");
//        final ResourceHandler resourceHandler = new ResourceHandler(staticResources);
//        resourceHandler.setWelcomeFiles("index.html");
//        return resourceHandler;
//    }

    /**
     * Method that create a HttpHandler for manage the web socket app and the
     * Servlet Handler
     */
//    private HttpHandler createWebSocketAppServletHandler() throws Exception {
//
//        /*
//         * Create and configure the xnioWorker
//
//        final Xnio xnio = Xnio.getInstance("nio", Undertow.class.getClassLoader());
//        final XnioWorker xnioWorker = xnio.createWorker(OptionMap.builder()
////                .set(Options.WORKER_IO_THREADS, Runtime.getRuntime().availableProcessors() * 4)
//                .set(Options.CONNECTION_HIGH_WATER, 1000000)
//                .set(Options.WORKER_TASK_KEEPALIVE, (int)TimeUnit.SECONDS.toMillis(90))
//                .set(Options.CONNECTION_LOW_WATER, 1000000)
//                .set(Options.WORKER_TASK_CORE_THREADS, 128)
//                .set(Options.WORKER_TASK_MAX_THREADS, 512)
//                .set(Options.TCP_NODELAY, true)
////                .set(Options.CORK, true)
//                .getMap());*/
//
//        /*
//         * Create the App WebSocketDeploymentInfo and configure
//         */
//        WebSocketDeploymentInfo appWebSocketDeploymentInfo = new WebSocketDeploymentInfo();
//        //appWebSocketDeploymentInfo.setBuffers(new XnioByteBufferPool(new ByteBufferSlicePool(BufferAllocator.BYTE_BUFFER_ALLOCATOR, 1024, 1024 * 2)));
//        //appWebSocketDeploymentInfo.setWorker(xnioWorker);
//        appWebSocketDeploymentInfo.setDispatchToWorkerThread(Boolean.TRUE);
//        appWebSocketDeploymentInfo.addEndpoint(FermatWebSocketNodeChannelServerEndpoint.class);
//        appWebSocketDeploymentInfo.addEndpoint(FermatWebSocketClientChannelServerEndpoint.class);
//        appWebSocketDeploymentInfo.addExtension(new PerMessageDeflateHandshake());
//
//         /*
//         * Create the App DeploymentInfo and configure
//         */
//        DeploymentInfo appDeploymentInfo = Servlets.deployment();
//        appDeploymentInfo.setClassLoader(FermatEmbeddedNodeServer.class.getClassLoader())
//                        .setContextPath(APP_NAME)
//                        .setDeploymentName(WAR_APP_NAME)
//                        .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, appWebSocketDeploymentInfo)
//                        .addServlets(Servlets.servlet("HomeServlet", HomeServlet.class).addMapping("/home"));
//                        //.addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));
//
//        /*
//         * Deploy the app
//         */
//        DeploymentManager manager = servletContainer.addDeployment(appDeploymentInfo);
//        manager.deploy();
//
//
//        return manager.start();
//    }

    /**
     * Method that create a HttpHandler for manage the Restful app api
     */
//    private HttpHandler createRestAppApiHandler() throws Exception {
//
//        /*
//         * Instantiate the undertowJaxrsServer
//         */
//        UndertowJaxrsServer undertowJaxrsServer = new UndertowJaxrsServer();
//
//        /*
//         * Create the App RestEasyDeployment and configure
//         */
//        ResteasyDeployment restEasyDeploymentInfo = new ResteasyDeployment();
//        restEasyDeploymentInfo.setApplicationClass(JaxRsActivator.class.getName());
//        restEasyDeploymentInfo.setProviderFactory(new ResteasyProviderFactory());
//
//        //restEasyDeploymentInfo.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
//
//        /*
//         * Create the restAppDeploymentInfo and configure
//         */
//        DeploymentInfo restAppDeploymentInfo = undertowJaxrsServer.undertowDeployment(restEasyDeploymentInfo, "/rest/api/v1");
//        restAppDeploymentInfo.setClassLoader(FermatEmbeddedNodeServer.class.getClassLoader())
//                             .setContextPath(APP_NAME)
//                             .setDeploymentName("FermatRestApi.war");
//                           //  .addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));
//
//
//        /*
//         * Deployment Security Filters
//         */
//
//        //Filter for the admin zone is apply to the all request to the web app of the node
//        FilterInfo filter = Servlets.filter("AdminSecurityFilter", AdminRestApiSecurityFilter.class);
//        restEasyDeploymentInfo.getProviderFactory().register(filter);
//        restAppDeploymentInfo.addFilter(filter);
//        restAppDeploymentInfo.addFilterUrlMapping("AdminSecurityFilter", "/rest/api/v1/admin/*", DispatcherType.REQUEST);
//
//        /*
//         * Deploy the app
//         */
//        DeploymentManager manager = servletContainer.addDeployment(restAppDeploymentInfo);
//        manager.deploy();
//
//        return manager.start();
//    }

    /**
     * Method tha configure the server
     * @throws Exception
     */
    private void configure() throws Exception {

//        serverBuilder.setHandler(Handlers.path()
//                        .addPrefixPath("/", createWebAppResourceHandler())
//                        .addPrefixPath("/api", new InternalHandler())
//                        .addPrefixPath(APP_NAME+"/ws", createWebSocketAppServletHandler())
//                        .addPrefixPath(APP_NAME, createRestAppApiHandler())
//        );
//
//        serverBuilder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);
       // serverBuilder.setServerOption(UndertowOptions.IDLE_TIMEOUT, 22000);

//        this.server = serverBuilder.build();

          /*
         * Create and configure the server
         */
        this.server = new Server();
        this.serverConnector = new ServerConnector(server);

        String port = ConfigurationManager.getValue(ConfigurationManager.PORT);

        this.serverConnector.setPort(new Integer(port.trim()));
        this.server.addConnector(serverConnector);

         /*
         * Setup the basic application "context" for this application at "/fermat"
         */
        this.servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        this.servletContextHandler.setContextPath("/fermat");
        this.servletContextHandler.setClassLoader(FermatEmbeddedNodeServer.class.getClassLoader());
        this.server.setHandler(servletContextHandler);

        /*
         * Initialize web layer
         */
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/fermat");
        webAppContext.setDescriptor("./web/WEB-INF/web.xml");
        webAppContext.setResourceBase("./web");
        webAppContext.addBean(new ServletContainerInitializersStarter(webAppContext), true);
        webAppContext.setWelcomeFiles(new String[]{"index.html"});
        webAppContext.addFilter(AdminRestApiSecurityFilter.class, "/rest/api/v1/admin/*", EnumSet.of(DispatcherType.REQUEST));
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        server.setHandler(webAppContext);

        /*
         * Initialize restful service layer
         */
        ServletHolder restfulServiceServletHolder = new ServletHolder(new HttpServlet30Dispatcher());
        restfulServiceServletHolder.setInitParameter("javax.ws.rs.Application", JaxRsActivator.class.getName());
        restfulServiceServletHolder.setInitParameter("resteasy.use.builtin.providers", "true");
        webAppContext.addServlet(restfulServiceServletHolder, "/rest/api/v1/*");

        /*
         * Initialize javax.websocket layer
         */
        this.wsServerContainer = WebSocketServerContainerInitializer.configureContext(webAppContext);

        /*
         * Add WebSocket endpoint to javax.websocket layer
         */
        this.wsServerContainer.addEndpoint(FermatWebSocketNodeChannelServerEndpoint.class);
        this.wsServerContainer.addEndpoint(FermatWebSocketClientChannelServerEndpoint.class);

        this.server.dump(System.err);


    }


    /**
     * Method tha initialize and start the
     * Embedded server
     */
    public void start() throws Exception {

        configure();
        server.start();
        server.join();

        LOG.info("***********************************************************");
        LOG.info("NODE SERVER LISTENING   : " + ConfigurationManager.getValue(ConfigurationManager.INTERNAL_IP) + " : " + ConfigurationManager.getValue(ConfigurationManager.PORT));
        LOG.info("***********************************************************");

    }
}
