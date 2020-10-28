package me.zeroeightsix.kami.gui.clickgui.window

import me.zeroeightsix.kami.gui.rgui.component.*
import me.zeroeightsix.kami.gui.rgui.windows.ListWindow
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.setting.impl.number.NumberSetting
import me.zeroeightsix.kami.setting.impl.other.BindSetting
import me.zeroeightsix.kami.setting.impl.primitive.BooleanSetting
import me.zeroeightsix.kami.setting.impl.primitive.EnumSetting
import me.zeroeightsix.kami.setting.impl.primitive.StringSetting
import me.zeroeightsix.kami.util.math.Vec2f
import org.lwjgl.input.Keyboard

class ModuleSettingWindow(val module: Module, posX: Float, posY: Float) : ListWindow("", posX, posY, 100.0f, 200.0f, false) {

    override val minWidth: Float get() = 100.0f
    override val minHeight: Float get() = draggableHeight

    override val minimizable get() = false

    var listeningChild: AbstractSlider? = null; private set

    init {
        for (setting in module.settingList) {
            if (setting.name == "Enabled") continue
            when (setting) {
                is BooleanSetting -> SettingButton(setting)
                is NumberSetting -> SettingSlider(setting)
                is EnumSetting -> EnumSlider(setting)
                is StringSetting -> StringButton(setting)
                is BindSetting -> BindButton(setting)
                else -> null
            }?.also {
                children.add(it)
            }
        }
    }

    override fun onDisplayed() {
        super.onDisplayed()
        lastActiveTime = System.currentTimeMillis() + 1000L
        name.value = module.name
    }

    override fun onRelease(mousePos: Vec2f, buttonId: Int) {
        super.onRelease(mousePos, buttonId)
        (hoveredChild as? AbstractSlider)?.let {
            listeningChild = if (it.listening) it
            else null
        }
    }

    override fun onTick() {
        super.onTick()
        if (listeningChild?.listening == false) listeningChild = null
        Keyboard.enableRepeatEvents(listeningChild != null)
    }

    override fun onClosed() {
        super.onClosed()
        listeningChild = null
    }

    override fun onKeyInput(keyCode: Int, keyState: Boolean) {
        listeningChild?.onKeyInput(keyCode, keyState)
    }

}