package me.zeroeightsix.kami.gui.rgui.component

import me.zeroeightsix.kami.module.modules.client.GuiColors
import me.zeroeightsix.kami.setting.impl.number.FloatSetting
import me.zeroeightsix.kami.setting.impl.number.IntegerSetting
import me.zeroeightsix.kami.setting.impl.number.NumberSetting
import me.zeroeightsix.kami.util.graphics.VertexHelper
import me.zeroeightsix.kami.util.graphics.font.FontRenderAdapter
import me.zeroeightsix.kami.util.math.MathUtils
import me.zeroeightsix.kami.util.math.Vec2f
import org.lwjgl.input.Keyboard
import kotlin.math.floor
import kotlin.math.round

class SettingSlider(val setting: NumberSetting<*>) : AbstractSlider(setting.name, 0.0) {

    private val range = setting.max.toDouble() - setting.min.toDouble()
    private val settingValueDouble get() = setting.value.toDouble()
    private val settingStep = if (setting.step.toDouble() > 0.0) setting.step else getDefaultStep()
    private val stepDouble = settingStep.toDouble()
    private val places = when (setting) {
        is IntegerSetting -> 1
        is FloatSetting -> MathUtils.decimalPlaces(settingStep.toFloat())
        else -> MathUtils.decimalPlaces(settingStep.toDouble())
    }

    private var preDragMousePos = Vec2f(0.0f, 0.0f)

    private fun getDefaultStep() = when (setting) {
        is IntegerSetting -> range / 20
        is FloatSetting -> range / 20.0f
        else -> range / 20.0
    }

    override fun onTick() {
        super.onTick()
        if (mouseState != MouseState.DRAG) {
            val min = setting.min.toDouble()
            val flooredSettingValue = floor((settingValueDouble - min) / stepDouble) * stepDouble
            if (value * range + min !in (flooredSettingValue - stepDouble)..(flooredSettingValue + stepDouble)) {
                value = (setting.value.toDouble() - min) / range
            }
        }
        visible.value = setting.isVisible
    }

    override fun onClick(mousePos: Vec2f, buttonId: Int) {
        super.onClick(mousePos, buttonId)
        preDragMousePos = mousePos
        updateValue(mousePos)
    }

    override fun onDrag(mousePos: Vec2f, clickPos: Vec2f, buttonId: Int) {
        super.onDrag(mousePos, clickPos, buttonId)
        updateValue(mousePos)
    }

    private fun updateValue(mousePos: Vec2f) {
        value = if (!Keyboard.isKeyDown(Keyboard.KEY_LMENU)) mousePos.x.toDouble() / width.value.toDouble()
        else (preDragMousePos.x + (mousePos.x - preDragMousePos.x) * 0.1) / width.value.toDouble()

        val roundedValue = MathUtils.round(round((value * range + setting.min.toDouble()) / stepDouble) * stepDouble, places)
        setting.setValue(roundedValue.toString())
    }

    override fun onRender(vertexHelper: VertexHelper, absolutePos: Vec2f) {
        val valueText = setting.toString()
        protectedWidth = FontRenderAdapter.getStringWidth(valueText, 0.75f).toDouble()

        super.onRender(vertexHelper, absolutePos)
        val posX = (renderWidth - protectedWidth - 2.0f).toFloat()
        val posY = renderHeight - 2.0f - FontRenderAdapter.getFontHeight(0.75f)
        FontRenderAdapter.drawString(valueText, posX, posY, color = GuiColors.text, scale = 0.75f)
    }

}