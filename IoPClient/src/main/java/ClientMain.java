import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.EventManager;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationManager;
import com.bitdubai.fermat_osa_addon.layer.linux.file_system.developer.bitdubai.version_1.PluginFileSystemLinuxAddonRoot;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.Profile;
import com.bitdubai.fermat_pip_addon.layer.platform_service.error_manager.developer.bitdubai.version_1.ErrorManagerPlatformServiceAddonRoot;
import com.bitdubai.fermat_pip_addon.layer.platform_service.event_manager.developer.bitdubai.version_1.EventManagerPlatformServiceAddonRoot;
import com.fermat_p2p_layer.version_1.P2PLayerPluginRoot;
import org.iop.client.version_1.IoPClientPluginRoot;
import org.iop.ns.chat.ChatNetworkServicePluginRoot;

import java.util.UUID;


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
            profile.setPhoto(new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
                    0, 0, 0, 15, 0, 0, 0, 15, 8, 6, 0, 0, 0, 59, -42, -107,
                    74, 0, 0, 0, 64, 73, 68, 65, 84, 120, -38, 99, 96, -62, 14, -2,
                    99, -63, 68, 1, 100, -59, -1, -79, -120, 17, -44, -8, 31, -121, 28, 81,
                    26, -1, -29, 113, 13, 78, -51, 100, -125, -1, -108, 24, 64, 86, -24, -30,
                    11, 101, -6, -37, 76, -106, -97, 25, 104, 17, 96, -76, 77, 97, 20, -89,
                    109, -110, 114, 21, 0, -82, -127, 56, -56, 56, 76, -17, -42, 0, 0, 0,
                    0, 73, 69, 78, 68, -82, 66, 96, -126});
            profile.setNsIdentityPublicKey(chatNetworkServicePluginRoot.getNetWorkServicePublicKey());
            profile.setExtraData("Test extra data");
            chatNetworkServicePluginRoot.registerActor(profile);
//            chatNetworkServicePluginRoot.getConnection().registerProfile();


            chatNetworkServicePluginRoot.requestActorProfilesList();

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
