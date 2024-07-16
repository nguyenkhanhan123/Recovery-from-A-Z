package com.gh.mp3player.recoveryfromatoz.view.act

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.gh.mp3player.recoveryfromatoz.databinding.RecoveryActivityImgBinding
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RecoveryActivityFiles : BaseActivity<RecoveryActivityImgBinding>() {

    override fun initView() {
        val trashFiles = mutableListOf<String>()

        // Đường dẫn đến thư mục thùng rác của Files by Google (đường dẫn này có thể thay đổi tùy thuộc vào thiết bị và phiên bản ứng dụng)
        val trashDir = File(Environment.getExternalStorageDirectory(), "Android/data/com.google.android.apps.nbu.files/trash")

        if (trashDir.exists() && trashDir.isDirectory) {
            trashDir.listFiles()?.forEach { file ->
                if (file.isFile) {
                    trashFiles.add(file.name)
                }
            }
        }

        Toast.makeText(this,trashFiles.size.toString(),Toast.LENGTH_SHORT).show()
    }

    override fun initViewBinding(): RecoveryActivityImgBinding {
        return RecoveryActivityImgBinding.inflate(layoutInflater)
    }


}
