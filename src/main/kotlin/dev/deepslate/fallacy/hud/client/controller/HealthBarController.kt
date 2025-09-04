package dev.deepslate.fallacy.hud.client.controller

import dev.deepslate.fallacy.hud.client.StatusBarUI
import dev.deepslate.fallacy.utils.RGB
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class HealthBarController : StatusBarUI.Controller {

    companion object {
        @JvmStatic
        private val NORMAL_COLORS = listOf("#FF0000", "#FFFF00", "#00FF00").map(RGB::fromHex)

        @JvmStatic
        private val POISON_COLORS = listOf("#00FF00", "#55FF55", "#00FF00").map(RGB::fromHex)

        @JvmStatic
        private val WITHER_COLORS = listOf("#555555", "#AAAAAA", "#555555").map(RGB::fromHex)

        @JvmStatic
        private val NORMAL_ICON: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/full")

        @JvmStatic
        private val POISON_ICON: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full")

        @JvmStatic
        private val WITHER_ICON: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/withered_full")

        @JvmStatic
        private val HARDCORE_ICON: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full")

        @JvmStatic
        private val HARDCORE_POISON_ICON: ResourceLocation =
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_full")

        @JvmStatic
        private val HARDCORE_WITHER_ICON: ResourceLocation =
            ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_full")

        @JvmStatic
        private val OVERLAY: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/container")
    }

    override val priority: Int = 0

    override fun shouldRender(entity: Entity): Boolean {
        if (entity !is LivingEntity) return false

        if (entity is Player) {
            if (entity.isSpectator || entity.abilities.instabuild) return false
        }

        return true
    }

    override val status: StatusBarUI.Status
        get() {
            val livingEntity = entity as? LivingEntity
            return StatusBarUI.Status(upbound = livingEntity?.maxHealth ?: -1f, value = livingEntity?.health ?: -1f)
        }

    override val color: RGB
        get() {
            val entity = entity as? LivingEntity ?: return RGB(0, 0, 0)
            val ratio = status.ratio
            val colors = if (entity.hasEffect(MobEffects.WITHER)) {
                WITHER_COLORS
            } else if (entity.hasEffect(MobEffects.POISON)) {
                POISON_COLORS
            } else {
                NORMAL_COLORS
            }

            if (ratio >= 0.75) return colors[2]
            if (ratio >= 0.4) return colors[1]
            return colors[0]
        }

    override val icon: ResourceLocation
        get() {
            val entity = entity as? LivingEntity ?: return NORMAL_ICON
            if (entity.level().levelData.isHardcore) {
                return if (entity.hasEffect(MobEffects.WITHER)) {
                    HARDCORE_WITHER_ICON
                } else if (entity.hasEffect(MobEffects.POISON)) {
                    HARDCORE_POISON_ICON
                } else {
                    HARDCORE_ICON
                }
            } else {
                return if (entity.hasEffect(MobEffects.WITHER)) {
                    WITHER_ICON
                } else if (entity.hasEffect(MobEffects.POISON)) {
                    POISON_ICON
                } else {
                    NORMAL_ICON
                }
            }
        }

    override val iconOverlay: ResourceLocation = OVERLAY

    override val highlightOnChanged: Boolean = true
}