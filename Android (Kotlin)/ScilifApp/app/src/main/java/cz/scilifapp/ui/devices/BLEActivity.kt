package cz.scilifapp.ui.devices

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.provider.Settings
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import cz.scilifapp.BuildConfig
import cz.scilifapp.R
import cz.scilifapp.ui.devices.ble.*
import kotlinx.android.synthetic.main.activity_bleactivity.*
import org.jetbrains.anko.alert
import timber.log.Timber
import java.util.*





private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

class BLEActivity : AppCompatActivity() {

    /*******************************************
     * Properties
     *******************************************/

    val connectionManager = ConnectionManager(this)

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()


    private val ledServiceFilter: ScanFilter = ScanFilter.Builder().setServiceUuid(
        ParcelUuid(UUID.fromString(UUIDs().LCS_UUID_SERVICE))
    ).build()

    private val deviceNameFilter: ScanFilter = ScanFilter.Builder().setDeviceName(
        "BlueIoToy"
    ).build()

    //    ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(deviceMacAddress).build();
    private val scanFilters: List<ScanFilter> = listOf(ledServiceFilter)

    private var isScanning = false
        set(value) {
            field = value
            runOnUiThread { btn_scan.text = if (value) "Stop Scan" else "Start Scan" }
        }

    private val scanResults = mutableListOf<ScanResult>()

    private val scanResultAdapter: ScanResultAdapter by lazy {
        ScanResultAdapter(scanResults) { result ->
            // The following code gets called when the user taps on a BLE scan result displayed in the RecyclerView
            if (isScanning) {
                stopBleScan()
            }
            with(result.device) {
                Toast.makeText(applicationContext, "Connecting to $name | $address", Toast.LENGTH_SHORT).show()
                Timber.w("Connecting to $name | $address")
                connectionManager.connect(this)
                createBond()
            }
        }
    }

    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    /*******************************************
     * Activity function overrides
     *******************************************/

    @SuppressLint("BinaryOperationInTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bleactivity)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        btn_scan.setOnClickListener {
            if (isScanning) stopBleScan() else {
                Toast.makeText(
                    applicationContext,
                    "Don't forget enabling location",
                    Toast.LENGTH_LONG
                )
                    .show()
                promptEnableBluetooth()
                startBleScan()
            }
        }
        setupRecyclerView()

        btn_clearScanResults.setOnClickListener {
            scanResults.clear()
            scanResultAdapter.notifyDataSetChanged()
        }

        // ACTION BAR
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Devices"

