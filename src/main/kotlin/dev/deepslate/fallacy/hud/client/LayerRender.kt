package dev.deepslate.fallacy.hud.client

import com.github.wintersteve25.tau.renderer.HudUIRenderer
import dev.deepslate.fallacy.hud.client.controller.*
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.world.entity.player.Player

class LayerRender : LayeredDraw.Layer {

    companion object {
        val INSTANCE = LayerRender()
    }

    val barUI = BarHudUI(
        HealthBarController(),
        FoodBarController(),
        AbsorptionBarController(),
        AirBarController(),
        VehicleBarController()
    )

    var statusUI = StatusHudUI()

    val hud = HudUIRenderer(barUI)

    val status = HudUIRenderer(statusUI)

    val centerUI = CenterHudUI()

    val center = HudUIRenderer(centerUI)

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val mc = Minecraft.getInstance()
        val entity = mc.getCameraEntity()

        if (entity == null || entity !is Player) return
        if (entity.abilities.instabuild || entity.isSpectator) return

        val window = mc.window
        hud.tick()
        hud.render(window, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true))
        statusUI.setY(barUI.getUIY())
        status.tick()
        status.render(window, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true))
        center.tick()
        center.render(window, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true))
    }
}