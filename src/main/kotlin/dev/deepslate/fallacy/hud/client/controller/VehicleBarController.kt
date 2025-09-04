package dev.deepslate.fallacy.hud.client.controller

import dev.deepslate.fallacy.hud.client.StatusBarUI
import dev.deepslate.fallacy.utils.RGB
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

class VehicleBarController : StatusBarUI.Controller {

    companion object {
        @JvmStatic
        private val NORMAL_COLORS = listOf("#FF0000", "#FFFF00", "#00FF00").map(RGB::fromHex)

        @JvmStatic
        private val ICON = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_full")

        @JvmStatic
        private val OVERLAY = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_container")
    }

    override val priority: Int = 4

    override val status: StatusBarUI.Status
        get() {
            val entity = entity as? LivingEntity ?: return StatusBarUI.Status.empty()
            return StatusBarUI.Status(entity.health, entity.maxHealth)
        }

    override val color: RGB
        get() {
            entity?.vehicle ?: return RGB(0, 0, 0)
            val ratio = status.ratio

            if (ratio >= 0.75) return NORMAL_COLORS[2]
            if (ratio >= 0.4) return NORMAL_COLORS[1]
            return NORMAL_COLORS[0]
        }

    override val icon: ResourceLocation = ICON

    override val iconOverlay: ResourceLocation = OVERLAY

    override fun shouldRender(entity: Entity): Boolean = entity.vehicle is LivingEntity
}