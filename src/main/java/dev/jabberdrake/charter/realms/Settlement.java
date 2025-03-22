package dev.jabberdrake.charter.realms;

import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class Settlement {



    private static final String DEFAULT_DESC = "<green>Settlement of <gold>";
    private static final TextColor DEFAULT_COLOR = TextUtils.ZORBA;

    private int id;
    private String name;
    private String displayName;
    private String description;
    private TextColor mapColor;
    private List<CharterTitle> titles;
    private CharterTitle defaultTitle;
    private Map<UUID, CharterTitle> population;
    private Set<ChunkAnchor> territory;

    public Settlement(int id, String name, String displayName, String description, TextColor mapColor, List<CharterTitle> titles, CharterTitle defaultTitle, Map<UUID, CharterTitle> population, Set<ChunkAnchor> territory) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.titles = titles;
        this.defaultTitle = defaultTitle;
        this.population = population;
        this.territory = territory;
    }

    public Settlement(String name, List<CharterTitle> titles, CharterTitle defaultTitle, UUID leaderID, Map<UUID, CharterTitle> population, Set<ChunkAnchor> territory) {
        this.id = RealmManager.incrementSettlementCount();
        this.name = name;
        this.displayName = name;
        this.description = DEFAULT_DESC + Bukkit.getPlayer(leaderID).getName();
        this.mapColor = DEFAULT_COLOR;
        this.titles = titles;
        this.defaultTitle = defaultTitle;
        this.population = population;
        this.territory = territory;
    }

    public Settlement(int id, String name, String displayName, String description, TextColor mapColor) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.titles = new ArrayList<>();
        this.population = new HashMap<>();
        this.territory = new HashSet<>();
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayNameAsString() {
        return this.displayName;
    }

    public Component getDisplayName() {
        return MiniMessage.miniMessage().deserialize(this.getDisplayNameAsString());
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescriptionAsString() {
        return this.description;
    }

    public Component getDescription() {
        return MiniMessage.miniMessage().deserialize(this.getDescriptionAsString());
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TextColor getMapColor() {
        return NamedTextColor.GOLD;
    }

    public void setMapColor(TextColor color) { this.mapColor = color; }

    public List<CharterTitle> getTitles() {
        return this.titles;
    }

    public CharterTitle getDefaultTitle() {
        return this.defaultTitle;
    }

    public boolean setDefaultTitle(CharterTitle defaultTitle) {
        for (CharterTitle title : this.titles) {
            if (title.equals(defaultTitle)) {
                this.defaultTitle = defaultTitle;
                return true;
            }
        }
        return false;
    }

    public Map<UUID, CharterTitle> getPopulation() {
        return this.population;
    }

    public boolean containsPlayer(UUID uuid) {
        return this.population.containsKey(uuid);
    }

    public Set<ChunkAnchor> getTerritory() {
        return this.territory;
    }

    public void addTitle(CharterTitle title) {
        this.titles.add(title);
    }

    public void addCitizen(UUID playerID, CharterTitle title) {
        this.population.put(playerID, title);
    }

    public void addTerritory(ChunkAnchor anchor) {
        this.territory.add(anchor);
    }

    public void removeTerritory(ChunkAnchor anchor) {
        this.territory.remove(anchor);
    }

    public CharterTitle getTitleFromName(String titleName) {
        for (CharterTitle title : this.titles) {
            if (title.getName().equals(titleName)) {
                return title;
            }
        }
        return null;
    }

    public CharterTitle getTitleFromMember(UUID memberID) {
        return this.population.getOrDefault(memberID, null);
    }

    public static Settlement load(FileConfiguration data, String root) {
        // Obtaining basic attributes
        int stmID = data.getInt(root + ".id");
        String stmName = data.getString(root + ".name");
        String stmDisplayName = data.getString(root + ".displayName");
        String stmDescription = data.getString(root + ".description");
        String stmMapColorAsString = data.getString(root + ".mapColor");
        TextColor stmMapColor = TextColor.fromHexString(stmMapColorAsString);

        Settlement stm = new Settlement(stmID, stmName, stmDisplayName, stmDescription, stmMapColor);

        // Building title list
        List<String> readTitles = data.getStringList(root + ".titles");
        for (String readTitle : readTitles) {
            stm.addTitle(CharterTitle.fromString(readTitle, stm));
        }

        // Setting default title
        String stmDefaultTitleName = data.getString(root + ".defaultTitle");
        for (CharterTitle title : stm.getTitles()) {
            if (title.getName().equals(stmDefaultTitleName)) {
                if (!stm.setDefaultTitle(title)) {
                    Charter.getPlugin(Charter.class).getLogger().warning("[Settlement::load] Default title for " + stmName + " is not in the title list!");
                    stm.setDefaultTitle(stm.getTitles().getFirst());
                }
            }
        }

        // Build population map
        List<String> readPopulation = data.getStringList(root + ".population");
        for (String readCitizen : readPopulation) {
            String[] parts = readCitizen.split(";");
            UUID convertedUUID = UUID.fromString(parts[0]);
            CharterTitle fetchedTitle = stm.getTitleFromName(parts[1]);

            stm.addCitizen(convertedUUID, fetchedTitle);
        }

        // Build chunk anchor set for Settlement
        List<String> readTerritory = data.getStringList(root + ".territory");
        for (String readChunk: readTerritory) {
            String[] parts = readChunk.split(";");
            int readX = Integer.parseInt(parts[0]);
            int readZ = Integer.parseInt(parts[1]);
            ChunkAnchor anchor = new ChunkAnchor(readX, readZ);
            stm.addTerritory(anchor);
            if (RealmManager.getTerritoryMap().containsKey(anchor)) {
                Settlement owner = RealmManager.getTerritoryMap().get(anchor);
                if (!owner.equals(stm)) {
                    Charter.getPlugin(Charter.class).getLogger().warning("[Settlement::load] Chunk at [x=" + readX + ", z=" + readZ + "] claimed by both " + owner.getName() + " and " + stm.getName() + "!");
                }
            } else {
                RealmManager.getTerritoryMap().put(anchor, stm);
            }
        }

        return stm;
    }

    public static void store(Settlement settlement, FileConfiguration data, String root) {
        // Storing basic attributes
        data.set(root + ".id", settlement.getId());
        data.set(root + ".name", settlement.getName());
        data.set(root + ".displayName", settlement.getDisplayNameAsString());
        data.set(root + ".description", settlement.getDescriptionAsString());
        data.set(root + ".mapColor", settlement.getMapColor().asHexString());

        // Storing all internal titles
        List<String> preparedTitleStrings = new ArrayList<>();
        for (CharterTitle title : settlement.getTitles()) {
            preparedTitleStrings.add(title.serialize());
        }
        data.set(root + ".titles", preparedTitleStrings);

        // Storing default title as name string
        String defaultTitleName = settlement.getDefaultTitle().getName();
        data.set(root + ".defaultTitle", defaultTitleName);

        // Storing population map
        List<String> preparedPopulationStrings = new ArrayList<>();
        for (UUID uuid : settlement.getPopulation().keySet()) {
            preparedPopulationStrings.add(uuid.toString() + ";" + settlement.getPopulation().get(uuid).getName());
        }
        data.set(root + ".population", preparedPopulationStrings);

        // Storing territory map
        List<String> preparedTerritoryStrings = new ArrayList<>();
        for (ChunkAnchor chunk : settlement.getTerritory()) {
            preparedTerritoryStrings.add(chunk.getX() + ";" + chunk.getZ());
        }
        data.set(root + ".territory", preparedTerritoryStrings);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Settlement) {
            Settlement other = (Settlement) object;
            return this.getId() == other.getId();
        } else return false;
    }

    @Override
    public String toString() {
        return "Settlement{id=" + this.getId() + ";name=" + this.getName() + "}";
    }
}
