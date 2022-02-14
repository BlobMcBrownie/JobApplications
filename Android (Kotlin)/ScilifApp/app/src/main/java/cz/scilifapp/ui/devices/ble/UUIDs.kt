package cz.scilifapp.ui.devices.ble

class UUIDs {
    val LCS_UUID_BASE = "00000000-1212-efde-1523-785fef13d123"
    val LCS_UUID_SERVICE = "0000aaaa-1212-efde-1523-785fef13d123"
    val LCS_UUID_DEBUG_LED_CHAR = "0000bbbb-1212-efde-1523-785fef13d123" // Third RGB LED (send 3 bytes)
    val LCS_UUID_DIM_LED_CHAR = "0000cccc-1212-efde-1523-785fef13d123" // External LED (send 1 byte)

    val MS_UUID_BASE = "00000000-1413-f0df-1624-7960f014d224"
    val MS_UUID_SERVICE = "0000aaaa-1413-f0df-1624-7960f014d224"
//    val MS_UUID_BATTERY_LEVEL_CHAR = "0000bbbb-1413-f0df-1624-7960f014d224" // Battery Level
    val MS_UUID_BATTERY_LEVEL_CHAR = "0000bbbb-0000-1000-8000-00805f9b34fb" // Battery Level ALTERNATE WRONG PROBABLY
//    val MS_UUID_BATTERY_CHARGE_CHAR = "0000cccc-1413-f0df-1624-7960f014d224" // Battery charging?
    val MS_UUID_BATTERY_CHARGE_CHAR = "0000cccc-0000-1000-8000-00805f9b34fb" // Battery charging?
//    val MS_UUID_TEMP_CHAR = "0000dddd-1413-f0df-1624-7960f014d224" // Temperature of device
    val MS_UUID_TEMP_CHAR = "0000dddd-0000-1000-8000-00805f9b34fb" // Temperature of device

    val MS_UUID_BATTERY_LEVEL_DESC = "0000bbff-1413-f0df-1624-7960f014d224"

    val SERVICES = listOf(
        LCS_UUID_SERVICE,
        MS_UUID_SERVICE
    )
    val CHARACTERISTICS = listOf(
        LCS_UUID_DEBUG_LED_CHAR,
        LCS_UUID_DIM_LED_CHAR,
        MS_UUID_BATTERY_LEVEL_CHAR,
        MS_UUID_BATTERY_CHARGE_CHAR,
        MS_UUID_TEMP_CHAR
    )
    val NOTIFIABLE_CHARACTERISTICS = listOf(
        LCS_UUID_DIM_LED_CHAR
    )
}