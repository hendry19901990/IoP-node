package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.EventManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair;
import com.bitdubai.fermat_api.layer.all_definition.enums.*;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.Broadcaster;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.FermatBundle;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationManager;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationUtil;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.exceptions.CantRegisterProfileException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkClientCall;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkClientConnection;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkClientManager;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.P2PLayerManager;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.DiscoveryQueryParameters;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.ActorListMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.agents.NetworkServicePendingMessagesSupervisorAgent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.constants.NetworkServiceDatabaseConstants;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.daos.QueriesDao;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceMessage;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceQuery;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.*;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.factories.NetworkServiceDatabaseFactory;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.enums.QueryStatus;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.enums.QueryTypes;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantInitializeIdentityException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantInitializeNetworkServiceProfileException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantSendMessageException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.factories.NetworkServiceMessageFactory;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.interfaces.NetworkService;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.structure.NetworkServiceConnectionManager2;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.NetworkServiceProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.MessageContentType;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.CommunicationChannels;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.MessagesStatus;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatMessagesStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The class <code>com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractNetworkService</code>
 * implements the basic functionality of a network service component and define its behavior.<p/>
 *
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 02/05/2016.
 *
 * @author  lnacosta
 * @version 1.0
 * @since   Java JDK 1.7
 */
