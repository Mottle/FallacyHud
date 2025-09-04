package dev.deepslate.fallacy.hud.client

import com.github.wintersteve25.tau.renderer.HudUIRenderer
import dev.deepslate.fallacy.hud.client.controller.*
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.world.entity.player.Player

class LayerRender : LayeredDraw.Layer {

    val barUI = BarHudUI(
        HealthBarController(),
        FoodBarController(),
        AbsorptionBarController(),
        AirBarController(),
        VehicleBarController()
    )

    val hud = HudUIRenderer(barUI)

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val mc = Minecraft.getInstance()
        val entity = mc.getCameraEntity()

        if (entity == null || entity !is Player) return
        if (entity.abilities.instabuild || entity.isSpectator) return

        val window = mc.window
        hud.tick()
        hud.render(window, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true))
    }
}