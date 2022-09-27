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
import com.godq.keepaccounts.mgrbg.BillMgrHomeFragment
import com.godq.keepaccounts.shop.ShopListFragment
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IHostActivity
import com.lazylite.mod.fragmentmgr.OnFragmentStackChangeListener
import com.lazylite.mod.utils.KwSystemSettingUtils
import com.lazylite.mod.widget.vp.NoScrollViewPager
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var mViewPager: NoScrollViewPager? = null

    private val guideController = GuideController()

    private var navHolder: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        App.setMainActivity(this)
        setCustomTheme()
        setContentView(R.layout.activity_main)
        bindFragmentOperation()

        mViewPager = findViewById(R.id.app_main_vp)

        navHolder = findViewById(R.id.nav_holder)

        val pairs: List<Pair<String, Fragment>> = requestAdapterData()
        setAdapter(pairs)

        switchMode()
    }

    fun switchMode(opt:String = GuideController.OPT_INIT) {
        guideController.attachAndChoose(findViewById(R.id.main_guide_view_container), opt) {
            when (it) {
                GuideController.TYPE_USER -> {
                    mViewPager?.currentItem = 0
                }
                GuideController.TYPE_MGR -> {
                    mViewPager?.currentItem = 1
                }
                else -> {
                    mViewPager?.currentItem = 0
                }
            }
        }
    }

    private fun setAdapter(fragments: List<Pair<String, Fragment>>) {
        val mAdapter = HomePageAdapter(supportFragmentManager, fragments)
        mViewPager?.setCanScroll(false)
        mViewPager?.offscreenPageLimit = 4
        mViewPager?.adapter = mAdapter
    }

    private fun requestAdapterData(): List<Pair<String, Fragment>> {
        val data = ArrayList<Pair<String, Fragment>>()
        data.add(Pair("shop_list",ShopListFragment()))
        data.add(Pair("bg_mgr_home",BillMgrHomeFragment()))
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