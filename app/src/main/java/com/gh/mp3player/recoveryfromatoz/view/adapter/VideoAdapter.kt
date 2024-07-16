package com.gh.mp3player.recoveryfromatoz.view.adapter

import CommonUtils
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.gh.mp3player.recoveryfromatoz.R
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import com.gh.mp3player.recoveryfromatoz.view.act.VideoActivity
import com.gh.mp3player.recoveryfromatoz.view.dialog.DownloadDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.channels.FileChannel

class VideoAdapter(private var list: List<ImageModel>, private var context: Context) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    companion object {
        const val CHANNEL_ID = "DownloadChannel"
        const val NOTIFICATION_ID = 2
    }

    init {
        createNotificationChannel()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_recovery_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item: ImageModel = list[position]
        val bitmap = retrieveVideoFrameFromUri(item.imageUri)
        if (bitmap != null) {
            holder.iv.setImageBitmap(bitmap)
        }
        holder.cv.setOnClickListener {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("key", item.imageUri.toString())
            intent.putExtra("name", item.name)
            context.startActivity(intent)
        }
        holder.cv.setOnLongClickListener {
            val dialog = DownloadDialog(context)
            dialog.event = View.OnClickListener {
                val s: String
                if(CommonUtils.getInstance().getPref("STT-MP4")=="0"){
                    CommonUtils.getInstance().savePref("STT-MP4", "100")
                }
                val stt = CommonUtils.getInstance().getPref("STT-MP4")
                s = if (stt != null) {
                    CommonUtils.getInstance().savePref("STT-MP4", (stt.toInt() + 1).toString())
                    "RECOVERY FROM A TO Z no$stt.mp4"
                } else {
                    Toast.makeText(context,"NULL",Toast.LENGTH_SHORT).show()
                    ""
                }
                CoroutineScope(Dispatchers.Main).launch {
                    downloadAndSaveVideo(context, item.imageUri, s)
                }
            }
            dialog.show()
            true
        }
    }

    private suspend fun downloadAndSaveVideo(context: Context, uri: Uri, fileName: String) {
        try {
            showNotification("Downloading...", "Downloading video...")
            copyFileToDownloads(context, uri, fileName)
            updateNotification("Download complete", "Video saved successfully")
        } catch (e: Exception) {
            Log.e("Save Video", "Error downloading video: ${e.message}")
            updateNotification("Download failed", "Error downloading video: ${e.message}")
        }
    }

    private suspend fun copyFileToDownloads(context: Context, uri: Uri, fileName: String) {
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Failed to get input stream from URI")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val newDir = File(downloadsDir, "Recovery From A To Z")
            if (!newDir.exists()) {
                newDir.mkdirs()
            }
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val file = File(newDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("Save Video", "Video saved to ${file.absolutePath}")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Video saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun retrieveVideoFrameFromUri(uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            retriever.getFrameAtTime(1000000) // Lấy khung hình tại 1 giây (1000000 microseconds)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv: ImageView = itemView.findViewById(R.id.image)
        val cv: LinearLayout = itemView.findViewById(R.id.yourLL)
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
