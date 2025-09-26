package dev.deepslate.fallacy.hud.client.controller

import dev.deepslate.fallacy.hud.client.StatusUI
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class TestStatusController : StatusUI.Controller {
    override val priority: Int = 0

    override val component: Component = Component.literal("lover~").withStyle(ChatFormatting.LIGHT_PURPLE)

    override val icon: ResourceLocation = ResourceLocation.withDefaultNamespace("")

    override fun shouldRender(entity: Entity): Boolean = true
}