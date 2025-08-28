package dev.deepslate.fallacy.hud.client

import com.github.wintersteve25.tau.components.base.DynamicUIComponent
import com.github.wintersteve25.tau.components.base.UIComponent
import com.github.wintersteve25.tau.components.layout.Align
import com.github.wintersteve25.tau.components.layout.Center
import com.github.wintersteve25.tau.components.layout.Row
import com.github.wintersteve25.tau.components.layout.Stack
import com.github.wintersteve25.tau.components.utils.Sized
import com.github.wintersteve25.tau.components.utils.Texture
import com.github.wintersteve25.tau.layout.Layout
import com.github.wintersteve25.tau.layout.LayoutSetting
import com.github.wintersteve25.tau.theme.Theme
import com.github.wintersteve25.tau.utils.FlexSizeBehaviour
import com.github.wintersteve25.tau.utils.SimpleVec2i
import com.github.wintersteve25.tau.utils.Size
import dev.deepslate.fallacy.base.client.screen.component.primitive.ColoredTexture
import dev.deepslate.fallacy.hud.TheMod
import dev.deepslate.fallacy.utils.RGB
import net.minecraft.resources.ResourceLocation
import kotlin.math.max

class StatusBarUI(val controller: Controller, val priority: Int = 1) : DynamicUIComponent() {

    interface Controller {
        val status: Status

        val color: RGB

        val icon: ResourceLocation

        val iconOverlay: ResourceLocation? get() = null
    }

    data class Status(val value: Number, val upbound: Number) {
        val ratio get() = (max(0f, value.toFloat()) / max(0.01f, upbound.toFloat())).coerceIn(0f, 1f)

        val valid get() = value.toFloat() > 0 && upbound.toFloat() > 0
    }

    enum class Side {
        LEFT,
        RIGHT
    }

    private var prevSide = Side.LEFT

    var side: Side = prevSide

    companion object {
        val RES: ResourceLocation = ResourceLocation.fromNamespaceAndPath(TheMod.ID, "textures/ui/bar.png")

        const val BAR_U = 2

        const val BAR_V = 11

        const val BAR_WIDTH = 77

        const val BAR_HEIGHT = 5

        const val BAR_BORDER_U = 0

        const val BAR_BORDER_V = 0

        const val BAR_BORDER_WIDTH = 81

        const val BAR_BORDER_HEIGHT = 9

        const val ICON_SIZE = 9

        const val UI_WIDTH = 91

        const val UI_HEIGHT = 9

        const val UI_SPACING = 1
    }

    private var prevStatus = Status(-1e5f, -1e5f)

    override fun tick() {
        val updatedStatus = controller.status
        if (updatedStatus != prevStatus || side != prevSide) {
            prevStatus = updatedStatus
            prevSide = side
            rebuild()
        }
    }

    private fun buildIcon(): UIComponent {
        val icon = ColoredTexture.Builder(controller.icon).withUv(SimpleVec2i(0, 0))
            .withSize(SimpleVec2i(ICON_SIZE, ICON_SIZE)).useSprite(true)

        val overlay = controller.iconOverlay?.let(ColoredTexture::Builder)?.withUv(SimpleVec2i(0, 0))
            ?.withSize(SimpleVec2i(ICON_SIZE, ICON_SIZE))?.useSprite(true)

        val iconComponent = if (overlay != null) {
            Stack.Builder().withSizeBehaviour(FlexSizeBehaviour.MIN).build(overlay, icon)
        } else icon

        return iconComponent
    }

    override fun build(layout: Layout, theme: Theme): UIComponent {
//        val backbone = Sized(Size.staticSize(91, 11), )
        val icon = buildIcon()

        val barInner =
            ColoredTexture.Builder(RES).withUv(SimpleVec2i(BAR_U, BAR_V))
                .withUvSize(SimpleVec2i(BAR_WIDTH, BAR_HEIGHT))
                .withSize(SimpleVec2i((BAR_WIDTH * prevStatus.ratio).toInt(), BAR_HEIGHT))
                .withColor(controller.color)

        val barOuter = Texture.Builder(RES).withUv(SimpleVec2i(BAR_BORDER_U, BAR_BORDER_V))
            .withUvSize(SimpleVec2i(BAR_BORDER_WIDTH, BAR_BORDER_HEIGHT)).withSize(
                SimpleVec2i(BAR_BORDER_WIDTH, BAR_BORDER_HEIGHT)
            )

        val sideBar = when (side) {
            Side.LEFT -> Align.Builder().withHorizontal(LayoutSetting.START).build(barInner)
            Side.RIGHT -> Align.Builder().withHorizontal(LayoutSetting.END).build(barInner)
        }

        val bar = Sized(
            Size.staticSize(BAR_BORDER_WIDTH, BAR_BORDER_HEIGHT),
            Stack.Builder()
                .build(barOuter, Center(Sized(Size.staticSize(SimpleVec2i(BAR_WIDTH, BAR_HEIGHT)), sideBar)))
        )

        val ui =
            if (side == Side.LEFT)
                Sized(Size.staticSize(UI_WIDTH, UI_HEIGHT), Row.Builder().withSpacing(UI_SPACING).build(icon, bar))
            else Sized(Size.staticSize(UI_WIDTH, UI_HEIGHT), Row.Builder().withSpacing(UI_SPACING).build(bar, icon))


        return ui
    }
}