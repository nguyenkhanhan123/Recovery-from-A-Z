package com.gh.mp3player.recoveryfromatoz.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import com.gh.mp3player.recoveryfromatoz.databinding.MainFragmentBinding
import com.gh.mp3player.recoveryfromatoz.view.act.RecoveryActivityFiles
import com.gh.mp3player.recoveryfromatoz.view.act.RecoveryActivityImg
import com.gh.mp3player.recoveryfromatoz.view.act.RecoveryActivityVideo
import com.gh.mp3player.recoveryfromatoz.viewmodel.CommonViewModel

class MainFragment: BaseFragment<MainFragmentBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        mbinding.ivShowList.setOnClickListener {
            mbinding.main.openDrawer(GravityCompat.START)
        }
        mbinding.llStart.setOnTouchListener { _, _ ->
            true // Ngăn chặn các sự kiện touch bên ngoài
        }
        mbinding.trRecoveryImg.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), RecoveryActivityImg::class.java))
        }
        mbinding.trRecoveryVideo.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), RecoveryActivityVideo::class.java))
        }
        mbinding.trRecoveryFiles.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), RecoveryActivityFiles::class.java))
        }
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MainFragmentBinding {
        return MainFragmentBinding.inflate(layoutInflater)
    }

    companion object {
        val TAG: String=MainFragment::class.java.name
    }
}