package com.godq.keepaccounts

import android.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePageAdapter(parent: FragmentActivity, fragments: List<Pair<String, Fragment>>) :
    FragmentStateAdapter(parent) {

    private val mFragments: List<Pair<String, Fragment>> = fragments

    override fun getItemCount(): Int {
        return mFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position].second
    }

}