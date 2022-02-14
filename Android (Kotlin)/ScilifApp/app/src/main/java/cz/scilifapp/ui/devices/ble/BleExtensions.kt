package cz.scilifapp.ui.devices.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/** UUID of the Client Characteristic Configuration Descriptor (0x2902). */
const val CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805F9B34FB"

// BluetoothGatt

fun BluetoothGatt.printGattTable() {
    if (services.isEmpty()) {
        Timber.i("No service and characteristic available, call discoverServices() first?")
        return
    }
    services.forEach { service ->
        val characteristicsTable = service.characteristics.joinToString(
            separator = "\n|--",
            prefix = "|--"
        ) { char ->
            var description = "${char.uuid}: ${char.printProperties()}"
            if (char.descriptors.isNotEmpty()) {
                description += "\n" + char.descriptors.joinToString(
                    separator = "\n|------",
                    prefix = "|------"
                ) { descriptor ->
                    "${descriptor.uuid}: ${descriptor.printProperties()}"
                }
            }
            description
        }
        Timber.i("Service ${service.uuid}\nCharacteristics:\n$characteristicsTable")
    }
}

fun BluetoothGatt.findCharacteristic(uuid: UUID): BluetoothGattCharacteristic? {
    services?.forEach { service ->
        service.characteristics?.firstOrNull { characteristic ->
            characteristic.uuid == uuid
        }?.let { matchingCharacteristic ->
            return matchingCharacteristic
        }
    }
    return null
}

fun BluetoothGatt.findDescriptor(uuid: UUID): BluetoothGattDescriptor? {
    services?.forEach { service ->
        service.characteristics.forEach { characteristic ->
            characteristic.descriptors?.firstOrNull { descriptor ->
                descriptor.uuid == uuid
            }?.let { matchingDescriptor ->
                return matchingDescriptor
            }
        }
    }
    return null
}

// BluetoothGattCharacteristic

fun BluetoothGattCharacteristic.printProperties(): String = mutableListOf<String>().apply {
    if (isReadable()) {
        add("READABLE")
        if (value != null) {
            add("val:$value")
        }
    }
    if (isWritable()) add("WRITABLE")
    if (isWritableWithoutResponse()) add("WRITABLE WITHOUT RESPONSE")
    if (isIndicatable()) add("INDICATABLE")
    if (isNotifiable()) add("NOTIFIABLE")
    if (isEmpty()) add("EMPTY")
}.joinToString()

fun BluetoothGattCharacteristic.isReadable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

fun BluetoothGattCharacteristic.isWritable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean =
    properties and property != 0

// BluetoothGattDescriptor

fun BluetoothGattDescriptor.printProperties(): String = mutableListOf<String>().apply {
    if (isReadable()) add("READABLE")
    if (isWritable()) add("WRITABLE")
    if (isEmpty()) add("EMPTY")
}.joinToString()

fun BluetoothGattDescriptor.isReadable(): Boolean =
    containsPermission(BluetoothGattDescriptor.PERMISSION_READ)

fun BluetoothGattDescriptor.isWritable(): Boolean =
    containsPermission(BluetoothGattDescriptor.PERMISSION_WRITE)

fun BluetoothGattDescriptor.containsPermission(permission: Int): Boolean =
    permissions and permission != 0

/**
 * Convenience extension function that returns true if this [BluetoothGattDescriptor]
 * is a Client Characteristic Configuration Descriptor.
 */
fun BluetoothGattDescriptor.isCccd() =
    uuid.toString().toUpperCase(Locale.US) == CCC_DESCRIPTOR_UUID.toUpperCase(Locale.US)

// ByteArray

fun ByteArray.toHexString(): String =
    joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }

fun ByteArray.toHexStringWithout0x(): String =
    joinToString(separator = " ") { String.format("%02X", it) }

fun ByteArray.toInt(): Int {
    var result = 0
    var shift = 0
    for (byte in this) {
        result = result or (byte.toInt() shl shift)
        shift += 8
    }
    return result
}

fun ByteArray.toUInt() =
    ((this[0].toUInt() and 0xFFu) shl 24)

//fun ByteArray.toUInt() =
//    ((this[0].toUInt() and 0xFFu) shl 24) or
//            ((this[1].toUInt() and 0xFFu) shl 16) or
//            ((this[2].toUInt() and 0xFFu) shl 8) or
//            (this[3].toUInt() and 0xFFu)

fun Int.toByteArray(): ByteArray {
//    val result = Array<Byte>(java.lang.Integer.BYTES, {0})
//    var _number: Int = this
//    var mask = 0xFF // binary 1111 1111
//    for (i in result.indices) {
//        result[i] = _number.and(mask).toByte()
//        _number = _number.shr(8)
//    }
//    result.reverse()
//    return result.toByteArray()
    return byteArrayOf(this.toByte()!!)
}

fun ByteArray.toArrayOfIntStrings(): ArrayList<String> {
    var ret = ArrayList<String>()
    for (byte in this) {
        ret.add(byte.toUByte().toString())
    }
    return ret
}
