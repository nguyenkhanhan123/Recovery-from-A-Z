package com.gh.mp3player.recoveryfromatoz.view.act

import android.net.Uri
import android.widget.MediaController
import android.widget.Toast
import android.media.MediaPlayer
import android.util.DisplayMetrics
import com.gh.mp3player.recoveryfromatoz.databinding.ActivityPlayVideoBinding

class VideoActivity : BaseActivity<ActivityPlayVideoBinding>() {

    override fun initViewBinding(): ActivityPlayVideoBinding {
        return ActivityPlayVideoBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val intent = intent
        val sUri: String = intent?.getStringExtra("key") ?: ""
        val sName:String=intent?.getStringExtra("name")?:""
        Toast.makeText(this, sUri, Toast.LENGTH_SHORT).show()

        val uri = Uri.parse(sUri)
        mbinding.icBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        mbinding.tvNameVideo.text=sName
        mbinding.vvVideo.setVideoURI(uri)

        val mediaController = MediaController(this)
        mbinding.vvVideo.setMediaController(mediaController)
        mediaController.setAnchorView(mbinding.vvVideo)

        mbinding.vvVideo.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // để phát lại video khi kết thúc

            val videoWidth = mediaPlayer.videoWidth
            val videoHeight = mediaPlayer.videoHeight

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels  - dpToPx(60)

            val videoRatio = videoWidth.toFloat() / videoHeight
            val screenRatio = screenWidth.toFloat() / screenHeight

            if (videoRatio > screenRatio) {
                // Video ratio is wider than screen ratio, adjust width
                val newWidth = screenWidth
                val newHeight = (screenWidth / videoRatio).toInt()
                mbinding.vvVideo.layoutParams.width = newWidth
                mbinding.vvVideo.layoutParams.height = newHeight
            } else {
                // Video ratio is taller than screen ratio, adjust height
                val newHeight = screenHeight
                val newWidth = (screenHeight * videoRatio).toInt()
                mbinding.vvVideo.layoutParams.width = newWidth
                mbinding.vvVideo.layoutParams.height = newHeight
            }

            mbinding.vvVideo.requestLayout()

            mediaPlayer.start()
        }

        mbinding.vvVideo.start()
    }

    companion object {
        val TAG: String = VideoActivity::class.java.name
    }
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp.toFloat() * density).toInt()
    }
}
