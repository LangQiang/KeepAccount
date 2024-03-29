package com.godq.keepaccounts.main

import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.godq.compose.botnav.BottomItemData
import com.godq.compose.botnav.BottomLayoutView
import com.godq.compose.botnav.BottomNavAdapter
import com.godq.compose.botnav.wrapper.ViewPager2Wrapper
import com.godq.deeplink.DeepLinkUtils
import com.godq.keepaccounts.MainLinkHelper
import com.godq.keepaccounts.R
import com.godq.keepaccounts.decorate.DecorateController
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IHostActivity
import com.lazylite.mod.fragmentmgr.OnFragmentStackChangeListener
import com.lazylite.mod.utils.KwSystemSettingUtils


class MainActivity : AppCompatActivity() {

    private var viewPager2: ViewPager2? = null

    private var bottomLayoutView: BottomLayoutView? = null

    private var decorateController = DecorateController()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        App.setMainActivity(this)
        KwSystemSettingUtils.resetStatusBarBlack(this)
        setContentView(R.layout.activity_main)
        bindFragmentOperation()

        viewPager2 = findViewById(R.id.view_pager2)
        viewPager2?.isSaveEnabled = false

        bottomLayoutView= findViewById(R.id.bottom_layout)

        decorateController.attach(findViewById(R.id.decorate_layer_container))


        val pairs: List<Pair<BottomItemData, Fragment>> = requestAdapterData()


        if (pairs.isNotEmpty()) {
            bottomLayoutView?.mAdapter = BottomNavAdapter(pairs)
            bottomLayoutView?.bind(ViewPager2Wrapper(viewPager2))
            viewPager2?.offscreenPageLimit = 4
            setAdapter(pairs)
            bottomLayoutView?.setOnTabClickListener(object : BottomLayoutView.OnTabClickListener{
                override fun onClick(view: View,pos:Int) {
                    if(!MainLinkHelper.isLogin()){
                        DeepLinkUtils.load("test://open/account?type=login").execute()
                        return
                    }
                    viewPager2?.setCurrentItem(pos, false)
                }

            })
        }

        if(!MainLinkHelper.isLogin()){
            DeepLinkUtils.load("test://open/account?type=login").execute()
            return
        }

//        findViewById<View>(R.id.main_test_btn)?.apply {
//            visibility = View.VISIBLE
//            setOnClickListener {
//            }
//        }

        MainLinkHelper.checkUpgrade(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MainLinkHelper.onResultCallbackForUploadChooseImage(requestCode, resultCode, data)
    }

    private fun setAdapter(fragments: List<Pair<BottomItemData, Fragment>>) {
        val mAdapter = HomePageAdapter(this, fragments)
        viewPager2?.isUserInputEnabled = false
        viewPager2?.offscreenPageLimit = 4
        viewPager2?.adapter = mAdapter
    }

    private fun requestAdapterData(): List<Pair<BottomItemData, Fragment>> {
        val data = ArrayList<Pair<BottomItemData, Fragment>>()
        MainLinkHelper.getUserHomeFragment()?.apply {
            data.add(Pair(BottomItemData("首页", R.string.bottom_index_select_icon,
                R.string.bottom_index_normal_icon
            ),this))
        }

        MainLinkHelper.getProductHomeFragment()?.apply {
            data.add(Pair(BottomItemData("商品", R.string.bottom_index_select_icon,
                R.string.bottom_index_normal_icon
            ),this))
        }

        MainLinkHelper.getMgrFragment()?.apply {
            data.add(Pair(BottomItemData("管理", R.string.bottom_index_select_icon,
                R.string.bottom_index_normal_icon
            ),this))
        }

        MainLinkHelper.getMineFragment()?.apply {
            data.add(Pair(BottomItemData("我的", R.string.bottom_mine_select_icon,
                R.string.bottom_mine_normal_icon
            ), this))
        }

        return data
    }


    private val onFragmentStackChangeListener: OnFragmentStackChangeListener =
        object : OnFragmentStackChangeListener {

            override fun onPushFragment(top: Fragment) {

            }

            override fun onPopFragment(top: Fragment) {

            }

            override fun onShowMainLayer(withBottom: Boolean) {
                if(!MainLinkHelper.isLogin()) {
                    viewPager2?.setCurrentItem(0, false)
                }
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