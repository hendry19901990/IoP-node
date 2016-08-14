import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationManager;
import com.bitdubai.fermat_osa_addon.layer.linux.device_location.developer.bitdubai.version_1.DeviceLocationSystemAddonRoot;
import com.bitdubai.fermat_osa_addon.layer.linux.file_system.developer.bitdubai.version_1.PluginFileSystemLinuxAddonRoot;
import org.iop.version_1.IoPNodePluginRoot;

/**
 * Created by mati on 12/08/16.
 */
public class Main {

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
            System.out.println("* FERMAT - Network Node - Version 1.0 (2016)                            *");
            System.out.println("* www.fermat.org                                                      *");
            System.out.println("***********************************************************************");
            System.out.println("");
            System.out.println("- Starting process ...");

            DeviceLocationSystemAddonRoot deviceLocationSystemAddonRoot = new DeviceLocationSystemAddonRoot();
            deviceLocationSystemAddonRoot.start();
//
            PluginFileSystemLinuxAddonRoot pluginFileSystemLinuxAddonRoot = new PluginFileSystemLinuxAddonRoot();
            pluginFileSystemLinuxAddonRoot.start();

            IoPNodePluginRoot ioPNodePluginRoot = new IoPNodePluginRoot();
            ioPNodePluginRoot.setLocationManager((LocationManager) deviceLocationSystemAddonRoot.getManager());
            ioPNodePluginRoot.setPluginFileSystem((PluginFileSystem) pluginFileSystemLinuxAddonRoot.getManager());
            ioPNodePluginRoot.start();

            System.out.println("FERMAT - Network Node - started satisfactory...");

        } catch (Exception e) {

            System.out.println("***********************************************************************");
            System.out.println("* FERMAT - ERROR                                                      *");
            System.out.println("***********************************************************************");
            e.printStackTrace();
            System.exit(1);
        }

    }
}