        scan_results_recycler_view.isNestedScrollingEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }


    override fun onStart() {
        super.onStart()
        setupSeekBarListeners()
        setupDisconnectBtnListeners()
        btn_enableLocation.setOnClickListener {
            promptEnableLocation()
        }
    }

    // Since the app requires Bluetooth to be enabled before it can do anything,
    // we want to be able to react to the user’s selection for the system alert.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    // If the user agrees to grant the app location access, we’re good to go and can start scanning for BLE devices.
    // Otherwise, we’ll keep asking them to grant location access.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    startBleScan()
                }
            }
        }
    }

    /*******************************************
     * Private functions
     *******************************************/

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    private fun promptEnableLocation() {
        // OPENS LOCATION SETTINGS
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//        Tasks.await(task)

//        println("******************* ${task.isComplete}")


//        Timber.i("isGpsPresent? ${task.result.locationSettingsStates.isGpsPresent}") // crashes the app
//        Timber.i("isLocationPresent? ${task.result.locationSettingsStates.isLocationPresent}")

    }

    private fun startBleScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
            requestLocationPermission()
        } else {
            scanResults.clear()
            scanResultAdapter.notifyDataSetChanged()
            bleScanner.startScan(scanFilters, scanSettings, scanCallback) // TODO here add scanFilters
            isScanning = true
        }
    }

    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        runOnUiThread {
            alert {
                title = "Location permission required"
                message = "Starting from Android M (6.0), the system requires apps to be granted " +
                        "location access in order to scan for BLE devices."
                isCancelable = false
                positiveButton(android.R.string.ok) {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }.show()
        }
    }

    /*
     * setup of the list of scanned devices
     */
    private fun setupRecyclerView() {
        scan_results_recycler_view.apply {
            adapter = scanResultAdapter
            layoutManager = LinearLayoutManager(
                this@BLEActivity,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = scan_results_recycler_view.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun setupSeekBarListeners() {
        seekBar_LEDcontroller1.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (connectionManager.devicesBonded[0] != null) {
                    connectionManager.writeLEDValue(
                        connectionManager.devicesBonded[0]!!,
                        seekBar?.progress!!
                    )
                }
            }
        })

        seekBar_LEDcontroller2.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (connectionManager.devicesBonded[1] != null) {
                    connectionManager.writeLEDValue(
                        connectionManager.devicesBonded[1]!!,
                        seekBar?.progress!!
                    )
                }
            }
        })

        seekBar_LEDcontroller3.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (connectionManager.devicesBonded[2] != null) {
                    connectionManager.writeLEDValue(
                        connectionManager.devicesBonded[2]!!,
                        seekBar?.progress!!
                    )
                }
            }
        })

        seekBar_LEDcontroller4.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (connectionManager.devicesBonded[3] != null) {
                    connectionManager.writeLEDValue(
                        connectionManager.devicesBonded[3]!!,
                        seekBar?.progress!!
                    )
                }
            }
        })

        seekBar_LEDcontroller5.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (connectionManager.devicesBonded[4] != null) {
                    connectionManager.writeLEDValue(
                        connectionManager.devicesBonded[4]!!,
                        seekBar?.progress!!
                    )
                }
            }
        })

        seekBar_LEDcontroller6.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (connectionManager.devicesBonded[5] != null) {
                    connectionManager.writeLEDValue(
                        connectionManager.devicesBonded[5]!!,
                        seekBar?.progress!!
                    )
                }
            }
        })
    }

    private fun setupDisconnectBtnListeners() {
        btn_disconnect1.setOnClickListener {
            if (connectionManager.devicesBonded.getOrNull(0) != null) {
                connectionManager.teardownConnection(connectionManager.devicesBonded[0]!!)
            }
        }
        btn_disconnect2.setOnClickListener {
            if (connectionManager.devicesBonded.getOrNull(1) != null) {
                connectionManager.teardownConnection(connectionManager.devicesBonded[1]!!)
            }
        }

        btn_disconnect3.setOnClickListener {
            if (connectionManager.devicesBonded.getOrNull(2) != null) {
                connectionManager.teardownConnection(connectionManager.devicesBonded[2]!!)
            }
        }
        btn_disconnect4.setOnClickListener {
            if (connectionManager.devicesBonded.getOrNull(3) != null) {
                connectionManager.teardownConnection(connectionManager.devicesBonded[3]!!)
            }
        }

        btn_disconnect5.setOnClickListener {
            if (connectionManager.devicesBonded.getOrNull(4) != null) {
                connectionManager.teardownConnection(connectionManager.devicesBonded[4]!!)
            }
        }
        btn_disconnect6.setOnClickListener {
            if (connectionManager.devicesBonded.getOrNull(5) != null) {
                connectionManager.teardownConnection(connectionManager.devicesBonded[5]!!)
            }
        }

    }

    /*******************************************
     * Callback bodies
     *******************************************/

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else {
                with(result.device) {
                    Timber.i("Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)
                scanResultAdapter.notifyItemInserted(scanResults.size - 1)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("onScanFailed: code $errorCode")
        }
    }

    /*******************************************
     * Extension functions
     *******************************************/

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    /*********************************
     * PUBLIC UI SETTER FUNCTIONS
     */

    fun setLEDLightMode(mode: Int?, index: Int) { // TODO add 6 devices
        when (index) {
            0 -> {
                seekBar_LEDcontroller1.progress = mode ?: 0
            }
            1 -> {
                seekBar_LEDcontroller2.progress = mode ?: 0
            }
            2 -> {
                seekBar_LEDcontroller3.progress = mode ?: 0
            }
            3 -> {
                seekBar_LEDcontroller4.progress = mode ?: 0
            }
            4 -> {
                seekBar_LEDcontroller5.progress = mode ?: 0
            }
            5 -> {
                seekBar_LEDcontroller6.progress = mode ?: 0
            }
            else -> {
                // incorrect index
            }
        }
    }

    fun setRGBValue(values: ArrayList<String>?, index: Int) { // TODO add 6 devices
        when (index) {
            0 -> {
                tV_RGBvalue1R.text = values?.get(0) ?: "??"
                tV_RGBvalue1G.text = values?.get(1) ?: "??"
                tV_RGBvalue1B.text = values?.get(2) ?: "??"
            }
            1 -> {
                tV_RGBvalue2R.text = values?.get(0) ?: "??"
                tV_RGBvalue2G.text = values?.get(1) ?: "??"
                tV_RGBvalue2B.text = values?.get(2) ?: "??"
            }
            2 -> {
                tV_RGBvalue3R.text = values?.get(0) ?: "??"
                tV_RGBvalue3G.text = values?.get(1) ?: "??"
                tV_RGBvalue3B.text = values?.get(2) ?: "??"
            }
            3 -> {
                tV_RGBvalue4R.text = values?.get(0) ?: "??"
                tV_RGBvalue4G.text = values?.get(1) ?: "??"
                tV_RGBvalue4B.text = values?.get(2) ?: "??"
            }
            4 -> {
                tV_RGBvalue5R.text = values?.get(0) ?: "??"
                tV_RGBvalue5G.text = values?.get(1) ?: "??"
                tV_RGBvalue5B.text = values?.get(2) ?: "??"
            }
            5 -> {
                tV_RGBvalue6R.text = values?.get(0) ?: "??"
                tV_RGBvalue6G.text = values?.get(1) ?: "??"
                tV_RGBvalue6B.text = values?.get(2) ?: "??"
            }
            else -> {
                // incorrect index
            }
        }
    }

    fun setDeviceName(name: String?, index: Int) { // TODO add 6 devices
        when (index) {
            0 -> {
                tV_deviceName1.text = name ?: "Unknown"
            }
            1 -> {
                tV_deviceName2.text = name ?: "Unknown"
            }
            2 -> {
                tV_deviceName3.text = name ?: "Unknown"
            }
            3 -> {
                tV_deviceName4.text = name ?: "Unknown"
            }
            4 -> {
                tV_deviceName5.text = name ?: "Unknown"
            }
            5 -> {
                tV_deviceName6.text = name ?: "Unknown"
            }
            else -> {
                // incorrect index
            }
        }
    }

    fun setBatteryLevel(batteryLevel: String?, index: Int) {
        val blInt: Int
        val blPercentage: Int
        var blPrint = "??"
        if (batteryLevel != null) {
            blInt = batteryLevel!!.toInt()
            blPercentage = 100 * (blInt / 255)
            blPrint = blPercentage.toString()
        }
        when (index) {
            0 -> {
                tV_batteryLevel1.text = "$blPrint %"
            }
            1 -> {
                tV_batteryLevel2.text = "$blPrint %"
            }
            2 -> {
                tV_batteryLevel3.text = "$blPrint %"
            }
            3 -> {
                tV_batteryLevel4.text = "$blPrint %"
            }
            4 -> {
                tV_batteryLevel5.text = "$blPrint %"
            }
            5 -> {
                tV_batteryLevel6.text = "$blPrint %"
            }
            else -> {
                // incorrect index
            }
        }
    }

    fun setTemperature(temperature: String?, index: Int) {
        when (index) {
            0 -> {
                tV_temp1.text = "$temperature °C" ?: "?? °C"
            }
            1 -> {
                tV_temp2.text = "$temperature °C" ?: "?? °C"
            }
            2 -> {
                tV_temp3.text = "$temperature °C" ?: "?? °C"
            }
            3 -> {
                tV_temp4.text = "$temperature °C" ?: "?? °C"
            }
            4 -> {
                tV_temp5.text = "$temperature °C" ?: "?? °C"
            }
            5 -> {
                tV_temp6.text = "$temperature °C" ?: "?? °C"
            }
            else -> {
                // incorrect index
            }
        }
    }

    fun setBatteryCharging(charging: String?, index: Int) { // TODO CHANGE VALUES
        if (charging != null) {
            if (charging == "0x01") { // charging
                when (index) {
                    0 -> {
                        iV_battery1.setImageResource(R.drawable.ic_battery_charging)
                    }
                    1 -> {
                        iV_battery2.setImageResource(R.drawable.ic_battery_charging)
                    }
                    2 -> {
                        iV_battery3.setImageResource(R.drawable.ic_battery_charging)
                    }
                    3 -> {
                        iV_battery4.setImageResource(R.drawable.ic_battery_charging)
                    }
                    4 -> {
                        iV_battery5.setImageResource(R.drawable.ic_battery_charging)
                    }
                    5 -> {
                        iV_battery6.setImageResource(R.drawable.ic_battery_charging)
                    }
                    else -> {
                        // incorrect index
                    }
                }
            } else { // not charging
                when (index) {
                    0 -> {
                        iV_battery1.setImageResource(R.drawable.ic_battery_full)
                    }
                    1 -> {
                        iV_battery2.setImageResource(R.drawable.ic_battery_full)
                    }
                    2 -> {
                        iV_battery3.setImageResource(R.drawable.ic_battery_full)
                    }
                    3 -> {
                        iV_battery4.setImageResource(R.drawable.ic_battery_full)
                    }
                    4 -> {
                        iV_battery5.setImageResource(R.drawable.ic_battery_full)
                    }
                    5 -> {
                        iV_battery6.setImageResource(R.drawable.ic_battery_full)
                    }
                    else -> {
                        // incorrect index
                    }
                }
            }
        }
    }
}
