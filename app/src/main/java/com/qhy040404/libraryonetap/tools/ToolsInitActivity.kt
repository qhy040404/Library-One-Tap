package com.qhy040404.libraryonetap.tools

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.activity.StartUpActivity
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.data.ElectricData
import com.qhy040404.libraryonetap.data.GetPortalData.getPortalData
import com.qhy040404.libraryonetap.data.NetData
import com.qhy040404.libraryonetap.utils.NetworkStateUtils
import com.qhy040404.libraryonetap.utils.PermissionUtils

class ToolsInitActivity : StartUpActivity() {
    override fun init() {}

    override fun getLayoutId(): Int = R.layout.activity_tools_init

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder(this)
                        .setMessage(R.string.gotPermission)
                        .setTitle(R.string.bath_title)
                        .setPositiveButton(R.string.ok) { _, _ ->
                        }
                        .setCancelable(true)
                        .create()
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setMessage(R.string.failPermission)
                        .setTitle(R.string.error)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setCancelable(false)
                        .create()
                        .show()
                }
            }
        }
    }

    fun buttonBath(view: View) {
        val netName = when (NetworkStateUtils().checkNetworkTypeStr(this)) {
            "WIFI" -> NetworkStateUtils().getSSID(this)
            "Cellular" -> "Cellular"
            else -> "Error"
        }

        val permission: Array<String> = arrayOf("android.permission.ACCESS_FINE_LOCATION")

        if (netName == "<unknown ssid>") {
            if (PermissionUtils().checkPermission(this, permission)) {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
            }
        } else if (netName == "DLUT-LingShui") {
            val intent = Intent(this, BathReserveActivity::class.java)
            startActivity(intent)
        } else {
            AlertDialog.Builder(this)
                .setMessage(R.string.networkLimit)
                .setTitle(R.string.bath_title)
                .setPositiveButton(R.string.ok) { _, _ ->
                }
                .setCancelable(false)
                .create()
                .show()
        }
    }

    fun buttonNet(view: View) {
        val makeText = Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT)
        makeText.show()
        Thread(getNet()).start()
    }

    fun buttonElectric(view: View) {
        val makeText = Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT)
        makeText.show()
        Thread(getElectric()).start()
    }

    fun buttonVCard(view: View) {
        val intent = Intent(this, VCardActivity::class.java)
        startActivity(intent)
    }

    private inner class getNet : Runnable {
        override fun run() {
            Looper.prepare()
            val netData = NetData()

            val id: String = GlobalValues.id
            val passwd: String = GlobalValues.passwd

            val data: String = getPortalData(id, passwd, 1)

            val remainFee = netData.getFee(data)
            val usedNet = netData.getDynamicUsedFlow(data)
            val remainNet = netData.getDynamicRemainFlow(data)
            val netMessage =
                getString(R.string.remainNetFeeAndColon) + remainFee + getString(R.string.rmb) + "\n" + getString(
                    R.string.usedNetAndColon
                ) + usedNet + getString(R.string.gigabyte) + "\n" + getString(R.string.remainNetAndColon) + remainNet + getString(
                    R.string.gigabyte
                )

            AlertDialog.Builder(this@ToolsInitActivity)
                .setMessage(netMessage)
                .setTitle(R.string.remainNet)
                .setPositiveButton(R.string.ok) { _, _ ->
                }
                .setCancelable(true)
                .create()
                .show()
            Looper.loop()
        }
    }

    private inner class getElectric : Runnable {
        override fun run() {
            Looper.prepare()
            val electricData = ElectricData()

            val id: String = GlobalValues.id
            val passwd: String = GlobalValues.passwd

            val data: String = getPortalData(id, passwd, 0)

            val SSMC = electricData.getSSMC(data)
            val remainElectric = electricData.getResele(data)
            val electricMessage =
                SSMC + "\n" + getString(R.string.remainElectricAndColon) + remainElectric + getString(
                    R.string.degree
                )

            AlertDialog.Builder(this@ToolsInitActivity)
                .setMessage(electricMessage)
                .setTitle(R.string.remainElectric)
                .setPositiveButton(R.string.ok) { _, _ ->
                }
                .setCancelable(true)
                .create()
                .show()
            Looper.loop()
        }
    }
}