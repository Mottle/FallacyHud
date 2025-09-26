package dev.deepslate.fallacy.hud.client

import com.github.wintersteve25.tau.components.base.DynamicUIComponent
import com.github.wintersteve25.tau.components.base.UIComponent
import com.github.wintersteve25.tau.components.layout.Align
import com.github.wintersteve25.tau.components.layout.Row
import com.github.wintersteve25.tau.components.layout.Stack
import com.github.wintersteve25.tau.components.utils.Sized
import com.github.wintersteve25.tau.components.utils.Text
import com.github.wintersteve25.tau.layout.Layout
import com.github.wintersteve25.tau.layout.LayoutSetting
import com.github.wintersteve25.tau.theme.Theme
import com.github.wintersteve25.tau.utils.FlexSizeBehaviour
import com.github.wintersteve25.tau.utils.SimpleVec2i
import com.github.wintersteve25.tau.utils.Size
import dev.deepslate.fallacy.base.TickCollector
import dev.deepslate.fallacy.base.client.screen.component.primitive.ColoredTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class StatusUI(val controller: Controller) : DynamicUIComponent() {

    companion object {
        const val ICON_SIZE = 9

        const val ICON_TEXT_SPACE = 1

        const val TEXT_WIDTH = 48

        const val UI_WIDTH = ICON_SIZE + ICON_TEXT_SPACE + TEXT_WIDTH

        const val UI_HEIGHT = 9
    }

    interface Controller {
        val priority: Int

        val component: Component

        val icon: ResourceLocation

        val iconOverlay: ResourceLocation? get() = null

        fun shouldRender(entity: Entity): Boolean
    }

    private var lastTickTime = -1

    private fun buildIcon(): UIComponent {
        val icon = ColoredTexture.Builder(controller.icon).withUv(SimpleVec2i(0, 0))
            .withSize(SimpleVec2i(ICON_SIZE, ICON_SIZE)).useSprite(true)

        if (controller.iconOverlay == null) return icon

        val overlay = ColoredTexture.Builder(controller.iconOverlay!!).withUv(SimpleVec2i(0, 0))
            .withSize(SimpleVec2i(ICON_SIZE, ICON_SIZE)).useSprite(true)

        return Stack.Builder().withSizeBehaviour(FlexSizeBehaviour.MIN).build(overlay, icon)
    }

    override fun build(layout: Layout, theme: Theme): UIComponent {
        val icon = buildIcon()
        val text = Text.Builder(controller.component)
            .let(Align.Builder().withHorizontal(LayoutSetting.START).withVertical(LayoutSetting.END)::build)
        val row = Row.Builder().withSpacing(ICON_TEXT_SPACE).build(icon, text)
        val sized = Sized(SimpleVec2i(UI_WIDTH, UI_HEIGHT).let(Size::staticSize), row)
        return sized
    }

    override fun tick() {
        val currentTick = TickCollector.clientTickCount

        if (lastTickTime == currentTick) return

        lastTickTime = currentTick
        rebuild()
    }
}