package com.godq.keepaccounts.main

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
import com.godq.cms.getUpdateBillUrl
import com.godq.compose.botnav.BottomItemData
import com.godq.compose.botnav.BottomLayoutView
import com.godq.compose.botnav.BottomNavAdapter
import com.godq.compose.botnav.wrapper.ViewPager2Wrapper
import com.godq.keepaccounts.MainLinkHelper
import com.godq.keepaccounts.R
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IHostActivity
import com.lazylite.mod.fragmentmgr.OnFragmentStackChangeListener
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.utils.KwSystemSettingUtils
import com.lazylite.mod.utils.toast.KwToast
import org.json.JSONObject
import timber.log.Timber
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    private var viewPager2: ViewPager2? = null

    private var navHolder: View? = null

    private var bottomLayoutView: BottomLayoutView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        App.setMainActivity(this)
        setCustomTheme()
        setContentView(R.layout.activity_main)
        bindFragmentOperation()

        viewPager2 = findViewById(R.id.view_pager2)
        viewPager2?.isSaveEnabled = false

        navHolder = findViewById(R.id.nav_holder)

        bottomLayoutView= findViewById(R.id.bottom_layout)


        val pairs: List<Pair<BottomItemData, Fragment>> = requestAdapterData()


        if (!pairs.isNullOrEmpty()) {
            bottomLayoutView?.mAdapter = BottomNavAdapter(pairs)
            bottomLayoutView?.bind(ViewPager2Wrapper(viewPager2))
            viewPager2?.offscreenPageLimit = 4
            setAdapter(pairs)
            bottomLayoutView?.setOnTabClickListener(object : BottomLayoutView.OnTabClickListener{
                override fun onClick(view: View,pos:Int) {
                    viewPager2?.setCurrentItem(pos, false)
                }

            })
        }

//        DeepLinkUtils.load("test://open/account?type=register").execute()
        //curl -d '{"shop_name":"双门洞的夏天-2","shop_img":"https://img2.baidu.com/it/u=3876889557,2331082860&fm=253&fmt=auto&app=120&f=PNG?w=1422&h=800","shop_desc":"韩式烤肉","shop_addr":"哈西万达底商","shop_phone":"13312345678"}' -H 'Content-Type: application/json' http://150.158.55.208/shop/create -i
//        val header = HashMap<String, String>()
//        header["Content-Type"] = "application/json"
//        val json = JSONObject()
//        json.putOpt("shop_name", "双门洞的夏天-2")
//        json.putOpt("shop_img", "https://img2.baidu.com/it/u=3876889557,2331082860&fm=253&fmt=auto&app=120&f=PNG?w=1422&h=800")
//        json.putOpt("shop_desc", "韩式烤肉")
//        json.putOpt("shop_addr", "哈西万达底商")
//        json.putOpt("shop_phone", "13312345678")
//
//        val req = RequestInfo.newPost("http://150.158.55.208/shop/create", header, json.toString().toByteArray())
//
//
//        KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
//            Timber.tag("createShop").e(it.dataToString())
//        }
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

        MainLinkHelper.getMineFragment()?.apply {
            data.add(Pair(BottomItemData("我的", R.string.bottom_mine_select_icon,
                R.string.bottom_mine_normal_icon
            ), this))
        }

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