public abstract class AbstractNetworkService2 extends AbstractPlugin implements NetworkService {

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM   , layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    protected EventManager eventManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM          , addon = Addons.DEVICE_LOCATION)
    protected LocationManager locationManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM          , addon = Addons.PLUGIN_DATABASE_SYSTEM)
    protected PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_BROADCASTER_SYSTEM)
    protected Broadcaster broadcaster;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM          , addon = Addons.PLUGIN_FILE_SYSTEM)
    protected PluginFileSystem pluginFileSystem;

    @NeededPluginReference(platform = Platforms.COMMUNICATION_PLATFORM, layer = Layers.COMMUNICATION, plugin = Plugins.NETWORK_CLIENT)
    protected NetworkClientManager networkClientManager;

    //todo: esto va por ahora, más adelante se saca si o si
    @NeededPluginReference(platform = Platforms.COMMUNICATION_PLATFORM, layer = Layers.COMMUNICATION, plugin = Plugins.P2P_LAYER)
    private P2PLayerManager p2PLayerManager;

    /**
     * Represents the EVENT_SOURCE
     */
    public EventSource eventSource;

    /**
     * Represents the identity
     */
    private ECCKeyPair identity;

    /**
     * Represents the network Service Type
     */
    private NetworkServiceType networkServiceType;

    /**
     * Represents the network service profile.
     */
    private NetworkServiceProfile profile;

    /**
     * Represents the dataBase
     */
    private Database networkServiceDatabase;

    private QueriesDao queriesDao;

    /**
     * Represents the registered
     */
    private boolean registered;

    /**
     * Holds the listeners references
     */
    protected List<FermatEventListener> listenersAdded;

    /**
     * Represents the networkServiceConnectionManager
     */
    private NetworkServiceConnectionManager2 networkServiceConnectionManager;

    /**
     * AGENTS DEFINITION ----->
     */

    /**
     * Represents the NetworkServicePendingMessagesSupervisorAgent
     */
    private NetworkServicePendingMessagesSupervisorAgent networkServicePendingMessagesSupervisorAgent;

    /**
     * Constructor with parameters
     *
     * @param pluginVersionReference
     * @param eventSource
     * @param networkServiceType
     */
    public AbstractNetworkService2(final PluginVersionReference pluginVersionReference,
                                   final EventSource            eventSource           ,
                                   final NetworkServiceType     networkServiceType    ) {

        super(pluginVersionReference);

        this.eventSource           = eventSource;
        this.networkServiceType    = networkServiceType;

        this.registered            = Boolean.FALSE;
        this.listenersAdded        = new CopyOnWriteArrayList<>();
    }

    /**
     * (non-javadoc)
     * @see AbstractPlugin#start()
     */
    @Override
    public synchronized final void start() throws CantStartPluginException {

        /*
         * Validate required resources
         */
//        validateInjectedResources();

        try {

            /*
             * Initialize the identity
             */
            initializeIdentity();

            /*
             * Initialize the profile
             */
            initializeProfile();

            /*
             * Initialize the data base
             */
//            initializeDataBase();

//            queriesDao = new QueriesDao(getDataBase());

            /*
             * Delete the history of queries of the network service each time we start it.
             */
//            deleteQueriesHistory();

            /*
             * Initialize listeners
             */
//            initializeNetworkServiceListeners();

            this.networkServiceConnectionManager = new NetworkServiceConnectionManager2(this);

            /**
             * Start elements
             */
            onNetworkServiceStart();

            p2PLayerManager.register(this);

            this.serviceStatus = ServiceStatus.STARTED;

        } catch (Exception exception) {

            String context = "Plugin ID: " + pluginId + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "Database Name: " + NetworkServiceDatabaseConstants.DATABASE_NAME
                    + "NS Name: " + this.networkServiceType;

            String possibleCause = "The Template triggered an unexpected problem that wasn't able to solve by itself - ";
            possibleCause += exception.getMessage();
            CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, exception, context, possibleCause);

            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
            throw pluginStartException;

        }
    }

    /**
     * This method validates if there are injected all the required resources
     * in the network service root.
     */
    private void validateInjectedResources() throws CantStartPluginException {

         /*
         * Ask if the resources are injected.
         */
        if (networkClientManager == null ||
                pluginDatabaseSystem == null ||
                locationManager == null ||
                errorManager == null ||
                eventManager == null) {

            String context =
                    "Plugin ID: " + pluginId
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "networkClientManager: " + networkClientManager
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "pluginDatabaseSystem: " + pluginDatabaseSystem
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "locationManager: " + locationManager
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "errorManager: " + errorManager
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "eventManager: " + eventManager;

            String possibleCause = "No all required resource are injected";
            CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);

            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
            throw pluginStartException;
        }

    }

    private static final String IDENTITY_FILE_DIRECTORY = "private"   ;
    private static final String IDENTITY_FILE_NAME      = "nsIdentity";

    /**
     * Initializes a key pair identity for this network service
     *
     * @throws CantInitializeIdentityException if something goes wrong.
     */
    private void initializeIdentity() throws CantInitializeIdentityException {

        try {

             /*
              * Load the file with the network service identity
              */
            PluginTextFile pluginTextFile = pluginFileSystem.getTextFile(pluginId, IDENTITY_FILE_DIRECTORY, IDENTITY_FILE_NAME, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
            String content = pluginTextFile.getContent();

            identity = new ECCKeyPair(content);

        } catch (FileNotFoundException e) {

            /*
             * The file does not exist, maybe it is the first time that the plugin had been run on this device,
             * We need to create the new network service identity
             */
            try {

                /*
                 * Create the new network service identity
                 */
                identity = new ECCKeyPair();

                /*
                 * save into the file
                 */
                PluginTextFile pluginTextFile = pluginFileSystem.createTextFile(pluginId, IDENTITY_FILE_DIRECTORY, IDENTITY_FILE_NAME, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
                pluginTextFile.setContent(identity.getPrivateKey());
                pluginTextFile.persistToMedia();

            } catch (Exception exception) {
                /*
                 * The file cannot be created. We can not handle this situation.
                 */
                this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, exception);
                throw new CantInitializeIdentityException(exception, "", "Unhandled Exception");
            }


        } catch (CantCreateFileException cantCreateFileException) {

            /*
             * The file cannot be load. We can not handle this situation.
             */
            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, cantCreateFileException);
            throw new CantInitializeIdentityException(cantCreateFileException, "", "Error creating the identity file.");

        }

    }

    /**
     * Initializes the profile of this network service
     *
     * @throws CantInitializeNetworkServiceProfileException if something goes wrong.
     */
    private void initializeProfile() throws CantInitializeNetworkServiceProfileException {

        Location location;

        try {
            //ramdom location
            location = LocationUtil.ramdomLocation(); //locationManager.getLastKnownLocation();

        } catch (Exception exception) {

            location = null;
            // TODO MANAGE IN OTHER WAY...
            this.reportError(
                    UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,
                    exception
            );
        }
        this.profile = new NetworkServiceProfile();

        this.profile.setIdentityPublicKey(this.identity.getPublicKey());
        this.profile.setNetworkServiceType(this.networkServiceType);
        this.profile.setLocation(location);

    }

    /**
     * This method initialize the database
     *
     * @throws CantInitializeNetworkServiceDatabaseException
     */
    private void initializeDataBase() throws CantInitializeNetworkServiceDatabaseException {

        try {
            /*
             * Open new database connection
             */
            this.networkServiceDatabase = this.pluginDatabaseSystem.openDatabase(pluginId, NetworkServiceDatabaseConstants.DATABASE_NAME);

        } catch (CantOpenDatabaseException cantOpenDatabaseException) {

            /*
             * The database exists but cannot be open. I can not handle this situation.
             */
            throw new CantInitializeNetworkServiceDatabaseException(cantOpenDatabaseException);

        } catch (DatabaseNotFoundException e) {

            /*
             * The database no exist may be the first time the plugin is running on this device,
             * We need to create the new database
             */
            NetworkServiceDatabaseFactory networkServiceDatabaseFactory = new NetworkServiceDatabaseFactory(pluginDatabaseSystem);

            try {

                /*
                 * We create the new database
                 */
                this.networkServiceDatabase = networkServiceDatabaseFactory.createDatabase(pluginId, NetworkServiceDatabaseConstants.DATABASE_NAME);

            } catch (CantCreateDatabaseException cantOpenDatabaseException) {

                /*
                 * The database cannot be created. I can not handle this situation.
                 */
                throw new CantInitializeNetworkServiceDatabaseException(cantOpenDatabaseException);

            }
        }

    }


    private void deleteQueriesHistory() throws CantDeleteRecordDataBaseException {

        queriesDao.deleteAll();
    }

    public final void handleNetworkClientCallConnected(NetworkClientCall networkClientCall) {

        try {
            synchronized (this) {
                /*
                 * Read all pending message from database
                 */
                List<NetworkServiceMessage> messages = getNetworkServiceConnectionManager().getOutgoingMessagesDao().findPendingToSendMessagesByReceiverPublicKey(networkClientCall.getProfile().getIdentityPublicKey());

                /*
                 * For each message
                 */
                for (NetworkServiceMessage message : messages) {

                    if (networkClientCall.isConnected() && (message.getFermatMessagesStatus() == FermatMessagesStatus.PENDING_TO_SEND)) {

                        networkClientCall.sendPackageMessage(message);

                        /*
                         * Change the message and update in the data base
                         */
                        message.setFermatMessagesStatus(FermatMessagesStatus.SENT);
                        getNetworkServiceConnectionManager().getOutgoingMessagesDao().update(message);

                    } else {
                        System.out.println("networkClientCall - Connection is NOT connected = " + networkClientCall.isConnected());
                    }

                }
                networkServiceConnectionManager.removeConnectionWaitingForResponse(networkClientCall.getProfile().getIdentityPublicKey());
                /*
                 * Hang up the call
                 */
                networkClientCall.hangUp();
            }
        } catch (Exception e) {

            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

    }

    public final void handleNetworkClientActorListReceivedEvent(final UUID                    queryId      ,
                                                                final List<ActorProfile> actorProfiles) throws CantReadRecordDataBaseException, RecordNotFoundException, CantUpdateRecordDataBaseException {


//        NetworkServiceQuery query = queriesDao.findById(queryId.toString());
//
//        queriesDao.markAsDone(query);

        onNetworkServiceActorListReceived(null, actorProfiles);

    }

    /**
     * By default it will broadcast the information in a fermat bundle, but it is overridable.
     *
     * @param query
     * @param actorProfiles
     */
    public void onNetworkServiceActorListReceived(final NetworkServiceQuery      query        ,
                                                  final List<ActorProfile>  actorProfiles) {

        FermatBundle bundle = new FermatBundle();
        bundle.put("actorProfiles", actorProfiles);

        System.out.println("AbstractNs: onNetworkServiceActorListReceived");
//        broadcaster.publish(BroadcasterType.UPDATE_VIEW, bundle, query.getBroadcastCode());

    }

    /**
     * Through this method you can handle the actor found event for the actor trace that you could have done.
     *
     * @param actorProfile an instance of the actor profile
     */
    public void handleActorFoundEvent(ActorProfile actorProfile){

    }

    public void handleActorUnreachableEvent(ActorProfile actorProfile) {

        checkFailedSentMessages(actorProfile.getIdentityPublicKey());
        networkServiceConnectionManager.removeConnectionWaitingForResponse(actorProfile.getIdentityPublicKey());
        onActorUnreachable(actorProfile);
    }

    /**
     * Through this method you can handle the actor found event for the actor trace that you could have done.
     *
     * @param actorProfile an instance of the actor profile
     */
    public void onActorUnreachable(ActorProfile actorProfile){

    }

    /**
     * Notify the client when a incoming message is receive by the incomingTemplateNetworkServiceMessage
     * ant fire a new event
     *
     */
    public final void onMessageReceived(String incomingMessage) {

        try {

            NetworkServiceMessage networkServiceMessage = NetworkServiceMessage.parseContent(incomingMessage);

            //TODO networkServiceMessage.setContent(AsymmetricCryptography.decryptMessagePrivateKey(networkServiceMessage.getContent(), this.identity.getPrivateKey()));
           /*
            * process the new message receive
            */
            networkServiceMessage.setFermatMessagesStatus(FermatMessagesStatus.NEW_RECEIVED);

            NetworkServiceMessage networkServiceMessageOld;

            try {
                networkServiceMessageOld = networkServiceConnectionManager.getIncomingMessagesDao().findById(networkServiceMessage.getId().toString());
                if(networkServiceMessageOld!=null && networkServiceMessageOld.equals(networkServiceMessage)) {
                    System.out.println("***************** MESSAGE DUPLICATED. IGNORING MESSAGE *****************");
                    return;
                }

                if(networkServiceMessageOld!=null){
                    System.out.println("***************** ID DUPLICATED. GENERATING A NEW ONE *****************");
                    networkServiceMessage.setId(UUID.randomUUID());
                }
            }catch(CantReadRecordDataBaseException | RecordNotFoundException e){
                e.printStackTrace();
            }

            networkServiceConnectionManager.getIncomingMessagesDao().create(networkServiceMessage);
            onNewMessageReceived(networkServiceMessage);

        } catch (Exception e) {
            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
    }

    public final void handleNetworkServiceRegisteredEvent() {

        this.registered = Boolean.TRUE;

//        try {
//            if (networkServicePendingMessagesSupervisorAgent == null)
//                this.networkServicePendingMessagesSupervisorAgent = new NetworkServicePendingMessagesSupervisorAgent(this);
//
//            this.networkServicePendingMessagesSupervisorAgent.start();

    /*    } catch (Exception ex) {
            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, ex);
        }*/

        onNetworkServiceRegistered();
    }

    protected void onNetworkServiceRegistered() {

    }

    protected void onNetworkServiceStart() throws CantStartPluginException {

    }

    /**
     * Handle the event NetworkClientConnectionLostEvent
     * @param communicationChannel
     */
    public final void handleNetworkClientConnectionLostEvent(final CommunicationChannels communicationChannel) {

        try {

            if(!networkClientManager.getConnection().isRegistered()) {

                try {
                    if (networkServicePendingMessagesSupervisorAgent != null)
                        this.networkServicePendingMessagesSupervisorAgent.pause();

                    networkServiceConnectionManager.removeAllConnectionWaitingForResponse();
                } catch (Exception ex) {
                    System.out.println("Failed to pause the messages supervisor agent - > NS: "+this.getProfile().getNetworkServiceType());
                }

                this.registered = Boolean.FALSE;

                onNetworkClientConnectionLost();

            }

        } catch (Exception e) {
            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

    }

    /**
     * This method is automatically called when the client connection was lost
     */
    protected void onNetworkClientConnectionLost() {

    }

    /**
     * Handle the event NetworkClientConnectionClosedEvent
     * @param communicationChannel
     */
    public final void handleNetworkClientConnectionClosedEvent(final CommunicationChannels communicationChannel) {

        try {

            if(!networkClientManager.getConnection().isRegistered()) {

                try {

                    if (networkServicePendingMessagesSupervisorAgent != null)
                        this.networkServicePendingMessagesSupervisorAgent.pause();

                    networkServiceConnectionManager.removeAllConnectionWaitingForResponse();
                } catch (Exception ex) {
                    System.out.println("Failed to pause the messages supervisor agent - > NS: "+this.getProfile().getNetworkServiceType());
                }

                this.registered = Boolean.FALSE;

                onNetworkClientConnectionClosed();

            }

        }catch (Exception e) {
            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

    }

    /**
     * Method tha send a new Message
     */
    public void sendNewMessage(NetworkServiceProfile destination, String messageContent) throws CantSendMessageException {

        try {

            /*
             * Created the message
             */
            NetworkServiceMessage networkServiceMessage = NetworkServiceMessageFactory.buildNetworkServiceMessage(
                    this.getProfile(),
                    destination      ,
                    messageContent,
                    MessageContentType.TEXT
            );


            /*
             * Save to the data base table
             */
//            networkServiceConnectionManager.getOutgoingMessagesDao().create(networkServiceMessage);

            /*
             * Ask the client to connect
             */
//            networkServiceConnectionManager.connectTo(destination);

        } catch (Exception e){

            System.out.println("Error sending message: " + e.getMessage());
            throw new CantSendMessageException(e, "destination: "+destination+" - message: "+messageContent, "Unhandled error trying to send a message.");
        }
    }

    /**
     * Check fail sent messages.
     * When a call to an actor fails then we update the fail count of the messages sent to it.
     * Then the message will be sent again after a amount of time defined in the message sender supervisor agent.
     * If the message stays more than three days not being sent then we're going to delete it.
     *
     * @param destinationPublicKey of the actor which we're sending the messages.
     */
    private void checkFailedSentMessages(final String destinationPublicKey){

        try {

            /*
             * Read all pending message from database
             */
            Map<String, Object> filters = new HashMap<>();
            filters.put(NetworkServiceDatabaseConstants.OUTGOING_MESSAGES_RECEIVER_PUBLIC_KEY_COLUMN_NAME, destinationPublicKey);
            filters.put(NetworkServiceDatabaseConstants.OUTGOING_MESSAGES_STATUS_COLUMN_NAME, MessagesStatus.PENDING_TO_SEND.getCode());

            List<NetworkServiceMessage> messages = getNetworkServiceConnectionManager().getOutgoingMessagesDao().findAll(filters);


            for (NetworkServiceMessage fermatMessageCommunication: messages) {

                /*
                 * Increment the fail count field
                 */
                fermatMessageCommunication.setFailCount(fermatMessageCommunication.getFailCount() + 1);

                if(fermatMessageCommunication.getFailCount() > 10) {

                    /*
                     * Calculate the date
                     */
                    long sentDate = fermatMessageCommunication.getShippingTimestamp().getTime();
                    long currentTime = System.currentTimeMillis();
                    long dif = currentTime - sentDate;
                    double dias = Math.floor(dif / (1000 * 60 * 60 * 24));

                    /*
                     * if have mora that 3 days
                     */
                    if ((int) dias > 3) {
                        getNetworkServiceConnectionManager().getOutgoingMessagesDao().delete(fermatMessageCommunication.getId().toString());
                    }
                } else {
                    getNetworkServiceConnectionManager().getOutgoingMessagesDao().update(fermatMessageCommunication);
                }

            }

        } catch(Exception e){
            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

    }

//    /**
//     * Method tha send a new Message
//     */
//    public UUID onlineActorsDiscoveryQuery(final DiscoveryQueryParameters discoveryQueryParameters,
//                                           final String                   broadcastCode           ) throws CantRequestProfileListException {
//
//        try {
//
//            UUID queryId = getConnection().onlineActorsDiscoveryQuery(discoveryQueryParameters, getPublicKey());
//
//            /*
//             * Create the query
//             */
//            NetworkServiceQuery networkServiceQuery = new NetworkServiceQuery(
//                    queryId,
//                    broadcastCode      ,
//                    discoveryQueryParameters,
//                    System.currentTimeMillis(),
//                    QueryTypes.ACTOR_LIST,
//                    QueryStatus.REQUESTED
//            );
//
//            queriesDao.create(networkServiceQuery);
//
//            return queryId;
//
//        } catch (Exception e){
//
//            throw new CantRequestProfileListException(e, "discoveryQueryParameters: "+discoveryQueryParameters+" - broadcastCode: "+broadcastCode, "Unhandled error trying to send a query request.");
//        }
//    }

    /**
     * Method tha send a new Message
     */
    public UUID sendNewMessage(final ActorProfile sender        ,
                               final ActorProfile destination   ,
                               final String       messageContent) throws CantSendMessageException {

        try {

            /*
             * Created the message
             */
            NetworkServiceMessage networkServiceMessage = NetworkServiceMessageFactory.buildNetworkServiceMessage(
                    sender           ,
                    destination      ,
                    this.getProfile(),
                    messageContent   ,
                    MessageContentType.TEXT
            );

            return p2PLayerManager.sendMessage(networkServiceMessage,getNetworkServiceType(),destination.getHomeNodePublicKey());

            /*
             * Save to the data base table
             */
//            networkServiceConnectionManager.getOutgoingMessagesDao().create(networkServiceMessage);

            /*
             * Ask the client to connect
             */
//            networkServiceConnectionManager.connectTo(destination);

        }catch (Exception e){

            System.out.println("Error sending message: " + e.getMessage());
            throw new CantSendMessageException(e, "destination: "+destination+" - message: "+messageContent, "Unhandled error trying to send a message.");
        }
    }

    protected UUID discoveryActorProfiles(final DiscoveryQueryParameters discoveryQueryParameters) throws com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.exceptions.CantSendMessageException {

         /*
         * Create the query
         */
        UUID uuid = UUID.randomUUID();
        ActorListMsgRequest actorListMsgRequest = new ActorListMsgRequest(uuid,networkServiceType.getCode(),discoveryQueryParameters);

        p2PLayerManager.sendDiscoveryMessage(actorListMsgRequest,networkServiceType,null);

        return uuid;

    }

    /**
     * This method is automatically called when the network service receive
     * a new message
     *
     * @param messageReceived
     */
    public synchronized void onNewMessageReceived(NetworkServiceMessage messageReceived) {

        System.out.println("Me llego un nuevo mensaje" + messageReceived);
    }

    public final synchronized void onNetworkServiceSentMessage(NetworkServiceMessage networkServiceMessage) {

        System.out.println("Message Delivered " + networkServiceMessage);

        //networkServiceMessage.setContent(AsymmetricCryptography.decryptMessagePrivateKey(networkServiceMessage.getContent(), this.identity.getPrivateKey()));

        try {
            networkServiceConnectionManager.getOutgoingMessagesDao().markAsDelivered(networkServiceMessage);

            onSentMessage(networkServiceMessage);
        } catch (Exception e) {
            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

    }

    public final synchronized void onNetworkServiceSentMessageError(NetworkServiceMessage networkServiceMessage) {

        System.out.println("Message not delivered " + networkServiceMessage);

        //networkServiceMessage.setContent(AsymmetricCryptography.decryptMessagePrivateKey(networkServiceMessage.getContent(), this.identity.getPrivateKey()));

        try {
            networkServiceConnectionManager.getOutgoingMessagesDao().markAsPendingToSend(networkServiceMessage);

            onSentMessageError(networkServiceMessage);
        } catch (Exception e) {
            this.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

    }

    public final synchronized void onNetworkServiceFailedMessage(NetworkServiceMessage networkServiceMessage) {

        System.out.println("12345P2P onNetworkServiceFailedMessage Message failed " + networkServiceMessage.toJson());

        try {
            networkServiceConnectionManager.getOutgoingMessagesDao().markAsPendingToSend(networkServiceMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void onSentMessage(NetworkServiceMessage networkServiceMessage) {

    }

    public synchronized void onSentMessageError(NetworkServiceMessage networkServiceMessage) {

    }


    /**
     * Get the database instance
     * @return Database
     */
    public Database getDataBase() {
        return this.networkServiceDatabase;
    }

    /**
     * This method is automatically called when the client connection was closed
     */
    protected void onNetworkClientConnectionClosed() {

    }

    public NetworkServiceConnectionManager2 getNetworkServiceConnectionManager() {
        return networkServiceConnectionManager;
    }

    public LocationManager getLocationManager() {

        return locationManager;
    }

    public EventManager getEventManager() {

        return eventManager;
    }

    /**
     * Get registered value
     *
     * @return boolean
     */
    public final boolean isRegistered() {
        return registered;
    }

    public final String getPublicKey() {

        return this.identity.getPublicKey();
    }

    public ECCKeyPair getIdentity() {

        return identity;
    }


    public final NetworkServiceProfile getProfile() {

        return profile;
    }

    public final P2PLayerManager getConnection() {

        return p2PLayerManager;
    }

    public NetworkServiceType getNetworkServiceType() {
        return networkServiceType;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setPluginFileSystem(PluginFileSystem pluginFileSystem) {
        this.pluginFileSystem = pluginFileSystem;
    }

    public void setP2PLayerManager(P2PLayerManager p2PLayerManager) {
        this.p2PLayerManager = p2PLayerManager;
    }

}
