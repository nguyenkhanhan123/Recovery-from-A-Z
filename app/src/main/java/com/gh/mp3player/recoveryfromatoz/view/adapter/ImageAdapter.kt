package com.gh.mp3player.recoveryfromatoz.view.adapter

import CommonUtils
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gh.mp3player.recoveryfromatoz.R
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import com.gh.mp3player.recoveryfromatoz.view.dialog.DownloadDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ImageAdapter(
    var list: MutableList<ImageModel>,
    private var context: Context,
) : RecyclerView.Adapter<ImageAdapter.GroupHolder>() {

    companion object {
        const val CHANNEL_ID = "DownloadChannel"
        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.item_recovery_img, parent, false)
        return GroupHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        val group: ImageModel = list[position]
        holder.iv.setImageURI(group.imageUri)
        Glide.with(context).load(group.imageUri).into(holder.iv)
        holder.iv.setOnLongClickListener {
            val dialog = DownloadDialog(context)
            dialog.event = View.OnClickListener {
                val s: String
                if(CommonUtils.getInstance().getPref("STT-IMG")=="0"){
                    CommonUtils.getInstance().savePref("STT-IMG", "100")
                }
                val stt = CommonUtils.getInstance().getPref("STT-IMG")
                s = if (stt != null) {
                    CommonUtils.getInstance().savePref("STT-IMG", (stt.toInt() + 1).toString())
                    "RECOVERY FROM A TO Z no$stt.jpg"
                } else {
                    Toast.makeText(context,"NULL",Toast.LENGTH_SHORT).show()
                    ""
                }
                CoroutineScope(Dispatchers.Main).launch {
                    downloadAndSaveImage(context, group.imageUri, s)
                }
            }
            dialog.show()
            true
        }
    }

    private suspend fun downloadAndSaveImage(context: Context, uri: Uri, fileName: String) {
        try {
            showNotification("Downloading...", "Downloading image...")
            val bitmap = downloadImage(context, uri)
            saveImageToDownloads(context, bitmap, fileName)
            updateNotification("Download complete", "Image saved successfully")
        } catch (e: Exception) {
            Log.e("Save Image", "Error downloading image: ${e.message}")
            updateNotification("Download failed", "Error downloading image: ${e.message}")
        }
    }

    private suspend fun downloadImage(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    BitmapFactory.decodeStream(inputStream)
                } else {
                    Log.e("Save Image", "Failed to get input stream from URI")
                    null
                }
            } finally {
                inputStream?.close()
            }
        }
    }

    private suspend fun saveImageToDownloads(context: Context, bitmap: Bitmap?, fileName: String) {
        withContext(Dispatchers.IO) {
            if (bitmap == null) {
                Log.e("Save Image", "Bitmap is null, cannot save image")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                    updateNotification("Download failed", "Bitmap is null, cannot save image")
                }
                return@withContext
            }
            val dir = Environment.getExternalStoragePublicDirectory(Environment.MEDIA_BAD_REMOVAL)
            val newDir = File(dir, "Recovery From A To Z")
            if (!newDir.exists()) {
                newDir.mkdirs()
            }
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(newDir, fileName)
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                Log.d("Save Image", "Image saved to ${file.absolutePath}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Image saved to ${file.absolutePath}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("Save Image", "Failed to save image: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                    updateNotification("Download failed", "Failed to save image: ${e.message}")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class GroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv: ImageView = itemView.findViewById(R.id.image)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, content: String) {
        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title).setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    @SuppressLint("MissingPermission")
    private fun updateNotification(title: String, content: String) {
        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title).setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(false)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download Channel"
            val descriptionText = "Channel for download notifications"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
