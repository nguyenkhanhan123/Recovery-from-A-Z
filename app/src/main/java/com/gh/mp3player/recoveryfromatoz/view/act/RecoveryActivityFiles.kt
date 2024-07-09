package com.gh.mp3player.recoveryfromatoz.view.act

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.gh.mp3player.recoveryfromatoz.databinding.RecoveryActivityImgBinding
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecoveryActivityFiles : BaseActivity<RecoveryActivityImgBinding>() {

    override fun initView() {
        CoroutineScope(Dispatchers.Main).launch {
            val files = withContext(Dispatchers.IO) { loadFiles() }

            if (files.isNotEmpty()) {
              //  mbinding.rvRecovery.adapter = FilesAdapter(files, this@com.gh.mp3player.recoveryfromatoz.view.act.RecoveryActivityFiles)
            } else {
                Toast.makeText(
                    this@RecoveryActivityFiles,
                    "Không tìm thấy các tệp tin khác",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Toast.makeText(
                this@RecoveryActivityFiles,
                "Số lượng các tệp tin khác: ${files.size}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun initViewBinding(): RecoveryActivityImgBinding {
        return RecoveryActivityImgBinding.inflate(layoutInflater)
    }

    @SuppressLint("Recycle")
    private fun loadFiles(): List<ImageModel> {
        val fileList = mutableListOf<ImageModel>()
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE
        )
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_NONE}" +
                " AND ${MediaStore.Files.FileColumns.MIME_TYPE} NOT IN ('image/jpeg', 'image/png', 'video/mp4', 'video/avi', 'video/x-matroska')"
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Files.getContentUri("external"), projection, selection, null, sortOrder
        )

        if (cursor?.moveToFirst() == true) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))

                val contentUri = MediaStore.Files.getContentUri("external").buildUpon()
                    .appendPath(id.toString()).build()

                fileList.add(ImageModel(name, contentUri))
                Log.d("File", "File path: $data")
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return fileList
    }
}
