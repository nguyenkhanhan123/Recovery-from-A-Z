package com.gh.mp3player.recoveryfromatoz.view.act

import android.annotation.SuppressLint
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.gh.mp3player.recoveryfromatoz.databinding.RecoveryActivityImgBinding
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import com.gh.mp3player.recoveryfromatoz.view.adapter.VideoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RecoveryActivityVideo : BaseActivity<RecoveryActivityImgBinding>() {

   override fun initView() {
       mbinding.icBack.setOnClickListener {
           onBackPressedDispatcher.onBackPressed()
       }
        CoroutineScope(Dispatchers.Main).launch {
            val normalVideos = withContext(Dispatchers.IO) { loadNormalVideos() }
            val trashVideos = withContext(Dispatchers.IO) { loadTrashVideos() }

            if (normalVideos.isNotEmpty()) {
                mbinding.rvRecovery.adapter = VideoAdapter(normalVideos, this@RecoveryActivityVideo)
            } else {
                Toast.makeText(
                    this@RecoveryActivityVideo,
                    "Không tìm thấy video bình thường",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Toast.makeText(
                this@RecoveryActivityVideo,
                "Video bình thường: ${normalVideos.size}",
                Toast.LENGTH_SHORT
            ).show()

            Toast.makeText(
                this@RecoveryActivityVideo,
                "Video trong rác: ${trashVideos.size}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun initViewBinding(): RecoveryActivityImgBinding {
      return RecoveryActivityImgBinding.inflate(layoutInflater)
    }

    @SuppressLint("Recycle", "Range")
    private fun loadNormalVideos(): List<ImageModel> {
        val videoList = mutableListOf<ImageModel>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder
        )

        if (cursor?.moveToFirst() == true) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))

                val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                videoList.add(ImageModel(name, contentUri))
                Log.d("Video", "Video path: $data")
                //Log.d("Image", "Video URI: $contentUri")
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return videoList
    }

    private fun loadTrashVideos(): List<ImageModel> {
        val videoList = mutableListOf<ImageModel>()
        val trashFolder = File(Environment.getExternalStorageDirectory(), "DCIM/.trash")

        if (trashFolder.exists() && trashFolder.isDirectory) {
            val videoFiles = trashFolder.listFiles { file ->
                file.extension == "mp4" || file.extension == "mkv" || file.extension == "avi"
            }

            videoFiles?.forEach { file ->
                val uri = Uri.fromFile(file)
                videoList.add(ImageModel(file.name, uri))
                Log.d("TrashVideo", "Video path: ${file.absolutePath}")
            }
        }
        return videoList
    }
}
