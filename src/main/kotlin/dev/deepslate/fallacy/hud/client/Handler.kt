package dev.deepslate.fallacy.hud.client

import dev.deepslate.fallacy.hud.TheMod
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers

@EventBusSubscriber(modid = TheMod.ID)
object Handler {
    @SubscribeEvent
    fun onLayerSet(event: RegisterGuiLayersEvent) {
        val rid = TheMod.withID("hud")
        event.registerBelow(VanillaGuiLayers.SELECTED_ITEM_NAME, rid, LayerRender())
    }

    private val vanillaOverlays = listOf(
        VanillaGuiLayers.AIR_LEVEL, VanillaGuiLayers.ARMOR_LEVEL,
        VanillaGuiLayers.PLAYER_HEALTH, VanillaGuiLayers.VEHICLE_HEALTH, VanillaGuiLayers.FOOD_LEVEL
    )

    //移除原版bar
    @SubscribeEvent
    fun disableVanillaOverlay(event: RenderGuiLayerEvent.Pre) {
        val name = event.name
        if (vanillaOverlays.contains(name)) event.isCanceled = true
    }
}