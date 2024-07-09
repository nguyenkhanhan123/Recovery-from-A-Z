package com.gh.mp3player.recoveryfromatoz.view.act

import android.annotation.SuppressLint
import android.content.ContentUris
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gh.mp3player.recoveryfromatoz.databinding.RecoveryActivityImgBinding
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import com.gh.mp3player.recoveryfromatoz.view.adapter.ImageAdapter
import com.gh.mp3player.recoveryfromatoz.view.adapter.VideoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecoveryActivityImg : BaseActivity<RecoveryActivityImgBinding>() {
    override fun initView() {
        mbinding.icBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        CoroutineScope(Dispatchers.Main).launch {
            val normalImages = withContext(Dispatchers.IO) { loadNormalImages() }
            val trashImages = withContext(Dispatchers.IO) { loadTrashImages() }
            if (normalImages.isNotEmpty()) {
                mbinding.rvRecovery.adapter = ImageAdapter(normalImages, this@RecoveryActivityImg)
                Toast.makeText(
                    this@RecoveryActivityImg,
                    "Ảnh bình thường: ${normalImages.size},Ảnh trong rác : ${trashImages.size}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@RecoveryActivityImg,
                    "Không tìm thấy ảnh bình thường,Ảnh trong rác : ${trashImages.size}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun initViewBinding(): RecoveryActivityImgBinding {
        return RecoveryActivityImgBinding.inflate(layoutInflater)
    }

    @SuppressLint("Recycle", "Range")
    private fun loadNormalImages(): List<ImageModel> {
        val imageList = mutableListOf<ImageModel>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder
        )

        if (cursor?.moveToFirst() == true) {
            do {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val data = cursor.getString(dataColumn)

                val contentUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageList.add(ImageModel(name, contentUri))
                Log.d("Image", "Image path: $contentUri")
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return imageList
    }

    @SuppressLint("Recycle", "Range")
    private fun loadTrashImages(): List<ImageModel> {
        val imageList = mutableListOf<ImageModel>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Trash%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        if (cursor?.moveToFirst() == true) {
            do {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val data = cursor.getString(dataColumn)

                val contentUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageList.add(ImageModel(name, contentUri))
                Log.d("TrashImage", "Image path: $contentUri")
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return imageList
    }
}
