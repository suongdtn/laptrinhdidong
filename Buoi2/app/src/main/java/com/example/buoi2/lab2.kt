package com.example.buoi2

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class SmartDevice(val name: String, val category: String) {

    var deviceStatus: String = "offline"
        protected set

    open val deviceType = "unknown"

    open fun turnOn() {
        deviceStatus = "on"
    }

    open fun turnOff() {
        deviceStatus = "off"
    }

    open fun printDeviceInfo() {
        println("Tên thiết bị: $name, danh mục: $category, loại: $deviceType")
    }
}

class SmartTvDevice(deviceName: String, deviceCategory: String) :
    SmartDevice(name = deviceName, category = deviceCategory) {

    override val deviceType = "Smart TV"

    private var speakerVolume by RangeRegulator(initialValue = 2, minValue = 0, maxValue = 100)
    private var channelNumber by RangeRegulator(initialValue = 1, minValue = 0, maxValue = 200)

    fun increaseSpeakerVolume() {
        if (deviceStatus == "on") {
            speakerVolume++
            println("Âm lượng tăng lên $speakerVolume.")
        } else {
            println("TV đang tắt. Không thể tăng âm lượng.")
        }
    }

    fun decreaseSpeakerVolume() {
        if (deviceStatus == "on") {
            speakerVolume--
            println("Âm lượng giảm xuống $speakerVolume.")
        } else {
            println("TV đang tắt. Không thể giảm âm lượng.")
        }
    }

    fun nextChannel() {
        if (deviceStatus == "on") {
            channelNumber++
            println("Chuyển lên kênh $channelNumber.")
        } else {
            println("TV đang tắt. Không thể chuyển kênh.")
        }
    }

    fun previousChannel() {
        if (deviceStatus == "on") {
            channelNumber--
            println("Chuyển xuống kênh $channelNumber.")
        } else {
            println("TV đang tắt. Không thể chuyển kênh.")
        }
    }

    override fun turnOn() {
        super.turnOn()
        println(
            "$name đã được bật. Âm lượng được đặt ở mức $speakerVolume và kênh hiện tại là $channelNumber."
        )
    }

    override fun turnOff() {
        super.turnOff()
        println("$name đã tắt")
    }
}

class SmartLightDevice(deviceName: String, deviceCategory: String) :
    SmartDevice(name = deviceName, category = deviceCategory) {

    override val deviceType = "Đèn thông minh"

    private var brightnessLevel by RangeRegulator(initialValue = 0, minValue = 0, maxValue = 100)

    fun increaseBrightness() {
        if (deviceStatus == "on") {
            brightnessLevel++
            println("Độ sáng tăng lên $brightnessLevel.")
        } else {
            println("Đèn đang tắt. Không thể tăng độ sáng.")
        }
    }

    fun decreaseBrightness() {
        if (deviceStatus == "on") {
            brightnessLevel--
            println("Độ sáng giảm xuống $brightnessLevel.")
        } else {
            println("Đèn đang tắt. Không thể giảm độ sáng.")
        }
    }

    override fun turnOn() {
        super.turnOn()
        brightnessLevel = 2
        println("$name đã được bật. Độ sáng hiện tại là $brightnessLevel.")
    }

    override fun turnOff() {
        super.turnOff()
        brightnessLevel = 0
        println("Đèn thông minh đã tắt")
    }
}

class SmartHome(
    private val smartTvDevice: SmartTvDevice,
    private val smartLightDevice: SmartLightDevice
) {
    var deviceTurnOnCount = 0
        private set

    fun turnOnTv() {
        if (smartTvDevice.deviceStatus != "on") {
            deviceTurnOnCount++
            smartTvDevice.turnOn()
        }
    }

    fun turnOffTv() {
        if (smartTvDevice.deviceStatus == "on") {
            deviceTurnOnCount--
            smartTvDevice.turnOff()
        }
    }

    fun increaseTvVolume() = smartTvDevice.increaseSpeakerVolume()
    fun decreaseTvVolume() = smartTvDevice.decreaseSpeakerVolume()
    fun changeTvChannelToNext() = smartTvDevice.nextChannel()
    fun changeTvChannelToPrevious() = smartTvDevice.previousChannel()
    fun printSmartTvInfo() = smartTvDevice.printDeviceInfo()

    fun turnOnLight() {
        if (smartLightDevice.deviceStatus != "on") {
            deviceTurnOnCount++
            smartLightDevice.turnOn()
        }
    }

    fun turnOffLight() {
        if (smartLightDevice.deviceStatus == "on") {
            deviceTurnOnCount--
            smartLightDevice.turnOff()
        }
    }

    fun increaseLightBrightness() = smartLightDevice.increaseBrightness()
    fun decreaseLightBrightness() = smartLightDevice.decreaseBrightness()
    fun printSmartLightInfo() = smartLightDevice.printDeviceInfo()

    fun turnOffAllDevices() {
        turnOffTv()
        turnOffLight()
    }
}

class RangeRegulator(
    initialValue: Int,
    private val minValue: Int,
    private val maxValue: Int
) : ReadWriteProperty<Any?, Int> {

    private var fieldData = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = fieldData

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        if (value in minValue..maxValue) {
            fieldData = value
        }
    }
}

fun main() {
    val tv = SmartTvDevice("Android TV", "Giải trí")
    val light = SmartLightDevice("Google Light", "Tiện ích")
    val smartHome = SmartHome(tv, light)

    smartHome.turnOnTv()
    smartHome.increaseTvVolume()
    smartHome.decreaseTvVolume()
    smartHome.changeTvChannelToNext()
    smartHome.changeTvChannelToPrevious()
    smartHome.printSmartTvInfo()

    smartHome.turnOnLight()
    smartHome.increaseLightBrightness()
    smartHome.decreaseLightBrightness()
    smartHome.printSmartLightInfo()

    println("Số thiết bị đang bật: ${smartHome.deviceTurnOnCount}")
    smartHome.turnOffAllDevices()
    println("Số thiết bị đang bật sau khi tắt tất cả: ${smartHome.deviceTurnOnCount}")
}