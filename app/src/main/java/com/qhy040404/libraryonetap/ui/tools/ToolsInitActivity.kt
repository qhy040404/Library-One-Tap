package com.qhy040404.libraryonetap.ui.tools

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.fragment.tools.ToolsInitFragment
import com.qhy040404.libraryonetap.base.BaseActivity

class ToolsInitActivity : BaseActivity() {
    override fun init() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.tools_list_screen, ToolsInitFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

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
                        .setPositiveButton(R.string.ok) { _, _ -> }
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
}