package pl.htgmc.htgpluginvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import pl.htgmc.htghttp.PluginInfo;
import pl.htgmc.htghttp.PluginRegistry;

import java.util.List;
import java.util.stream.Collectors;

@Plugin(
        id = "htgpluginvelocity",
        name = "HTGPluginVelocity",
        version = "0.0.1-alfa",
        dependencies = {
                @Dependency(id = "htghttp")
        }
)
public class HTGPluginVelocity {

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer server;

    private static List<String> officialPlugins = List.of();

    private static final String SECURITY_TOKEN = "SECRET-123";

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("[HTGPluginVelocity] Rejestracja pluginów Velocity...");

        List<PluginInfo> allPlugins = server.getPluginManager().getPlugins().stream()
                .map(PluginContainer::getDescription)
                .map(desc -> new PluginInfo(
                        desc.getName().orElse("unknown"),
                        desc.getVersion().orElse("unknown"),
                        true,
                        "VELOCITY"
                ))
                .collect(Collectors.toList());

        PluginRegistry.register("VELOCITY", allPlugins);

        List<String> officialNames = allPlugins.stream()
                .map(p -> p.name)
                .filter(name -> name.toUpperCase().startsWith("HTG"))
                .collect(Collectors.toList());

        setOfficialPlugins(officialNames);

        logger.info("[HTGPluginVelocity] Zarejestrowano {} pluginów, w tym {} oficjalnych (HTG).",
                allPlugins.size(), officialNames.size());
    }

    // 🔒 Prywatna pamięć podręczna
    public static void setOfficialPlugins(List<String> list) {
        officialPlugins = list;
    }

    public static List<String> getOfficialPlugins(String token) {
        if (token == null || !token.equals(SECURITY_TOKEN)) {
            throw new SecurityException("❌ Nieprawidłowy token dostępu");
        }
        return officialPlugins;
    }

    public static boolean isOfficial(String name) {
        return officialPlugins.stream().anyMatch(p -> p.equalsIgnoreCase(name));
    }
}
