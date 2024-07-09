package com.gh.mp3player.recoveryfromatoz.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import com.gh.mp3player.recoveryfromatoz.R
import com.gh.mp3player.recoveryfromatoz.databinding.DialogBinding

class DownloadDialog(context: Context) : Dialog(context, R.style.CustomDialogStyle) {
    private val mbinding: DialogBinding = DialogBinding.inflate(layoutInflater)
    lateinit var event: View.OnClickListener
    init {
        setContentView(mbinding.root)
        initView()
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
mbinding.tvDownload.setOnClickListener { v->
    mbinding.tvDownload.text="Downloading..."
    event.onClick(v)
    dismiss()
}
        mbinding.tvCancel.setOnClickListener {
            dismiss()
        }
    }
}
