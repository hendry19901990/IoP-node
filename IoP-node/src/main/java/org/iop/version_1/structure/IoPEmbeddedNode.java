package org.iop.version_1.structure;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.util.Headers;
import io.undertow.websockets.extensions.PerMessageDeflateHandshake;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.apache.commons.lang.ClassUtils;
import org.iop.version_1.structure.channels.endpoinsts.clients.FermatWebSocketClientChannelServerEndpoint;
import org.iop.version_1.structure.conf.EmbeddedNodeServerConf;
import org.iop.version_1.structure.servlets.HomeServlet;
import org.iop.version_1.structure.util.ConfigurationManager;
import org.jboss.logging.Logger;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import java.util.concurrent.TimeUnit;

import static org.iop.version_1.structure.conf.EmbeddedNodeServerConf.APP_NAME;
import static org.iop.version_1.structure.conf.EmbeddedNodeServerConf.WAR_APP_NAME;

/**
 * Created by mati on 11/08/16.
 */
public class IoPEmbeddedNode {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(IoPEmbeddedNode.class));

    /**
     * Represent the serverBuilder instance
     */
    private final Undertow.Builder serverBuilder;

    /**
     * Represent the servletContainer instance
     */
    private final ServletContainer servletContainer;

    /**
     * Represent the server instance
     */
    private Undertow server;

    /**
     * Constructor
     */
    public IoPEmbeddedNode(){
        super();

        LOG.info("Configure INTERNAL_IP  : " + ConfigurationManager.getValue(ConfigurationManager.INTERNAL_IP));
        LOG.info("Configure PORT: " + ConfigurationManager.getValue(ConfigurationManager.PORT));

        this.serverBuilder = Undertow.builder().addHttpListener(Integer.valueOf(ConfigurationManager.getValue(ConfigurationManager.PORT)), ConfigurationManager.getValue(ConfigurationManager.INTERNAL_IP));
        this.servletContainer = Servlets.defaultContainer();
    }


    /**
     * Method tha initialize and start the
     * Embedded server
     */
    public void start() throws Exception {

        configure();
        server.start();

        LOG.info("***********************************************************");
        LOG.info("NODE SERVER LISTENING   : " + ConfigurationManager.getValue(ConfigurationManager.INTERNAL_IP) + " : " + ConfigurationManager.getValue(ConfigurationManager.PORT));
        LOG.info("***********************************************************");

    }

    /**
     * Method tha configure the server
     * @throws Exception
     */
    private void configure() throws Exception {

        serverBuilder.setHandler(Handlers.path()
//                .addPrefixPath("/", createWebAppResourceHandler())
                .addPrefixPath("/api", new InternalHandler())
                .addPrefixPath(APP_NAME+"/ws", createWebSocketAppServletHandler())
//                .addPrefixPath(APP_NAME, createRestAppApiHandler())
        );

        serverBuilder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);
        // serverBuilder.setServerOption(UndertowOptions.IDLE_TIMEOUT, 22000);

        this.server = serverBuilder.build();
    }

    /**
     * Method that create a InternalHandler
     */
    private class InternalHandler implements HttpHandler {

        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("IoP - Network Node running....");
        }
    }

    /**
     * Method that create a HttpHandler for manage the web socket app and the
     * Servlet Handler
     */
    private HttpHandler createWebSocketAppServletHandler() throws Exception {

        /*
         * Create and configure the xnioWorker
        */
        final Xnio xnio = Xnio.getInstance("nio", Undertow.class.getClassLoader());
        final XnioWorker xnioWorker = xnio.createWorker(OptionMap.builder()
//                .set(Options.WORKER_IO_THREADS, Runtime.getRuntime().availableProcessors() * 4)
                .set(Options.WORKER_IO_THREADS,25)
                .set(Options.CONNECTION_HIGH_WATER, 1000000)
                .set(Options.WORKER_TASK_KEEPALIVE, (int) TimeUnit.SECONDS.toMillis(90))
                .set(Options.CONNECTION_LOW_WATER, 1000000)
                .set(Options.WORKER_TASK_CORE_THREADS, 100)
                .set(Options.WORKER_TASK_MAX_THREADS, 125)
                .set(Options.TCP_NODELAY, true)
//                .set(Options.CORK, true)
                .getMap());




        /*
         * Create the App WebSocketDeploymentInfo and configure
         */
        WebSocketDeploymentInfo appWebSocketDeploymentInfo = new WebSocketDeploymentInfo();
        //appWebSocketDeploymentInfo.setBuffers(new XnioByteBufferPool(new ByteBufferSlicePool(BufferAllocator.BYTE_BUFFER_ALLOCATOR, 1024, 1024 * 2)));
        //appWebSocketDeploymentInfo.setWorker(xnioWorker);
        appWebSocketDeploymentInfo.setDispatchToWorkerThread(Boolean.TRUE);
//        appWebSocketDeploymentInfo.addEndpoint(FermatWebSocketNodeChannelServerEndpoint.class);
        appWebSocketDeploymentInfo.addEndpoint(FermatWebSocketClientChannelServerEndpoint.class);
        appWebSocketDeploymentInfo.addExtension(new PerMessageDeflateHandshake());

         /*
         * Create the App DeploymentInfo and configure
         */
        DeploymentInfo appDeploymentInfo = Servlets.deployment();
        appDeploymentInfo.setClassLoader(IoPEmbeddedNode.class.getClassLoader())
                .setContextPath(APP_NAME)
                .setDeploymentName(WAR_APP_NAME)
                .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, appWebSocketDeploymentInfo)
                .addServlets(Servlets.servlet("HomeServlet", HomeServlet.class).addMapping("/home"));
        //.addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));

        /*
         * Deploy the app
         */
        DeploymentManager manager = servletContainer.addDeployment(appDeploymentInfo);
        manager.deploy();


        return manager.start();
    }


}
