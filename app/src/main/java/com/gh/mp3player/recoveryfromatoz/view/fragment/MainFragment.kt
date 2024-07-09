package com.gh.mp3player.recoveryfromatoz.view.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.gh.mp3player.recoveryfromatoz.databinding.MainFragmentBinding
import com.gh.mp3player.recoveryfromatoz.view.act.RecoveryActivityFiles
import com.gh.mp3player.recoveryfromatoz.view.act.RecoveryActivityImg
import com.gh.mp3player.recoveryfromatoz.view.act.RecoveryActivityVideo
import com.gh.mp3player.recoveryfromatoz.viewmodel.CommonViewModel

class MainFragment: BaseFragment<MainFragmentBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }
    override fun initView() {
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