package com.godq.keepaccounts.main

import android.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.godq.compose.botnav.BottomItemData

class HomePageAdapter(parent: FragmentActivity, fragments: List<Pair<BottomItemData, Fragment>>) :
    FragmentStateAdapter(parent) {

    private val mFragments: List<Pair<BottomItemData, Fragment>> = fragments

    override fun getItemCount(): Int {
        return mFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position].second
    }

}