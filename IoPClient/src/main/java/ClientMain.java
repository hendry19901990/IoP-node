import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.EventManager;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_osa_addon.layer.linux.file_system.developer.bitdubai.version_1.PluginFileSystemLinuxAddonRoot;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_pip_addon.layer.platform_service.error_manager.developer.bitdubai.version_1.ErrorManagerPlatformServiceAddonRoot;
import com.bitdubai.fermat_pip_addon.layer.platform_service.event_manager.developer.bitdubai.version_1.EventManagerPlatformServiceAddonRoot;
import com.fermat_p2p_layer.version_1.P2PLayerPluginRoot;
import org.iop.client.version_1.IoPClientPluginRoot;
import org.iop.ns.chat.ChatNetworkServicePluginRoot;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by mati on 12/08/16.
 */
public class ClientMain {

    /**
     * Represent the fermatSystem instance
     */

    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {

        try {

            System.out.println("***********************************************************************");
            System.out.println("* FERMAT - Network Client - Version 1.0 (2016)                            *");
            System.out.println("* www.fermat.org                                                      *");
            System.out.println("***********************************************************************");
            System.out.println("");
            System.out.println("- Starting process ...");


            //file system
            PluginFileSystemLinuxAddonRoot pluginFileSystemLinuxAddonRoot = new PluginFileSystemLinuxAddonRoot();
            pluginFileSystemLinuxAddonRoot.start();

            //error manager
            ErrorManagerPlatformServiceAddonRoot errorManagerPlatformServiceAddonRoot = new ErrorManagerPlatformServiceAddonRoot();
            errorManagerPlatformServiceAddonRoot.start();

            //event manager
            EventManagerPlatformServiceAddonRoot eventManagerPlatformServiceAddonRoot = new EventManagerPlatformServiceAddonRoot();
            eventManagerPlatformServiceAddonRoot.setErrorManager((ErrorManager) errorManagerPlatformServiceAddonRoot.getManager());
            eventManagerPlatformServiceAddonRoot.start();

            //layer
            P2PLayerPluginRoot p2PLayerPluginRoot = new P2PLayerPluginRoot();
            p2PLayerPluginRoot.setEventManager((EventManager) eventManagerPlatformServiceAddonRoot.getManager());
            p2PLayerPluginRoot.start();

            //node
            IoPClientPluginRoot ioPNodePluginRoot = new IoPClientPluginRoot();
            ioPNodePluginRoot.setPluginFileSystem((PluginFileSystem) pluginFileSystemLinuxAddonRoot.getManager());
            ioPNodePluginRoot.setEventManager((EventManager) eventManagerPlatformServiceAddonRoot.getManager());
            ioPNodePluginRoot.setP2PLayerManager(p2PLayerPluginRoot);
            ioPNodePluginRoot.start();

            //console ns
            ChatNetworkServicePluginRoot chatNetworkServicePluginRoot = new ChatNetworkServicePluginRoot();
            chatNetworkServicePluginRoot.setP2PLayerManager(p2PLayerPluginRoot);
            chatNetworkServicePluginRoot.setEventManager((EventManager) eventManagerPlatformServiceAddonRoot.getManager());
            chatNetworkServicePluginRoot.setPluginFileSystem((PluginFileSystem) pluginFileSystemLinuxAddonRoot.getManager());
            chatNetworkServicePluginRoot.setErrorManager((ErrorManager) errorManagerPlatformServiceAddonRoot.getManager());
            //For test
            chatNetworkServicePluginRoot.setNetworkClientManager(ioPNodePluginRoot);
            p2PLayerPluginRoot.register(chatNetworkServicePluginRoot);
            chatNetworkServicePluginRoot.start();

            System.out.println("FERMAT - Network Client - started satisfactory...");

            while(!ioPNodePluginRoot.isConnected()){
                System.out.println("Not connected yet");
                Thread.sleep(500);
            }

            //Register an actor for testing
            ActorProfile profile = new ActorProfile();
            profile.setIdentityPublicKey(UUID.randomUUID().toString());
            System.out.println("I will try to register an actor with pk "+profile.getIdentityPublicKey());
            profile.setActorType(Actors.CHAT.getCode());
            profile.setName("Juan");
            profile.setAlias("Alias chat");
            //This represents a valid image
            profile.setPhoto(null);
            profile.setNsIdentityPublicKey(chatNetworkServicePluginRoot.getNetWorkServicePublicKey());
            profile.setExtraData("Test extra data");
            chatNetworkServicePluginRoot.registerActor(profile, 0, 0);

            ActorProfile profile2 = new ActorProfile();
            profile2.setIdentityPublicKey(UUID.randomUUID().toString());
            System.out.println("I will try to register an actor with pk "+profile2.getIdentityPublicKey());
            profile2.setActorType(Actors.CHAT.getCode());
            profile2.setName("Pedro");
            profile2.setAlias("Alias chat");
            //This represents a valid image
            profile2.setPhoto(null);
            profile2.setNsIdentityPublicKey(chatNetworkServicePluginRoot.getNetWorkServicePublicKey());
            profile2.setExtraData("Test extra data");
            chatNetworkServicePluginRoot.registerActor(profile2, 0, 0);
//            chatNetworkServicePluginRoot.getConnection().registerProfile();


            chatNetworkServicePluginRoot.requestActorProfilesList();

            ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(4);
            scheduledThreadPool.scheduleAtFixedRate(
                    new Thread() {
                        int i = 0;
                        @Override
                        public void run() {
                            try {
                                chatNetworkServicePluginRoot.sendNewMessage(
                                        profile,
                                        profile2,
                                        "HEEE MACHO COMO ANDAS?"+i
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            i++;
                        }
                    },
                    15,
                    5,
                    TimeUnit.SECONDS
            );

            while (true){

            }



        } catch (Exception e) {

            System.out.println("***********************************************************************");
            System.out.println("* FERMAT - ERROR                                                      *");
            System.out.println("***********************************************************************");
            e.printStackTrace();
            System.exit(1);
        }

    }
}
