package com.gh.mp3player.recoveryfromatoz.view.adapter

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gh.mp3player.recoveryfromatoz.R
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import com.gh.mp3player.recoveryfromatoz.view.dialog.DownloadDialog
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageAdapter(private var list: List<ImageModel>, private var context: Context) :
    RecyclerView.Adapter<ImageAdapter.GroupHolder>() {

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
                val bestScore = CommonUtils.getInstance().getPref("BEST")
                if (bestScore != null) {
                    s="RECOVERY FROM A TO Z no$bestScore.jpg"
                    CommonUtils.getInstance().savePref("BEST", (bestScore.toInt()+1).toString())
                }
                else{
                    s="RECOVERY FROM A TO Z no1.jpg"
                    CommonUtils.getInstance().savePref("BEST", (2).toString())
                }
                download(context, group.imageUri, s)
            }
            dialog.show()
            true
        }
    }

    private fun download(context: Context, uri: Uri, fileName: String) {

    }
    override fun getItemCount(): Int {
        return list.size
    }

    class GroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv: ImageView = itemView.findViewById(R.id.image)
    }
}
