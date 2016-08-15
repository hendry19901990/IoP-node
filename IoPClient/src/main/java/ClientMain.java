import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.EventManager;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationManager;
import com.bitdubai.fermat_osa_addon.layer.linux.file_system.developer.bitdubai.version_1.PluginFileSystemLinuxAddonRoot;
import com.bitdubai.fermat_pip_addon.layer.platform_service.error_manager.developer.bitdubai.version_1.ErrorManagerPlatformServiceAddonRoot;
import com.bitdubai.fermat_pip_addon.layer.platform_service.event_manager.developer.bitdubai.version_1.EventManagerPlatformServiceAddonRoot;
import com.fermat_p2p_layer.version_1.P2PLayerPluginRoot;
import org.iop.client.version_1.IoPClientPluginRoot;
import org.iop.ns.chat.ChatNetworkServicePluginRoot;


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

            //console ns
            ChatNetworkServicePluginRoot chatNetworkServicePluginRoot = new ChatNetworkServicePluginRoot();
            chatNetworkServicePluginRoot.setP2PLayerManager(p2PLayerPluginRoot);
            chatNetworkServicePluginRoot.setEventManager((EventManager) eventManagerPlatformServiceAddonRoot.getManager());
            chatNetworkServicePluginRoot.setPluginFileSystem((PluginFileSystem) pluginFileSystemLinuxAddonRoot.getManager());
            chatNetworkServicePluginRoot.setErrorManager((ErrorManager) errorManagerPlatformServiceAddonRoot.getManager());


            //node
            IoPClientPluginRoot ioPNodePluginRoot = new IoPClientPluginRoot();
            ioPNodePluginRoot.setPluginFileSystem((PluginFileSystem) pluginFileSystemLinuxAddonRoot.getManager());
            ioPNodePluginRoot.setEventManager((EventManager) eventManagerPlatformServiceAddonRoot.getManager());
            ioPNodePluginRoot.setP2PLayerManager(p2PLayerPluginRoot);
            ioPNodePluginRoot.start();

            System.out.println("FERMAT - Network Client - started satisfactory...");


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
