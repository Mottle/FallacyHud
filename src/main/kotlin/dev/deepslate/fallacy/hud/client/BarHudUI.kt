package dev.deepslate.fallacy.hud.client

import com.github.wintersteve25.tau.components.base.DynamicUIComponent
import com.github.wintersteve25.tau.components.base.UIComponent
import com.github.wintersteve25.tau.components.interactable.ListView
import com.github.wintersteve25.tau.components.layout.Row
import com.github.wintersteve25.tau.components.layout.Spacer
import com.github.wintersteve25.tau.components.utils.Positioned
import com.github.wintersteve25.tau.components.utils.Sized
import com.github.wintersteve25.tau.layout.Layout
import com.github.wintersteve25.tau.theme.Theme
import com.github.wintersteve25.tau.utils.SimpleVec2i
import com.github.wintersteve25.tau.utils.Size
import dev.deepslate.fallacy.base.TickCollector
import net.minecraft.client.Minecraft
import org.slf4j.LoggerFactory

class BarHudUI(vararg ui: StatusBarUI.Controller) : DynamicUIComponent() {

    companion object {
        const val BAR_DEFAULT_POS_Y = 30

        const val BAR_SPACE = 20

        const val ROW_UI_NUM = 2

        const val ROW_WIDTH = StatusBarUI.UI_WIDTH * ROW_UI_NUM + BAR_SPACE

        const val ROW_HEIGHT = StatusBarUI.UI_HEIGHT

        const val LIST_VIEW_SPACING = 1
    }

    private val logger = LoggerFactory.getLogger(BarHudUI::class.java)

    private val statusBars: MutableList<StatusBarUI> = ui.map(::StatusBarUI).toMutableList()

    private var rowNum = -1

    private var layout: Layout? = null

    fun add(controller: StatusBarUI.Controller) {
        statusBars.add(StatusBarUI(controller))
        statusBars.sortBy { b -> b.controller.priority }
    }

    fun removeBy(clazz: Class<*>) {
        statusBars.removeIf { b -> b.controller.javaClass == clazz }
    }

    private fun buildRow(lr: List<StatusBarUI>): UIComponent {
        val size = Size.staticSize(ROW_WIDTH, ROW_HEIGHT)
        lr.first().side = StatusBarUI.Side.LEFT
        if (lr.size > 1) lr.last().side = StatusBarUI.Side.RIGHT
        return Sized(size, Row.Builder().withSpacing(BAR_SPACE).build(lr))
    }

    override fun build(layout: Layout, theme: Theme): UIComponent {

        val entity = Minecraft.getInstance().cameraEntity ?: return Spacer(SimpleVec2i.zero())
        this.layout = layout
        val rows = statusBars.filter { bar -> bar.controller.shouldRender(entity) }
            .chunked(ROW_UI_NUM).map(::buildRow).reversed()
        val list = ListView.Builder().withSpacing(LIST_VIEW_SPACING).build(rows)
        rowNum = rows.size
        val uiY = getUIY()
//            layout.height - BAR_DEFAULT_POS_Y - rows.size * (StatusBarUI.UI_HEIGHT + LIST_VIEW_SPACING) - LIST_VIEW_SPACING
        val ui = Positioned(SimpleVec2i(0, uiY), list)
        return ui
    }

    private var lastTickTime = -1

    override fun tick() {
        val currentTick = TickCollector.clientTickCount

        if (lastTickTime == currentTick) return
        lastTickTime = currentTick

        statusBars.forEachIndexed { idx, bar ->
            try {
                bar.tick()
            } catch (_: Throwable) {
                logger.warn("Failed to render bar $idx")
            }
        }

        rebuild()
    }

    fun getUIY() =
        layout!!.height - BAR_DEFAULT_POS_Y - rowNum * (StatusBarUI.UI_HEIGHT + LIST_VIEW_SPACING) - LIST_VIEW_SPACING

    fun getTopY() = layout!!.height - BAR_DEFAULT_POS_Y
}