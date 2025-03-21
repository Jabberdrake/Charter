package dev.jabberdrake.charter.jade.players;

import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.realms.RealmManager;
import dev.jabberdrake.charter.realms.Settlement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PlayerManager {

    private static final Charter plugin = Charter.getPlugin(Charter.class);
    private static final Logger logger = plugin.getLogger();

    private static final String PROFILE_FOLDER = "/profiles/";

    private static Map<UUID, JadePlayer> cache = new HashMap<>();

    public static void initialize() {

    }

    public static void shutdown() {

    }

    public static JadePlayer parsePlayer(UUID uuid) {
        return cache.getOrDefault(uuid, null);
    }

    public static void handleLogin(UUID uuid) {
        PlayerManager.loadProfile(uuid);
        JadePlayer jadePlayer = parsePlayer(uuid);

        for (int stmID = 1; stmID <= RealmManager.getSettlementCount(); stmID++) {
            Settlement stm = RealmManager.getSettlement(stmID);
            if (stm != null && stm.containsPlayer(uuid)) {
                jadePlayer.addSettlement(stmID);
                if (jadePlayer.getFocusSettlementID() == -1) {
                    jadePlayer.setFocusSettlement(stmID);
                }
            }
        }

        logger.info("[PlayerManager::handleLogin] Successfully composed internal data structures for player " + plugin.getServer().getPlayer(uuid).getName() + "!");
    }

    public static void handleLogout(UUID uuid) {
        PlayerManager.storeProfile(uuid);
    }


    public static JadeProfile fetchProfile(UUID uuid) {
        return cache.getOrDefault(uuid, null).getProfile();
    }

    public static String getProfileFilepath(UUID uuid) {
        return Charter.getPlugin(Charter.class).getDataFolder() + PROFILE_FOLDER + uuid.toString() + ".yml";
    }

    public static boolean loadProfile(UUID uuid) {

        // Profile files should always be in '/Charter/profiles/<uuid>.yml'
        String pathname = getProfileFilepath(uuid);
        File profileFile = new File(pathname);

        if (!profileFile.exists()) {
            // If no matching profile file was found, it's probably the first login.
            JadeProfile profile = new JadeProfile(uuid);
            JadePlayer jadePlayer = new JadePlayer(uuid, profile);

            PlayerManager.cache.put(uuid, jadePlayer);
            logger.info("[PlayerManager::loadProfile] Created a new profile for uuid=" + uuid + "!");
            return true;
        }

        // If a matching profile file was found, compose the profile object from the data therein.
        FileConfiguration data = YamlConfiguration.loadConfiguration(profileFile);
        JadeProfile profile = JadeProfile.load(data, "profile");

        JadePlayer jadePlayer = new JadePlayer(uuid, profile);
        PlayerManager.cache.put(uuid, jadePlayer);
        return true;
    }

    public static boolean storeProfile(UUID uuid) {
        String pathname = getProfileFilepath(uuid);
        File profileFile = new File(pathname);
        FileConfiguration data = YamlConfiguration.loadConfiguration(profileFile);

        JadeProfile profile = PlayerManager.fetchProfile(uuid);
        JadeProfile.store(profile, data, "profile");

        try {
            data.save(profileFile);
        } catch (IOException e) {
            logger.warning("[PlayerManager::storeProfile] Failed to save data for player profile (UUID=" + uuid + ")!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
