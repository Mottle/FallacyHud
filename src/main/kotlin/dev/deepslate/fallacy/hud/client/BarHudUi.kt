package dev.deepslate.fallacy.hud.client

import com.github.wintersteve25.tau.components.base.DynamicUIComponent
import com.github.wintersteve25.tau.components.base.UIComponent
import com.github.wintersteve25.tau.layout.Layout
import com.github.wintersteve25.tau.theme.Theme

class BarHudUi(vararg ui: StatusBarUI) : DynamicUIComponent() {

    private val statusBars: MutableList<StatusBarUI> = ui.toMutableList()

    fun add(hud: StatusBarUI) {
        statusBars.add(hud)
        statusBars.sortBy(StatusBarUI::priority)
    }

    override fun build(layout: Layout, theme: Theme): UIComponent {
        TODO("Not yet implemented")
    }

    override fun tick() {
        statusBars.forEachIndexed { idx, bar ->
            bar.side = if (idx % 2 == 0) StatusBarUI.Side.LEFT else StatusBarUI.Side.RIGHT
            bar.tick()
        }
    }
}