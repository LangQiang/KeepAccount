package com.product.product.ui.home

import android.content.Context
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.godq.xskin.SkinManager
import com.lazylite.mod.App
import com.lazylite.mod.widget.indicator.base.CommonContainer
import com.lazylite.mod.widget.indicator.base.IPagerIndicator
import com.lazylite.mod.widget.indicator.base.IPagerTitle
import com.lazylite.mod.widget.indicator.base.IndicatorParameter
import com.lazylite.mod.widget.indicator.ui.extsimple.FixedWidthLinearIndicatorView
import com.lazylite.mod.widget.indicator.ui.home.HomePagerTitleView
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper
import com.product.product.R


/**
 * @author  GodQ
 * @date  2023/12/8 13:35
 */
open class ProductIndicatorContainer(context: Context): CommonContainer(context) {

    private var mTitles: List<String>? = null

    init {
        tabMode = MODE_FIXED_SPACE
    }

    fun setTitles(titles: List<String>) {
        mTitles = titles
    }

    override fun getTitleView(context: Context?, index: Int): IPagerTitle {
        val simplePagerTitleView = object : HomePagerTitleView(context) {
            init {
                mSelectedColor = SkinManager.getSkinResource()?.getColor(R.color.app_theme_color)?: 0xffff5400.toInt()
            }
        }
        simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)

        simplePagerTitleView.text = provideIndicatorTitle(index)

        simplePagerTitleView.setPadding(IndicatorHelper.dip2px(15.0), 0, IndicatorHelper.dip2px(15.0), 0)

        return simplePagerTitleView
    }

    override fun getIndicator(context: Context?): IPagerIndicator {
        return FixedWidthLinearIndicatorView(context, provideIndicatorParameter()!!)

    }

    protected open fun provideIndicatorTitle(index: Int): CharSequence? {
        return mTitles?.get(index)?: ""
    }

    private fun provideIndicatorParameter(): IndicatorParameter? {
        return IndicatorParameter.Builder()
            .withIndicatorHeight(IndicatorHelper.dip2px(2.5)) //                .withLRPadding(IndicatorHelper.dip2px(8))//这个是设置指示条的左右padding，不要和本类中的 mLRPadding 混淆了
            .withUseHighColor(false)
            .withIndicatorColorRid(R.color.app_theme_color)
            .withVerticalSpace(IndicatorHelper.dip2px(0.0))
            .withShowMode(IndicatorParameter.MODE_NORMAL)
            .withRadius(IndicatorHelper.dip2px(2.0))
            .withStartInterpolator(AccelerateDecelerateInterpolator())
            .withEndInterpolator(DecelerateInterpolator())
            .build()
    }

}