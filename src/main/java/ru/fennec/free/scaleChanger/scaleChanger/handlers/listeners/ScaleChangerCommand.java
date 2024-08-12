package ru.fennec.free.scaleChanger.scaleChanger.handlers.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.fennec.free.scaleChanger.scaleChanger.ScaleChangerPlugin;
import ru.fennec.free.scaleChanger.scaleChanger.common.abstracts.AbstractCommand;
import ru.fennec.free.scaleChanger.scaleChanger.common.configs.ConfigManager;
import ru.fennec.free.scaleChanger.scaleChanger.handlers.database.configs.MainConfig;

import java.util.List;
import java.util.UUID;

public class ScaleChangerCommand extends AbstractCommand {

    private final ScaleChangerPlugin plugin;
    private final ConfigManager<MainConfig> mainConfigManager;
    private MainConfig mainConfig;

    public ScaleChangerCommand(ScaleChangerPlugin plugin, ConfigManager<MainConfig> mainConfigManager) {
        super(plugin, "scalechanger");
        this.plugin = plugin;
        this.mainConfigManager = mainConfigManager;
        this.mainConfig = mainConfigManager.getConfigData();
    }

    /**
     * Доступные команды:
     * /scalechanger info - информация о своём размере
     * /scalechanger help - помощь по командам
     * /scalechanger set <значение> - установить свой размер
     * /scalechanger add <значение> - добавить свой размер
     * /scalechanger remove <значение> - вычесть свой размер
     * /scalechanger player info <ник> - информация об игроке
     * /scalechanger entity info <UUID/near> - информация о сущности
     * /scalechanger player set <ник> <значение> - установить размер игроку
     * /scalechanger player add <ник> <значение> - добавить размер игроку
     * /scalechanger player remove <ник> <значение> - вычесть размер у игрока
     * /scalechanger entity set <UUID/near> <значение> - установить размер сущности
     * /scalechanger entity add <UUID/near> <значение> - добавить размер сущности
     * /scalechanger entity remove <UUID/near> <значение> - вычесть размер у сущности
     */
    @Override
    public void execute(CommandSender commandSender, String label, String[] args) {
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "info" -> infoOwnScale(commandSender);
                    case "about" -> aboutPlugin(commandSender);
                    case "reload" -> reloadPlugin(commandSender);
                    default -> sendHelp(commandSender);
                }
                break;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "set" -> setOwnScale(commandSender, args[1]);
                    case "add" -> addOwnScale(commandSender, args[1]);
                    case "remove", "rem" -> removeOwnScale(commandSender, args[1]);
                    default -> sendHelp(commandSender);
                }
                break;
            case 3:
                if (args[1].equalsIgnoreCase("info")) {
                    if (args[0].equalsIgnoreCase("player")) {
                        sendPlayerScale(commandSender, args[2]);
                    } else if (args[0].equalsIgnoreCase("entity")) {
                        sendEntityScale(commandSender, args[2]);
                    } else {
                        sendHelp(commandSender);
                    }
                } else {
                    sendHelp(commandSender);
                }
                break;
            case 4:
                boolean player = args[0].equalsIgnoreCase("player");
                switch (args[1].toLowerCase()) {
                    case "set":
                        if (player) setPlayerScale(commandSender, args[2], args[3]);
                        else setEntityScale(commandSender, args[2], args[3]);
                        break;
                    case "add":
                        if (player) addPlayerScale(commandSender, args[2], args[3]);
                        else addEntityScale(commandSender, args[2], args[3]);
                        break;
                    case "remove":
                    case "rem":
                        if (player) removePlayerScale(commandSender, args[2], args[3]);
                        else removeEntityScale(commandSender, args[2], args[3]);
                        break;
                    default:
                        sendHelp(commandSender);
                        break;
                }
                break;
            default:
                sendHelp(commandSender);
                break;
        }
    }

    private void aboutPlugin(CommandSender commandSender) {
        commandSender.sendMessage(parseString("<prefix> <white>Плагин ScaleChanger от <green>BuseSo</green> (<green>iBuseWinner</green>). " +
                "Установлена версия <green><version></green></white>",
                Placeholder.parsed("prefix", mainConfig.prefix()), Placeholder.parsed("version", plugin.getPluginMeta().getVersion())));
        commandSender.sendMessage(parseString("<prefix> <white>Ссылка на плагин: <green>https://spigotmc.ru/resources/2440/</green></white>",
                Placeholder.parsed("prefix", mainConfig.prefix())));
    }

    private void reloadPlugin(CommandSender commandSender) {
        if (!commandSender.hasPermission("scalechanger.admin.reload")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        mainConfigManager.reloadConfig(plugin.getLogger());
        mainConfig = mainConfigManager.getConfigData();
    }

    private void sendHelp(CommandSender commandSender) {
        mainConfig.helpPlayerStrings().forEach(string -> commandSender.sendMessage(parseString(string,
                Placeholder.parsed("prefix", mainConfig.prefix()))));
        if (commandSender.hasPermission("scalechanger.admin.help")) {
            mainConfig.helpAdminStrings().forEach(string -> commandSender.sendMessage(parseString(string,
                    Placeholder.parsed("prefix", mainConfig.prefix()))));
        }
    }

    private void infoOwnScale(CommandSender commandSender) {
        if (!commandSender.hasPermission("scalechanger.player.self.info")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(parseString(mainConfig.onlyInGame(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        Player selfPlayer = (Player)commandSender;
        double scale = selfPlayer.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
        commandSender.sendMessage(parseString(mainConfig.infoAboutYourScale(),
                Placeholder.parsed("prefix", mainConfig.prefix()),
                Placeholder.parsed("player_scale", String.valueOf(scale))));
    }

    private void setOwnScale(CommandSender commandSender, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.self.set")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(parseString(mainConfig.onlyInGame(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        Player selfPlayer = (Player)commandSender;

        try {
            double scale = Double.parseDouble(arg);
            if (scale < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (scale > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            selfPlayer.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(scale);
            commandSender.sendMessage(parseString(mainConfig.changeYourScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_scale", String.valueOf(arg))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private void addOwnScale(CommandSender commandSender, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.self.add")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(parseString(mainConfig.onlyInGame(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        Player selfPlayer = (Player)commandSender;

        try {
            double scale = Double.parseDouble(arg);
            double playerScale = selfPlayer.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
            double total = playerScale+scale;
            if (total < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (total > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            selfPlayer.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(total);
            commandSender.sendMessage(parseString(mainConfig.changeYourScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_scale", String.valueOf(total))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private void removeOwnScale(CommandSender commandSender, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.self.remove")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(parseString(mainConfig.onlyInGame(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        Player selfPlayer = (Player)commandSender;

        try {
            double scale = Double.parseDouble(arg);
            double playerScale = selfPlayer.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
            double total = playerScale-scale;
            if (total < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (total > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            selfPlayer.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(total);
            commandSender.sendMessage(parseString(mainConfig.changeYourScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_scale", String.valueOf(total))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private void sendPlayerScale(CommandSender commandSender, String target) {
        if (!commandSender.hasPermission("scalechanger.player.other.info")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null) {
            commandSender.sendMessage(parseString(mainConfig.playerNotFound(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_name", target)));
            return;
        }

        double scale = targetPlayer.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
        commandSender.sendMessage(parseString(mainConfig.infoAboutPlayerScale(),
                Placeholder.parsed("prefix", mainConfig.prefix()),
                Placeholder.parsed("player_name", targetPlayer.getName()),
                Placeholder.parsed("player_scale", String.valueOf(scale))));
    }

    private void setPlayerScale(CommandSender commandSender, String target, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.other.set")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null) {
            commandSender.sendMessage(parseString(mainConfig.playerNotFound(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_name", target)));
            return;
        }

        try {
            double scale = Double.parseDouble(arg);
            if (scale < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (scale > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            targetPlayer.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(scale);
            commandSender.sendMessage(parseString(mainConfig.changeOtherPlayerScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_name", targetPlayer.getName()),
                    Placeholder.parsed("player_scale", String.valueOf(arg))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private void addPlayerScale(CommandSender commandSender, String target, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.other.add")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null) {
            commandSender.sendMessage(parseString(mainConfig.playerNotFound(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_name", target)));
            return;
        }

        try {
            double scale = Double.parseDouble(arg);
            double playerScale = targetPlayer.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
            double total = playerScale+scale;
            if (total < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (total > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            targetPlayer.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(total);
            commandSender.sendMessage(parseString(mainConfig.changeOtherPlayerScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_name", targetPlayer.getName()),
                    Placeholder.parsed("player_scale", String.valueOf(total))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private void removePlayerScale(CommandSender commandSender, String target, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.other.remove")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null) {
            commandSender.sendMessage(parseString(mainConfig.playerNotFound(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_name", target)));
            return;
        }

        try {
            double scale = Double.parseDouble(arg);
            double playerScale = targetPlayer.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
            double total = playerScale-scale;
            if (total < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (total > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            targetPlayer.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(total);
            commandSender.sendMessage(parseString(mainConfig.changeYourScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("player_name", targetPlayer.getName()),
                    Placeholder.parsed("player_scale", String.valueOf(total))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private void sendEntityScale(CommandSender commandSender, String target) {
        if (!commandSender.hasPermission("scalechanger.player.entity.info")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(parseString(mainConfig.onlyInGame(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        LivingEntity livingEntity = parseLivingEntity(target, (Player) commandSender);
        if (livingEntity == null) {
            commandSender.sendMessage(parseString(mainConfig.entityNotFound(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("entity_name", target)));
            return;
        }

        double scale = livingEntity.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
        commandSender.sendMessage(parseString(mainConfig.infoAboutEntityScale(),
                Placeholder.parsed("prefix", mainConfig.prefix()),
                Placeholder.parsed("entity_name", livingEntity.getName()),
                Placeholder.parsed("entity_type", String.valueOf(livingEntity.getType())),
                Placeholder.parsed("entity_uuid", String.valueOf(livingEntity.getUniqueId())),
                Placeholder.parsed("entity_scale", String.valueOf(scale))));
    }

    private void setEntityScale(CommandSender commandSender, String target, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.entity.set")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(parseString(mainConfig.onlyInGame(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        LivingEntity livingEntity = parseLivingEntity(target, (Player) commandSender);
        if (livingEntity == null) {
            commandSender.sendMessage(parseString(mainConfig.entityNotFound(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("entity_name", target)));
            return;
        }

        try {
            double scale = Double.parseDouble(arg);
            if (scale < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (scale > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            livingEntity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(scale);
            commandSender.sendMessage(parseString(mainConfig.changeEntityScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("entity_name", livingEntity.getName()),
                    Placeholder.parsed("entity_type", String.valueOf(livingEntity.getType())),
                    Placeholder.parsed("entity_uuid", String.valueOf(livingEntity.getUniqueId())),
                    Placeholder.parsed("entity_scale", String.valueOf(arg))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private void addEntityScale(CommandSender commandSender, String target, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.entity.add")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(parseString(mainConfig.onlyInGame(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        LivingEntity livingEntity = parseLivingEntity(target, (Player) commandSender);
        if (livingEntity == null) {
            commandSender.sendMessage(parseString(mainConfig.entityNotFound(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("entity_name", target)));
            return;
        }

        try {
            double scale = Double.parseDouble(arg);
            double playerScale = livingEntity.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
            double total = playerScale+scale;
            if (total < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (total > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            livingEntity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(total);
            commandSender.sendMessage(parseString(mainConfig.changeEntityScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("entity_name", livingEntity.getName()),
                    Placeholder.parsed("entity_type", String.valueOf(livingEntity.getType())),
                    Placeholder.parsed("entity_uuid", String.valueOf(livingEntity.getUniqueId())),
                    Placeholder.parsed("entity_scale", String.valueOf(total))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private void removeEntityScale(CommandSender commandSender, String target, String arg) {
        if (!commandSender.hasPermission("scalechanger.player.entity.remove")) {
            commandSender.sendMessage(parseString(mainConfig.noPermission(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(parseString(mainConfig.onlyInGame(),
                    Placeholder.parsed("prefix", mainConfig.prefix())));
            return;
        }

        LivingEntity livingEntity = parseLivingEntity(target, (Player) commandSender);
        if (livingEntity == null) {
            commandSender.sendMessage(parseString(mainConfig.entityNotFound(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("entity_name", target)));
            return;
        }

        try {
            double scale = Double.parseDouble(arg);
            double playerScale = livingEntity.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
            double total = playerScale-scale;
            if (total < mainConfig.minScale()) {
                commandSender.sendMessage(parseString(mainConfig.minScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("min_scale", String.valueOf(mainConfig.minScale()))));
                return;
            }
            if (total > mainConfig.maxScale()) {
                commandSender.sendMessage(parseString(mainConfig.maxScaleLimit(),
                        Placeholder.parsed("prefix", mainConfig.prefix()),
                        Placeholder.parsed("max_scale", String.valueOf(mainConfig.maxScale()))));
                return;
            }

            livingEntity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(total);
            commandSender.sendMessage(parseString(mainConfig.changeEntityScale(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("entity_name", livingEntity.getName()),
                    Placeholder.parsed("entity_type", String.valueOf(livingEntity.getType())),
                    Placeholder.parsed("entity_uuid", String.valueOf(livingEntity.getUniqueId())),
                    Placeholder.parsed("entity_scale", String.valueOf(total))));
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(parseString(mainConfig.mustBeNumber(),
                    Placeholder.parsed("prefix", mainConfig.prefix()),
                    Placeholder.parsed("arg", arg)));
        }
    }

    private Component parseString(String message, TagResolver... tags) {
        return MiniMessage.miniMessage().deserialize(message, tags);
    }

    private LivingEntity getNearestLivingEntity(Entity commandExecutor, Location location) {
        Entity nearestEntity = null;
        double lowestDistanceSoFar = Double.MAX_VALUE;

        List<Entity> nearbyEntities = commandExecutor.getNearbyEntities(25, 25, 25);
        for (Entity entity : nearbyEntities) {
            double distance = location.distance(entity.getLocation());
            if (distance < lowestDistanceSoFar) {
                lowestDistanceSoFar = distance;
                nearestEntity = entity;
            }
        }

        if (nearestEntity == null) return null;
        if (nearestEntity instanceof LivingEntity) return (LivingEntity) nearestEntity;
        return null;
    }

    private LivingEntity parseLivingEntity(String target, Player commandSender) {
        LivingEntity livingEntity;
        try {
            UUID targetUUID = UUID.fromString(target);
            Entity targetEntity = Bukkit.getEntity(targetUUID);
            if (targetEntity == null) {
                return null;
            }

            livingEntity = (LivingEntity) targetEntity;
        } catch (IllegalArgumentException ex) {
            //Try near then
            return getNearestLivingEntity(commandSender, (commandSender).getLocation());
        }
        return livingEntity;
    }
}
