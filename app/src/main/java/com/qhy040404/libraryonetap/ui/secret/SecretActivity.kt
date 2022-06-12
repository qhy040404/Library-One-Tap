package com.qhy040404.libraryonetap.ui.secret

import android.net.Uri
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.view.ModifiedVideoView
import com.qhy040404.libraryonetap.base.BaseActivity

class SecretActivity : BaseActivity() {
    override fun init() = Thread(Play()).start()

    override fun getLayoutId(): Int = R.layout.activity_secret

    private inner class Play : Runnable {
        override fun run() {
            val videoView: ModifiedVideoView = findViewById(R.id.videoView)

            videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.lol))
            videoView.start()
        }
    }
}