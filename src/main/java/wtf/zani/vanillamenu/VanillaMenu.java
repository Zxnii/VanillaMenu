package wtf.zani.vanillamenu;

import club.maxstats.weave.loader.api.HookManager;
import club.maxstats.weave.loader.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import wtf.zani.vanillamenu.hooks.MinecraftClientHook;

public class VanillaMenu implements ModInitializer {
    public static final Logger logger = LogManager.getLogger();

    @Override
    public void preInit(@NotNull HookManager hookManager) {
        logger.info("Adding Vanilla Menu's hook");

        hookManager.register(new MinecraftClientHook());
    }
}
