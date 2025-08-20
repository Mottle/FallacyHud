package dev.deepslate.fallacy.hud

import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(TheMod.ID)
object TheMod {
    const val ID = "fallacy_hud"

    val LOGGER: Logger = LogManager.getLogger(ID)
}
