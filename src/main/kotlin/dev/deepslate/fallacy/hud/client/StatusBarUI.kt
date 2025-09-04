package dev.deepslate.fallacy.hud.client

import com.github.wintersteve25.tau.components.base.DynamicUIComponent
import com.github.wintersteve25.tau.components.base.UIComponent
import com.github.wintersteve25.tau.components.layout.Align
import com.github.wintersteve25.tau.components.layout.Center
import com.github.wintersteve25.tau.components.layout.Row
import com.github.wintersteve25.tau.components.layout.Stack
import com.github.wintersteve25.tau.components.utils.Sized
import com.github.wintersteve25.tau.components.utils.Text
import com.github.wintersteve25.tau.components.utils.Texture
import com.github.wintersteve25.tau.layout.Layout
import com.github.wintersteve25.tau.layout.LayoutSetting
import com.github.wintersteve25.tau.theme.Theme
import com.github.wintersteve25.tau.utils.FlexSizeBehaviour
import com.github.wintersteve25.tau.utils.SimpleVec2i
import com.github.wintersteve25.tau.utils.Size
import dev.deepslate.fallacy.base.TickCollector
import dev.deepslate.fallacy.base.client.screen.component.primitive.ColoredTexture
import dev.deepslate.fallacy.hud.TheMod
import dev.deepslate.fallacy.utils.RGB
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import kotlin.math.max
import kotlin.math.sin

class StatusBarUI(val controller: Controller) : DynamicUIComponent() {

    interface Controller {

        val entity: Entity? get() = Minecraft.getInstance().cameraEntity

        val priority: Int

        val status: Status

        val color: RGB

        val secondaryStatus: Status? get() = null

        val secondaryColor: RGB? get() = null

        val increment: Number get() = 0

        val secondaryIncrement: Number get() = 0

        val icon: ResourceLocation

        val iconOverlay: ResourceLocation? get() = null

        fun shouldRender(entity: Entity): Boolean

        val highlightOnChanged: Boolean get() = false
    }

    data class Status(val value: Number, val upbound: Number) {
        companion object {
            fun empty() = Status(-1, -1)
        }

        val ratio get() = (max(0f, value.toFloat()) / max(0.01f, upbound.toFloat())).coerceIn(0f, 1f)

        val valid get() = value.toFloat() > 0 && upbound.toFloat() > 0

        fun incrementRatio(increment: Number): Float {
            if (value.toFloat() < 0f || upbound.toFloat() <= 0 || ratio >= 0.99f) return 0f
            val incRatio = (increment.toFloat() / upbound.toFloat()).coerceIn(0f, 1f - ratio)
            return incRatio
        }
    }

    enum class Side {
        LEFT,
        RIGHT;

        fun toLayout(): LayoutSetting = if (this == LEFT) LayoutSetting.START else LayoutSetting.END
    }

    class MainBarAnimation {
        private var updateTick = 0

        //当前显示
        private var displayRatio: Float = -1f

        //当前目标
        private var recentTargetRatio: Float = -1f

        val ratio get() = displayRatio

        fun tick(status: Status?) {
            if (status == null) {
                displayRatio = -1f
                recentTargetRatio = -1f
                return
            }

            if (recentTargetRatio == displayRatio) {
                recentTargetRatio = status.ratio
                updateTick = 0
            }

            if (recentTargetRatio != displayRatio) updateTick = (updateTick + 1) % UPDATED_TICKS

            val delta = (recentTargetRatio - displayRatio) / UPDATED_TICKS.toFloat() * (updateTick + 1).toFloat()
            displayRatio += delta
        }

        fun shouldHighlight() = updateTick > 0 && updateTick < UPDATED_TICKS / 2
    }

    class IncreaseBarAnimation {
        var alpha = 0f
            private set

        fun tick(controller: Controller) {
            val time = (System.currentTimeMillis() / 1000.0) * 3.0
            alpha = (sin(time) / 2.0 + 0.5).toFloat()
        }
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

        const val HIGHLIGHT_BAR_BORDER_V = 18

        const val BAR_BORDER_WIDTH = 81

        const val BAR_BORDER_HEIGHT = 9

        const val ICON_SIZE = 9

        const val FONT_WIDTH = 20

        const val UI_SPACING = 1

        const val UI_WIDTH = 91 + FONT_WIDTH + UI_SPACING

        const val UI_HEIGHT = 9

        const val UPDATED_TICKS = 20
    }

    private val mainBarAnimation = MainBarAnimation()

    private val secondMainBarAnimation = MainBarAnimation()

    private val mainBarIncreaseBarAnimation = IncreaseBarAnimation()

    private val secondMainBarIncreaseBarAnimation = IncreaseBarAnimation()

    private var lastTickTime = -1

