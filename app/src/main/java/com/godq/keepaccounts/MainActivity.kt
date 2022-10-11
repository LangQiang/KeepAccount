package com.godq.keepaccounts

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.godq.accountsa.IAccountService
import com.godq.deeplink.DeepLinkUtils
import com.lazylite.bridge.router.ServiceImpl
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IHostActivity
import com.lazylite.mod.fragmentmgr.OnFragmentStackChangeListener
import com.lazylite.mod.utils.KwSystemSettingUtils
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var viewPager2: ViewPager2? = null

    private val guideController = GuideController()

    private var navHolder: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        App.setMainActivity(this)
        setCustomTheme()
        setContentView(R.layout.activity_main)
        bindFragmentOperation()

        viewPager2 = findViewById(R.id.view_pager2)

        navHolder = findViewById(R.id.nav_holder)

        val pairs: List<Pair<String, Fragment>> = requestAdapterData()
        setAdapter(pairs)

        switchMode()

        DeepLinkUtils.load("test://open/account?type=register").execute()
    }

    fun switchMode(opt:String = GuideController.OPT_INIT) {
        guideController.attachAndChoose(findViewById(R.id.main_guide_view_container), opt) {
            when (it) {
                GuideController.TYPE_USER -> {
                    viewPager2?.setCurrentItem(0, false)
                }
                GuideController.TYPE_MGR -> {
                    viewPager2?.setCurrentItem(1, false)
                }
                else -> {
                    viewPager2?.setCurrentItem(0, false)
                }
            }
        }
    }

    private fun setAdapter(fragments: List<Pair<String, Fragment>>) {
        val mAdapter = HomePageAdapter(this, fragments)
        viewPager2?.isUserInputEnabled = false
        viewPager2?.offscreenPageLimit = 4
        viewPager2?.adapter = mAdapter
    }

    private fun requestAdapterData(): List<Pair<String, Fragment>> {
        val data = ArrayList<Pair<String, Fragment>>()
        data.add(Pair("shop_list",MainLinkHelper.getUserHomeFragment()))
        data.add(Pair("bg_mgr_home",MainLinkHelper.getCMSHomeFragment()))
        return data
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setCustomTheme() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        KwSystemSettingUtils.resetStatusBarBlack(this)

        val decor = window?.decorView?: return
        val insertCtrl = ViewCompat.getWindowInsetsController(decor)
        insertCtrl?.addOnControllableInsetsChangedListener { _, typeMask ->
            Timber.tag("bar").e("typeMask: $typeMask")
        }
        decor.post {
            val insert = ViewCompat.getRootWindowInsets(decor)
            val navHeight = insert?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom
            navHolder?.layoutParams?.apply {
                height = navHeight ?: 0
            }
        }
    }


    private val onFragmentStackChangeListener: OnFragmentStackChangeListener =
        object : OnFragmentStackChangeListener {

            override fun onPushFragment(top: Fragment) {

            }

            override fun onPopFragment(top: Fragment) {

            }

            override fun onShowMainLayer(withBottom: Boolean) {

            }

            override fun onHideMainLayer(isHide: Boolean) {

            }
        }

    private fun bindFragmentOperation() {
        FragmentOperation.getInstance().bind(this, object : IHostActivity {
            override fun onGetManLayerTopFragment(): Fragment? {
                return null
            }

            override fun containerViewId(): Int {
                return R.id.app_fragment_container
            }
        }, onFragmentStackChangeListener)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (FragmentOperation.getInstance().onKeyDown(keyCode, event)) {
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!FragmentOperation.getInstance().close()) {
                try {
                    moveTaskToBack(true)
                } catch (ignore: Exception) {
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}