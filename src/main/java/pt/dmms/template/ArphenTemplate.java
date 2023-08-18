package pt.dmms.template;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pt.dmms.arphenheart.database.Database;
import pt.dmms.arphenheart.database.HikariDatabase;
import pt.dmms.arphenheart.database.SQLite;
import pt.dmms.arphenheart.factory.ConfigFactory;
import pt.dmms.arphenheart.util.InventoryUtil;
import pt.dmms.arphenheart.util.SoundsUtil;
import pt.dmms.arphenheart.util.TitleUtil;
import pt.dmms.arphenheart.util.message.ColorUtil;
import pt.dmms.arphenheart.util.message.MessageUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ArphenTemplate extends JavaPlugin {

    private Database database;
    private ConfigFactory langConfigFactory, soundsConfigFactory, titleConfigFactory;

    @Override
    public void onEnable() {
        loadConfig();
        loadUtil(false);
        loadDatabase();
        loadHook();
        loadRepository();
        loadManager();
        loadCommand();
        loadListener();
    }

    private final List<ConfigFactory> configFactories = new ArrayList<>();

    private void loadConfig() {
        saveDefaultConfig();
        configFactories.addAll(
                List.of(
                        langConfigFactory = new ConfigFactory(this, "lang.yml"),
                        soundsConfigFactory = new ConfigFactory(this, "sounds.yml"),
                        titleConfigFactory = new ConfigFactory(this, "title.yml")
                )
        );
        //Update the info.yml every run
        ConfigFactory.delete(this, "info.yml");
        new ConfigFactory(this, "info.yml");
    }

    private void loadUtil(boolean reload) {
        MessageUtil.config(langConfigFactory.getConfig());
        SoundsUtil.config = soundsConfigFactory.getConfig();
        TitleUtil.config = titleConfigFactory.getConfig();
        if (!reload) {
            InventoryUtil.load(this);
        }
    }

    private void loadDatabase() {
        val credentials = Database.getCredentials(this);
        if (credentials.useMySQL())
            this.database = new HikariDatabase(
                    credentials.host(),
                    credentials.database(),
                    credentials.user(),
                    credentials.password()
            );
        else
            this.database = new SQLite(this);
    }

    private void loadHook() {
    }

    private void loadRepository() {
    }

    private void loadManager() {
    }

    private void loadListener() {
    }

    private void listener(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void loadCommand() {
    }

    private PaperCommandManager paperCommandManager;


    private void command(BaseCommand... command) {
        paperCommandManager = new PaperCommandManager(this);
        for (BaseCommand baseCommand : command) {
            paperCommandManager.registerCommand(baseCommand);
        }
    }

    private void loadCompletions() {

    }

    public void reloadConfig(CommandSender sender) {
        reloadConfig();
        for (ConfigFactory configFactory : configFactories) {
            configFactory.reload();
        }
        loadUtil(true);
        sender.sendMessage(ColorUtil.apply("<green>Config reloaded!"));
    }

}