package dev.deepslate.fallacy.hud.client

import dev.deepslate.fallacy.hud.TheMod
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers

@EventBusSubscriber(modid = TheMod.ID)
object Handler {
    @SubscribeEvent
    fun onLayerSet(event: RegisterGuiLayersEvent) {
        val rid = TheMod.withID("hud")
        event.registerBelow(VanillaGuiLayers.SELECTED_ITEM_NAME, rid, LayerRender())
    }
}