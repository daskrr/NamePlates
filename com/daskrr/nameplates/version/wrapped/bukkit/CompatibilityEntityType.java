package com.daskrr.nameplates.version.wrapped.bukkit;

import com.daskrr.nameplates.version.VersionProvider;
import org.bukkit.entity.EntityType;

import java.util.function.Function;

public class CompatibilityEntityType {
    // v1_17_R1
    public static final EntityType DROPPED_ITEM = getByVersion("DROPPED_ITEM", VersionProvider::entityType);
    public static final EntityType EXPERIENCE_ORB = getByVersion("EXPERIENCE_ORB", VersionProvider::entityType);
    public static final EntityType AREA_EFFECT_CLOUD = getByVersion("AREA_EFFECT_CLOUD", VersionProvider::entityType);
    public static final EntityType ELDER_GUARDIAN = getByVersion("ELDER_GUARDIAN", VersionProvider::entityType);
    public static final EntityType WITHER_SKELETON = getByVersion("WITHER_SKELETON", VersionProvider::entityType);
    public static final EntityType STRAY = getByVersion("STRAY", VersionProvider::entityType);
    public static final EntityType EGG = getByVersion("EGG", VersionProvider::entityType);
    public static final EntityType LEASH_HITCH = getByVersion("LEASH_HITCH", VersionProvider::entityType);
    public static final EntityType PAINTING = getByVersion("PAINTING", VersionProvider::entityType);
    public static final EntityType ARROW = getByVersion("ARROW", VersionProvider::entityType);
    public static final EntityType SNOWBALL = getByVersion("SNOWBALL", VersionProvider::entityType);
    public static final EntityType FIREBALL = getByVersion("FIREBALL", VersionProvider::entityType);
    public static final EntityType SMALL_FIREBALL = getByVersion("SMALL_FIREBALL", VersionProvider::entityType);
    public static final EntityType ENDER_PEARL = getByVersion("ENDER_PEARL", VersionProvider::entityType);
    public static final EntityType ENDER_SIGNAL = getByVersion("ENDER_SIGNAL", VersionProvider::entityType);
    public static final EntityType SPLASH_POTION = getByVersion("SPLASH_POTION", VersionProvider::entityType);
    public static final EntityType THROWN_EXP_BOTTLE = getByVersion("THROWN_EXP_BOTTLE", VersionProvider::entityType);
    public static final EntityType ITEM_FRAME = getByVersion("ITEM_FRAME", VersionProvider::entityType);
    public static final EntityType WITHER_SKULL = getByVersion("WITHER_SKULL", VersionProvider::entityType);
    public static final EntityType PRIMED_TNT = getByVersion("PRIMED_TNT", VersionProvider::entityType);
    public static final EntityType FALLING_BLOCK = getByVersion("FALLING_BLOCK", VersionProvider::entityType);
    public static final EntityType FIREWORK = getByVersion("FIREWORK", VersionProvider::entityType);
    public static final EntityType HUSK = getByVersion("HUSK", VersionProvider::entityType);
    public static final EntityType SPECTRAL_ARROW = getByVersion("SPECTRAL_ARROW", VersionProvider::entityType);
    public static final EntityType SHULKER_BULLET = getByVersion("SHULKER_BULLET", VersionProvider::entityType);
    public static final EntityType DRAGON_FIREBALL = getByVersion("DRAGON_FIREBALL", VersionProvider::entityType);
    public static final EntityType ZOMBIE_VILLAGER = getByVersion("ZOMBIE_VILLAGER", VersionProvider::entityType);
    public static final EntityType SKELETON_HORSE = getByVersion("SKELETON_HORSE", VersionProvider::entityType);
    public static final EntityType ZOMBIE_HORSE = getByVersion("ZOMBIE_HORSE", VersionProvider::entityType);
    public static final EntityType ARMOR_STAND = getByVersion("ARMOR_STAND", VersionProvider::entityType);
    public static final EntityType DONKEY = getByVersion("DONKEY", VersionProvider::entityType);
    public static final EntityType MULE = getByVersion("MULE", VersionProvider::entityType);
    public static final EntityType EVOKER_FANGS = getByVersion("EVOKER_FANGS", VersionProvider::entityType);
    public static final EntityType EVOKER = getByVersion("EVOKER", VersionProvider::entityType);
    public static final EntityType VEX = getByVersion("VEX", VersionProvider::entityType);
    public static final EntityType VINDICATOR = getByVersion("VINDICATOR", VersionProvider::entityType);
    public static final EntityType ILLUSIONER = getByVersion("ILLUSIONER", VersionProvider::entityType);
    public static final EntityType MINECART_COMMAND = getByVersion("MINECART_COMMAND", VersionProvider::entityType);
    public static final EntityType BOAT = getByVersion("BOAT", VersionProvider::entityType);
    public static final EntityType MINECART = getByVersion("MINECART", VersionProvider::entityType);
    public static final EntityType MINECART_CHEST = getByVersion("MINECART_CHEST", VersionProvider::entityType);
    public static final EntityType MINECART_FURNACE = getByVersion("MINECART_FURNACE", VersionProvider::entityType);
    public static final EntityType MINECART_TNT = getByVersion("MINECART_TNT", VersionProvider::entityType);
    public static final EntityType MINECART_HOPPER = getByVersion("MINECART_HOPPER", VersionProvider::entityType);
    public static final EntityType MINECART_MOB_SPAWNER = getByVersion("MINECART_MOB_SPAWNER", VersionProvider::entityType);
    public static final EntityType CREEPER = getByVersion("CREEPER", VersionProvider::entityType);
    public static final EntityType SKELETON = getByVersion("SKELETON", VersionProvider::entityType);
    public static final EntityType SPIDER = getByVersion("SPIDER", VersionProvider::entityType);
    public static final EntityType GIANT = getByVersion("GIANT", VersionProvider::entityType);
    public static final EntityType ZOMBIE = getByVersion("ZOMBIE", VersionProvider::entityType);
    public static final EntityType SLIME = getByVersion("SLIME", VersionProvider::entityType);
    public static final EntityType GHAST = getByVersion("GHAST", VersionProvider::entityType);
    public static final EntityType ZOMBIFIED_PIGLIN = getByVersion("ZOMBIFIED_PIGLIN", VersionProvider::entityType);
    public static final EntityType ENDERMAN = getByVersion("ENDERMAN", VersionProvider::entityType);
    public static final EntityType CAVE_SPIDER = getByVersion("CAVE_SPIDER", VersionProvider::entityType);
    public static final EntityType SILVERFISH = getByVersion("SILVERFISH", VersionProvider::entityType);
    public static final EntityType BLAZE = getByVersion("BLAZE", VersionProvider::entityType);
    public static final EntityType MAGMA_CUBE = getByVersion("MAGMA_CUBE", VersionProvider::entityType);
    public static final EntityType ENDER_DRAGON = getByVersion("ENDER_DRAGON", VersionProvider::entityType);
    public static final EntityType WITHER = getByVersion("WITHER", VersionProvider::entityType);
    public static final EntityType BAT = getByVersion("BAT", VersionProvider::entityType);
    public static final EntityType WITCH = getByVersion("WITCH", VersionProvider::entityType);
    public static final EntityType ENDERMITE = getByVersion("ENDERMITE", VersionProvider::entityType);
    public static final EntityType GUARDIAN = getByVersion("GUARDIAN", VersionProvider::entityType);
    public static final EntityType SHULKER = getByVersion("SHULKER", VersionProvider::entityType);
    public static final EntityType PIG = getByVersion("PIG", VersionProvider::entityType);
    public static final EntityType SHEEP = getByVersion("SHEEP", VersionProvider::entityType);
    public static final EntityType COW = getByVersion("COW", VersionProvider::entityType);
    public static final EntityType CHICKEN = getByVersion("CHICKEN", VersionProvider::entityType);
    public static final EntityType SQUID = getByVersion("SQUID", VersionProvider::entityType);
    public static final EntityType WOLF = getByVersion("WOLF", VersionProvider::entityType);
    public static final EntityType MUSHROOM_COW = getByVersion("MUSHROOM_COW", VersionProvider::entityType);
    public static final EntityType SNOWMAN = getByVersion("SNOWMAN", VersionProvider::entityType);
    public static final EntityType OCELOT = getByVersion("OCELOT", VersionProvider::entityType);
    public static final EntityType IRON_GOLEM = getByVersion("IRON_GOLEM", VersionProvider::entityType);
    public static final EntityType HORSE = getByVersion("HORSE", VersionProvider::entityType);
    public static final EntityType RABBIT = getByVersion("RABBIT", VersionProvider::entityType);
    public static final EntityType POLAR_BEAR = getByVersion("POLAR_BEAR", VersionProvider::entityType);
    public static final EntityType LLAMA = getByVersion("LLAMA", VersionProvider::entityType);
    public static final EntityType LLAMA_SPIT = getByVersion("LLAMA_SPIT", VersionProvider::entityType);
    public static final EntityType PARROT = getByVersion("PARROT", VersionProvider::entityType);
    public static final EntityType VILLAGER = getByVersion("VILLAGER", VersionProvider::entityType);
    public static final EntityType ENDER_CRYSTAL = getByVersion("ENDER_CRYSTAL", VersionProvider::entityType);
    public static final EntityType TURTLE = getByVersion("TURTLE", VersionProvider::entityType);
    public static final EntityType PHANTOM = getByVersion("PHANTOM", VersionProvider::entityType);
    public static final EntityType TRIDENT = getByVersion("TRIDENT", VersionProvider::entityType);
    public static final EntityType COD = getByVersion("COD", VersionProvider::entityType);
    public static final EntityType SALMON = getByVersion("SALMON", VersionProvider::entityType);
    public static final EntityType PUFFERFISH = getByVersion("PUFFERFISH", VersionProvider::entityType);
    public static final EntityType TROPICAL_FISH = getByVersion("TROPICAL_FISH", VersionProvider::entityType);
    public static final EntityType DROWNED = getByVersion("DROWNED", VersionProvider::entityType);
    public static final EntityType DOLPHIN = getByVersion("DOLPHIN", VersionProvider::entityType);
    public static final EntityType CAT = getByVersion("CAT", VersionProvider::entityType);
    public static final EntityType PANDA = getByVersion("PANDA", VersionProvider::entityType);
    public static final EntityType PILLAGER = getByVersion("PILLAGER", VersionProvider::entityType);
    public static final EntityType RAVAGER = getByVersion("RAVAGER", VersionProvider::entityType);
    public static final EntityType TRADER_LLAMA = getByVersion("TRADER_LLAMA", VersionProvider::entityType);
    public static final EntityType WANDERING_TRADER = getByVersion("WANDERING_TRADER", VersionProvider::entityType);
    public static final EntityType FOX = getByVersion("FOX", VersionProvider::entityType);
    public static final EntityType BEE = getByVersion("BEE", VersionProvider::entityType);
    public static final EntityType HOGLIN = getByVersion("HOGLIN", VersionProvider::entityType);
    public static final EntityType PIGLIN = getByVersion("PIGLIN", VersionProvider::entityType);
    public static final EntityType STRIDER = getByVersion("STRIDER", VersionProvider::entityType);
    public static final EntityType ZOGLIN = getByVersion("ZOGLIN", VersionProvider::entityType);
    public static final EntityType PIGLIN_BRUTE = getByVersion("PIGLIN_BRUTE", VersionProvider::entityType);
    public static final EntityType AXOLOTL = getByVersion("AXOLOTL", VersionProvider::entityType);
    public static final EntityType GLOW_ITEM_FRAME = getByVersion("GLOW_ITEM_FRAME", VersionProvider::entityType);
    public static final EntityType GLOW_SQUID = getByVersion("GLOW_SQUID", VersionProvider::entityType);
    public static final EntityType GOAT = getByVersion("GOAT", VersionProvider::entityType);
    public static final EntityType MARKER = getByVersion("MARKER", VersionProvider::entityType);
    public static final EntityType FISHING_HOOK = getByVersion("FISHING_HOOK", VersionProvider::entityType);
    public static final EntityType LIGHTNING = getByVersion("LIGHTNING", VersionProvider::entityType);
    public static final EntityType PLAYER = getByVersion("PLAYER", VersionProvider::entityType);
    public static final EntityType UNKNOWN = getByVersion("UNKNOWN", VersionProvider::entityType);

    //v1_8_R1
    public static final EntityType COMPLEX_PART = getByVersion("COMPLEX_PART", VersionProvider::entityType);
    public static final EntityType WEATHER = getByVersion("WEATHER", VersionProvider::entityType);

    private static EntityType getByVersion(String id, Function<String, EntityType> provider) {
        if (VersionProvider.getInstance() == null)
            throw new IllegalStateException("The version provider was not initialized yet, hence an appropriate EntityType could not be determined.");

        return provider.apply(id);
    }
}
