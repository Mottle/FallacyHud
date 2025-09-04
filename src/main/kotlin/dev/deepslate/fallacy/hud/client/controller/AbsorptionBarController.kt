package dev.deepslate.fallacy.hud.client.controller

import dev.deepslate.fallacy.hud.client.StatusBarUI
import dev.deepslate.fallacy.utils.RGB
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class AbsorptionBarController : StatusBarUI.Controller {

    companion object {
        @JvmStatic
        val COLORS = listOf(
            "#D4AF37",
            "#C2C73B",
            "#8DC337",
            "#36BA77",
            "#4A5BC4",
            "#D89AE2",
            "#DF9DC7",
            "#DFA99D",
            "#D4DF9D",
            "#3E84C6",
            "#B8C1E8",
            "#DFDFDF"
        ).map(RGB::fromHex)

        @JvmStatic
        private val ICON = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full")

        @JvmStatic
        private val HARDCORE_ICON = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full")

        @JvmStatic
        private val OVERLAY = ResourceLocation.withDefaultNamespace("hud/heart/container")
    }

    override val priority: Int = 2

    override val status: StatusBarUI.Status
        get() = if (entity !is LivingEntity) StatusBarUI.Status.empty() else StatusBarUI.Status(
            (entity as LivingEntity).absorptionAmount,
            (entity as LivingEntity).maxAbsorption
        )

    override val color: RGB
        get() = COLORS.first()

    override val icon: ResourceLocation
        get() {
            if (entity == null) return ICON

            val level = entity!!.level()

            if (level.levelData.isHardcore) return HARDCORE_ICON

            return ICON
        }

    override fun shouldRender(entity: Entity): Boolean {
        if (entity !is LivingEntity) return false

        if (entity is Player) {
            if (entity.isSpectator || entity.abilities.instabuild) return false
        }

        return entity.absorptionAmount > 0
    }

    override val iconOverlay: ResourceLocation = OVERLAY
}