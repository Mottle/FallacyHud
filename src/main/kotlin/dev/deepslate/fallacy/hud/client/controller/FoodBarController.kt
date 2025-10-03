package dev.deepslate.fallacy.hud.client.controller

import dev.deepslate.fallacy.hud.client.StatusBarUI
import dev.deepslate.fallacy.utils.ARGB
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class FoodBarController : StatusBarUI.Controller {
    companion object {
        @JvmStatic
        private val ICON = ResourceLocation.withDefaultNamespace("hud/food_full")

        @JvmStatic
        private val HUNGER_DEBUFF_ICON = ResourceLocation.withDefaultNamespace("hud/food_full_hunger")

        @JvmStatic
        private val OVERLAY = ResourceLocation.withDefaultNamespace("hud/food_empty")

        @JvmStatic
        private val COLOR = ARGB.fromHex("#B34D00")

        @JvmStatic
        private val HUNGER_DEBUFF_COLOR = ARGB.fromHex("#249016")

        @JvmStatic
        private val SATURATION_COLOR = ARGB.fromHex("#FFCC00")

        @JvmStatic
        private val SATURATION_DEBUFF_COLOR = ARGB.fromHex("#87BC00")
    }

    override val priority: Int = 1

    override val status: StatusBarUI.Status
        get() {
            val player = entity as? Player ?: return StatusBarUI.Status(-1, -1)
            val foodState = player.foodData
            val upbound = 20
            val value = foodState.foodLevel
            return StatusBarUI.Status(value, upbound)
        }

    override val color: ARGB
        get() = if (entity is Player && (entity as Player).hasEffect(MobEffects.HUNGER)) HUNGER_DEBUFF_COLOR else COLOR

    override val icon: ResourceLocation
        get() = if (entity is Player && (entity as Player).hasEffect(MobEffects.HUNGER)) HUNGER_DEBUFF_ICON else ICON


    override val iconOverlay: ResourceLocation = OVERLAY

    override fun shouldRender(entity: Entity): Boolean {
        if (entity !is LivingEntity) return false

        //只有玩家显示饱食度
        if (entity is Player) {
            return !(entity.isSpectator || entity.abilities.instabuild)
        }

        return false
    }

    override val secondaryStatus: StatusBarUI.Status
        get() {
            val player = entity as? Player ?: return StatusBarUI.Status(-1f, -1f)
            val value = player.foodData.saturationLevel
            return StatusBarUI.Status(value, 20f)
        }

    override val secondaryColor: ARGB
        get() = if (entity is Player && (entity as Player).hasEffect(MobEffects.HUNGER)) SATURATION_DEBUFF_COLOR else SATURATION_COLOR

    override val increment: Number
        get() {
            val player = entity as? Player ?: return 0
            if (!player.mainHandItem.has(DataComponents.FOOD)) return 0
            return player.mainHandItem.get(DataComponents.FOOD)!!.nutrition
        }

    override val secondaryIncrement: Number
        get() {
            val player = entity as? Player ?: return 0
            if (!player.mainHandItem.has(DataComponents.FOOD)) return 0
            return player.mainHandItem.get(DataComponents.FOOD)!!.saturation
        }
}