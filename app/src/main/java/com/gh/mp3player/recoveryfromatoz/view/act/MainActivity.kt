package com.gh.mp3player.recoveryfromatoz.view.act

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.gh.mp3player.recoveryfromatoz.R
import com.gh.mp3player.recoveryfromatoz.databinding.ActivityMainBinding
import com.gh.mp3player.recoveryfromatoz.view.fragment.MainFragment
import com.gh.mp3player.recoveryfromatoz.view.fragment.Splash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : BaseActivity<ActivityMainBinding>() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView() {
        if (packageManager.checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                packageName
            ) != PackageManager.PERMISSION_GRANTED || packageManager.checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                packageName
            ) != PackageManager.PERMISSION_GRANTED ||packageManager.checkPermission(
                Manifest.permission.POST_NOTIFICATIONS,
                packageName
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.POST_NOTIFICATIONS
                ), 101
            )
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                showFragment(Splash.TAG, null, false, R.id.main)
                delay(3000)
                showFragment(MainFragment.TAG, null, false, R.id.main)
            }
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CoroutineScope(Dispatchers.Main).launch {
                    showFragment(Splash.TAG, null, false, R.id.main)
                    delay(3000)
                    showFragment(MainFragment.TAG, null, false, R.id.main)
                }
            } else {
                finish()
            }
        }
    }
    override fun initViewBinding(): ActivityMainBinding {
      return ActivityMainBinding.inflate(layoutInflater)
    }

    companion object {
        val TAG: String = MainActivity::class.java.name
    }

}