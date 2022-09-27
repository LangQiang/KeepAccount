package com.godq.keepaccounts

import android.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by tc :)
 */
class HomePageAdapter(fm: FragmentManager, fragments: List<Pair<String, Fragment>>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragments: List<Pair<String, Fragment>> = fragments

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position].second
    }



    fun getFragment(position: Int): Fragment? {
        return mFragments[position].second
    }

    fun Resume() {
        for (pair in mFragments) {
            if (pair.second != null && pair.second.isAdded) {
                pair.second.onResume()
            }
        }
    }

    fun Pause() {
        for (pair in mFragments) {
            if (pair.second != null && pair.second.isAdded) {
                pair.second.onPause()
            }
        }
    }

    fun isMainTab(f: Fragment): Boolean {
        for (pair in mFragments) {
            if (f === pair.second) {
                return true
            }
        }
        return false
    }

}