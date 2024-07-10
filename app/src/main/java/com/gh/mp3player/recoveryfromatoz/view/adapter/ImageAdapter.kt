package com.gh.mp3player.recoveryfromatoz.view.adapter

import CommonUtils
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gh.mp3player.recoveryfromatoz.R
import com.gh.mp3player.recoveryfromatoz.model.ImageModel
import com.gh.mp3player.recoveryfromatoz.view.dialog.DownloadDialog

class ImageAdapter(
     var list: MutableList<ImageModel>,
    private var context: Context,
    var isLoadingAdd: Boolean
) : RecyclerView.Adapter<ImageAdapter.GroupHolder>() {
    companion object {
        const val TYPE_ITEM = 1
        const val TYPE_LOADING = 2
    }

    override fun getItemViewType(position: Int): Int {
        if (position == list.size - 1 && isLoadingAdd) {
            return TYPE_LOADING
        }
        return TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val v: View = if (viewType == TYPE_ITEM) {
            LayoutInflater.from(context).inflate(R.layout.item_recovery_img, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_recovery_img_loading, parent, false)
        }
        return GroupHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        if (holder.itemViewType == TYPE_ITEM) {
            val group: ImageModel = list[position]
            holder.iv.setImageURI(group.imageUri)
            Glide.with(context).load(group.imageUri).into(holder.iv)
            holder.iv.setOnLongClickListener {
                val dialog = DownloadDialog(context)
                dialog.event = View.OnClickListener {
                    val s: String
                    val stt = CommonUtils.getInstance().getPref("STT")
                    if (stt != null) {
                        s = "RECOVERY FROM A TO Z no$stt.jpg"
                        CommonUtils.getInstance().savePref("STT", (stt.toInt() + 1).toString())
                    } else {
                        s = "RECOVERY FROM A TO Z no1.jpg"
                        CommonUtils.getInstance().savePref("STT", (2).toString())
                    }
                    download(context, group.imageUri, s)
                }
                dialog.show()
                true
            }
        }
    }

    fun addFooterLoading() {
        isLoadingAdd = true
        list.add(ImageModel("Loading...", Uri.parse("android.resource://${context.packageName}/${R.drawable.ic_loading}")))
    }

    fun removeFooterLoading(){
        isLoadingAdd=false
        val position=list.size-1
        list.removeAt(position)
        notifyItemRemoved(position)
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
