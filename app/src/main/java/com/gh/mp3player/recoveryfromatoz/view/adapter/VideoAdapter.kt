package com.gh.mp3player.recoveryfromatoz.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.gh.mp3player.recoveryfromatoz.R
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import com.gh.mp3player.recoveryfromatoz.view.act.VideoActivity

class VideoAdapter(private var list: List<ImageModel>, private var context: Context) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
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
            intent.putExtra("key",item.imageUri.toString())
            intent.putExtra("name",item.name)
            context.startActivity(intent)
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

}
