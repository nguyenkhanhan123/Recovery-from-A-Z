package com.gh.mp3player.recoveryfromatoz.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gh.mp3player.recoveryfromatoz.databinding.SplashBinding
import com.gh.mp3player.recoveryfromatoz.viewmodel.CommonViewModel

class Splash: BaseFragment<SplashBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }
    override fun initView() {

    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): SplashBinding {
        return SplashBinding.inflate(layoutInflater)
    }

    companion object {
        val TAG: String=Splash::class.java.name
    }
}