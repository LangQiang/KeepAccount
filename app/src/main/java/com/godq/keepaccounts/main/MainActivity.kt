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
import com.godq.media_api.media.PlayStatus
import com.godq.media_api.media.bean.BookBean
import com.godq.media_api.media.bean.ChapterBean
import com.lazylite.media.playctrl.PlayControllerImpl
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

        findViewById<View>(R.id.main_test_btn)?.apply {
//            visibility = View.VISIBLE
            setOnClickListener {
                if (PlayControllerImpl.getInstance().playStatus == PlayStatus.STATUS_INIT || PlayControllerImpl.getInstance().playStatus == PlayStatus.STATUS_STOP) {
                    val book = BookBean()
                    book.mBookId = 1
                    book.mArtist = "周杰伦"
                    book.mImgUrl = "https://godq-1307306000.cos.ap-beijing.myqcloud.com/song/0885b348344f49d7812fc1085cc79230.jpeg"
                    book.mDescription = "《叶惠美》是周杰伦于2003年发行的专辑，共收录了11首歌曲。2004年，该专辑获得了第15届金曲奖最佳流行音乐演唱专辑奖、新城国语力颁奖礼新城国语力亚洲大碟奖、第四届全球华语歌曲排行榜颁奖典礼年度最受欢迎专辑奖 。"
                    book.mName = "叶惠美"
                    book.mCount = 2
                    val chapterBeans = ArrayList<ChapterBean>()

                    val chapterBean2 = ChapterBean()
                    chapterBean2.mName = "以父之名"
                    chapterBean2.mRid = 2
                    chapterBean2.resUrl = "https://godq-1307306000.cos.ap-beijing.myqcloud.com/song/%E5%91%A8%E6%9D%B0%E4%BC%A6-%E4%BB%A5%E7%88%B6%E4%B9%8B%E5%90%8D.mp3"
                    chapterBeans.add(chapterBean2)

                    val chapterBean = ChapterBean()
                    chapterBean.mName = "晴天"
                    chapterBean.mRid = 3
                    chapterBean.resUrl = "https://godq-1307306000.cos.ap-beijing.myqcloud.com/song/%E6%99%B4%E5%A4%A9.mp3"
                    chapterBeans.add(chapterBean)
                    PlayControllerImpl.getInstance().play(book, chapterBeans, 0 , 0)
                }

                DeepLinkUtils.load("test://open/play").execute()
            }
        }

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