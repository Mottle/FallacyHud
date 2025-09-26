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

class StatusHudUI(vararg ui: StatusUI.Controller) : DynamicUIComponent() {

    companion object {
        const val UI_SPACING = 4

        const val ROW_UI_NUM = 4

        const val LIST_VIEW_SPACING = 1
    }

    private val statusUIs: MutableList<StatusUI> = ui.map(::StatusUI).toMutableList()

    private var startY = -1

    fun setY(y: Int) {
        startY = y
    }

    fun add(controller: StatusUI.Controller) {
        statusUIs.add(StatusUI(controller))
        statusUIs.sortBy { b -> b.controller.priority }
    }

    private fun buildRow(rowUIs: List<StatusUI>): UIComponent {
        if (rowUIs.size > 4) throw IllegalArgumentException("Too many status bars in a row")

        val size = Size.staticSize(BarHudUI.ROW_WIDTH, StatusUI.UI_HEIGHT)
        return Sized(size, Row.Builder().withSpacing(UI_SPACING).build(rowUIs))
    }

    override fun build(layout: Layout, theme: Theme): UIComponent {
        val entity = Minecraft.getInstance().cameraEntity ?: return Spacer(SimpleVec2i.zero())
        val uiRows = statusUIs.filter { ui -> ui.controller.shouldRender(entity) }
            .chunked(ROW_UI_NUM).map(::buildRow).reversed()
        val list = ListView.Builder().withSpacing(LIST_VIEW_SPACING).build(uiRows)
        val uiY = startY - uiRows.size * (StatusUI.UI_HEIGHT + LIST_VIEW_SPACING) - LIST_VIEW_SPACING
        val ui = Positioned(SimpleVec2i(0, uiY), list)

        return ui
    }

    private var lastTickTime = -1

    override fun tick() {
        val currentTick = TickCollector.clientTickCount

        if (lastTickTime == currentTick) return
        lastTickTime = currentTick

        statusUIs.forEachIndexed { idx, s ->
            try {
                s.tick()
            } catch (_: Throwable) {
//                logger.warn("Failed to render bar $idx")
            }
        }

        rebuild()
    }
}