    override fun tick() {

        val currentTick = TickCollector.clientTickCount

        if (lastTickTime == currentTick) return
        lastTickTime = currentTick

        mainBarAnimation.tick(controller.status)
        secondMainBarAnimation.tick(controller.secondaryStatus)

        mainBarIncreaseBarAnimation.tick(controller)
        secondMainBarIncreaseBarAnimation.tick(controller)

        rebuild() //rebuild 会触发一次额外的tick
//        TheMod.LOGGER.info("bar tick")
//        TheMod.LOGGER.info("tick: ${TickCollector.clientTickCount}")
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

    private fun buildBarInner(ratio: Float, color: RGB) = ColoredTexture.Builder(RES).withUv(SimpleVec2i(BAR_U, BAR_V))
        .withUvSize(SimpleVec2i(BAR_WIDTH, BAR_HEIGHT))
        .withSize(SimpleVec2i((BAR_WIDTH * ratio).toInt(), BAR_HEIGHT))
        .withColor(color)

    private fun buildBarAndBuffer(side: Side): UIComponent {
        val barInner = buildBarInner(mainBarAnimation.ratio, controller.color)
        val barIncrease = buildBarInner(
            controller.status.incrementRatio(controller.increment).coerceIn(0f, 1f - mainBarAnimation.ratio),
            controller.color.copy(alpha = mainBarIncreaseBarAnimation.alpha)
        )
        val barRow = if (side == Side.LEFT) Row.Builder().withSpacing(0).build(barInner, barIncrease) else Row.Builder()
            .withSpacing(0).build(barIncrease, barInner)

        if (secondMainBarAnimation.ratio < 0 || controller.secondaryStatus == null || controller.secondaryColor == null) return barInner

        val secondaryBarInner = buildBarInner(secondMainBarAnimation.ratio, controller.secondaryColor!!)
        val secondaryIncrease = buildBarInner(
            controller.secondaryStatus!!.incrementRatio(controller.secondaryIncrement)
                .coerceIn(0f, 1f - secondMainBarAnimation.ratio),
            controller.secondaryColor!!.copy(alpha = secondMainBarIncreaseBarAnimation.alpha)
        )
        val secondaryRow = if (side == Side.LEFT) Row.Builder().withSpacing(0)
            .build(secondaryBarInner, secondaryIncrease) else Row.Builder()
            .withSpacing(0).build(secondaryIncrease, secondaryBarInner)

        val uis = listOf(barRow, secondaryRow)
            .let { if (secondMainBarAnimation.ratio > mainBarAnimation.ratio) it.reversed() else it }
        val aligns =
            uis.map { ui -> Align.Builder().withHorizontal(side.toLayout()).build(ui) as UIComponent }.toTypedArray()

        return Stack.Builder().build(*aligns)
    }

    private fun buildBarBorder(): UIComponent {
        val v = if (mainBarAnimation.shouldHighlight()) HIGHLIGHT_BAR_BORDER_V else BAR_BORDER_V
        return Texture.Builder(RES).withUv(SimpleVec2i(BAR_BORDER_U, v))
            .withUvSize(SimpleVec2i(BAR_BORDER_WIDTH, BAR_BORDER_HEIGHT)).withSize(
                SimpleVec2i(BAR_BORDER_WIDTH, BAR_BORDER_HEIGHT)
            )
    }

    override fun build(layout: Layout, theme: Theme): UIComponent {

        val textLayoutSide = if (side == Side.LEFT) LayoutSetting.END else LayoutSetting.START

//        val backbone = Sized(Size.staticSize(91, 11), )
        val text = Sized(
            Size.staticSize(FONT_WIDTH, UI_HEIGHT),
            Text.Builder(controller.status.value.toInt().toString())
                .let(Align.Builder().withHorizontal(textLayoutSide).withVertical(LayoutSetting.END)::build)
        )
        val icon = buildIcon()
        val barInner = buildBarAndBuffer(side)
        val barOuter = buildBarBorder()
        val bar = Sized(
            Size.staticSize(BAR_BORDER_WIDTH, BAR_BORDER_HEIGHT),
            Stack.Builder()
                .build(barOuter, Center(Sized(Size.staticSize(SimpleVec2i(BAR_WIDTH, BAR_HEIGHT)), barInner)))
        )
        val ui =
            if (side == Side.LEFT)
                Sized(
                    Size.staticSize(UI_WIDTH, UI_HEIGHT),
                    Row.Builder().withSpacing(UI_SPACING).build(text, icon, bar)
                )
            else Sized(
                Size.staticSize(UI_WIDTH, UI_HEIGHT),
                Row.Builder().withSpacing(UI_SPACING).build(bar, icon, text)
            )


        return ui
    }
}