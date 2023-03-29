package wtf.zani.vanillamenu;

import club.maxstats.weave.loader.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaMenu implements ModInitializer {
    public static final Logger logger = LogManager.getLogger();

    @Override
    public void init() {
        logger.info("Initializing VanillaMenu");
    }
}
