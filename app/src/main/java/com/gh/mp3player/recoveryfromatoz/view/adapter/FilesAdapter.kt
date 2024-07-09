package com.gh.mp3player.recoveryfromatoz.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gh.mp3player.recoveryfromatoz.R
import com.gh.mp3player.recoveryfromatoz.model.ImageModel

class FilesAdapter(private var list: List<ImageModel>, private var context: Context) :
    RecyclerView.Adapter<FilesAdapter.GroupHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.item_recovery_img, parent, false)
        return GroupHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        val group: ImageModel = list[position]
        holder.iv.setImageURI(group.imageUri)
        Glide.with(context)
            .load(group.imageUri)
            .into(holder.iv)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class GroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv: ImageView
        init {
            iv=itemView.findViewById(R.id.image)
        }
    }
}
