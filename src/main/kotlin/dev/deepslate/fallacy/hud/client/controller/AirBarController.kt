package dev.deepslate.fallacy.hud.client.controller

import dev.deepslate.fallacy.hud.client.StatusBarUI
import dev.deepslate.fallacy.utils.ARGB
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

class AirBarController : StatusBarUI.Controller {
    override val priority: Int = 4

    override val status: StatusBarUI.Status
        get() {
            val player = entity as? Player ?: return StatusBarUI.Status.empty()
            return StatusBarUI.Status(player.airSupply, player.maxAirSupply)
        }

    override val color: ARGB = ARGB.fromHex("#00E6E6")

    override val icon: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/air")

    override fun shouldRender(entity: Entity): Boolean {
        val player = entity as? Player ?: return false

        return player.airSupply < player.maxAirSupply
    }
}