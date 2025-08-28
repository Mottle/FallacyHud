package dev.deepslate.fallacy.hud.client.controller

import dev.deepslate.fallacy.hud.client.StatusBarUI
import dev.deepslate.fallacy.utils.RGB
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffects

object HealthBarController : StatusBarUI.Controller {

    private val NORMAL_COLORS = listOf("#FF0000", "#FFFF00", "#00FF00").map(RGB::fromHex)

    private val POISON_COLORS = listOf("#00FF00", "#55FF55", "#00FF00").map(RGB::fromHex)

    private val WITHER_COLORS = listOf("#555555", "#AAAAAA", "#555555").map(RGB::fromHex)

    val NORMAL_ICON: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/full")

    val POISON_ICON: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full")

    val WITHER_ICON: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/withered_full")

    val HARDCORE_ICON: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full")

    val HARDCORE_POISON_ICON: ResourceLocation =
        ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_full")

    val HARDCORE_WITHER_ICON: ResourceLocation =
        ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_full")

    val OVERLAY: ResourceLocation = ResourceLocation.withDefaultNamespace("hud/heart/container")

    override val status: StatusBarUI.Status
        get() {
            val player = Minecraft.getInstance().player
            return StatusBarUI.Status(upbound = player?.maxHealth ?: -1f, value = player?.health ?: -1f)
        }

    override val color: RGB
        get() {
            val player = Minecraft.getInstance().player ?: return RGB(0, 0, 0)
            val ratio = status.ratio
            val colors = if (player.hasEffect(MobEffects.WITHER)) {
                WITHER_COLORS
            } else if (player.hasEffect(MobEffects.POISON)) {
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
            val player = Minecraft.getInstance().player ?: return NORMAL_ICON
            if (player.level().levelData.isHardcore) {
                return if (player.hasEffect(MobEffects.WITHER)) {
                    HARDCORE_WITHER_ICON
                } else if (player.hasEffect(MobEffects.POISON)) {
                    HARDCORE_POISON_ICON
                } else {
                    HARDCORE_ICON
                }
            } else {
                return if (player.hasEffect(MobEffects.WITHER)) {
                    WITHER_ICON
                } else if (player.hasEffect(MobEffects.POISON)) {
                    POISON_ICON
                } else {
                    NORMAL_ICON
                }
            }
        }

    override val iconOverlay: ResourceLocation
        get() = OVERLAY
}