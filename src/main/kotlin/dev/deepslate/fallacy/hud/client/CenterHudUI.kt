package dev.deepslate.fallacy.hud.client

import com.github.wintersteve25.tau.components.base.DynamicUIComponent
import com.github.wintersteve25.tau.components.base.UIComponent
import com.github.wintersteve25.tau.components.layout.Align
import com.github.wintersteve25.tau.components.layout.Center
import com.github.wintersteve25.tau.components.layout.Spacer
import com.github.wintersteve25.tau.components.layout.Stack
import com.github.wintersteve25.tau.components.utils.Positioned
import com.github.wintersteve25.tau.components.utils.Sized
import com.github.wintersteve25.tau.components.utils.Text
import com.github.wintersteve25.tau.layout.Layout
import com.github.wintersteve25.tau.layout.LayoutSetting
import com.github.wintersteve25.tau.theme.Theme
import com.github.wintersteve25.tau.utils.SimpleVec2i
import com.github.wintersteve25.tau.utils.Size
import dev.deepslate.fallacy.base.TickCollector
import dev.deepslate.fallacy.base.client.screen.component.primitive.ColoredTexture
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class CenterHudUI : DynamicUIComponent() {
    companion object {
        const val ICON_SIZE = 18

        const val UI_HEIGHT = 18

        const val UI_WIDTH = 50
    }

    private var controller: Controller? = null

    fun setController(controller: Controller) {
        this.controller = controller
    }

    interface Controller {
        val icon: ResourceLocation

        val component: Component?

        fun shouldRender(entity: Entity): Boolean
    }

    fun shouldRender(): Boolean {
        val entity = Minecraft.getInstance().cameraEntity ?: return false
        return controller?.shouldRender(entity) ?: false
    }

    override fun build(layout: Layout, theme: Theme): UIComponent {
        if (controller == null || !shouldRender()) return Spacer(SimpleVec2i.zero())

        val icon = ColoredTexture.Builder(controller!!.icon).withUv(SimpleVec2i(0, 0))
            .withUvSize(SimpleVec2i(ICON_SIZE, ICON_SIZE)).withSize(SimpleVec2i(ICON_SIZE, ICON_SIZE)).useSprite(true)
            .let(::Center)
        val text = controller?.component?.let(Text::Builder)
            ?.let(Align.Builder().withVertical(LayoutSetting.END).withHorizontal(LayoutSetting.CENTER)::build)
        val stack = if (text != null) Stack.Builder().build(icon, text) else icon
        val container = Sized(Size.staticSize(UI_WIDTH, UI_HEIGHT), stack)

        val uiY = layout.height - 28 - UI_HEIGHT
        val ui = Positioned(SimpleVec2i(0, uiY), Align.Builder().withHorizontal(LayoutSetting.CENTER).build(container))

        return ui
    }

    private var lastTickTime = -1

    override fun tick() {
        val currentTick = TickCollector.clientTickCount

        if (lastTickTime == currentTick) return
        lastTickTime = currentTick
        rebuild()
    }
}