package com.godq.resource

import android.graphics.drawable.Drawable
import com.godq.compose.titlebar.AbsTitleBarResDelegate
import com.godq.compose.titlebar.TitleBar
import com.godq.xskin.SkinManager


/**
 * @author  GodQ
 * @date  2023/6/8 5:24 PM
 */
class SkinTitleBarResDelegate(private val titleBar: TitleBar): AbsTitleBarResDelegate, SkinManager.SkinChangedListener {

    override fun onAttach() {
        SkinManager.registerSkinChangedListener(this)
    }

    override fun onDetach() {
        SkinManager.unregisterSkinChangedListener(this)
    }

    override fun getBackIcon(): Drawable? = SkinManager.getSkinResource()?.getDrawable(R.drawable.ui_compose_title_bar_back_black)

    override fun getTitleColor(): Int? = SkinManager.getSkinResource()?.getColor(R.color.skin_text_primary)


    override fun getMenuIcon(): Drawable? = null

    override fun getMenuTextStr(): String? = null

    override fun getMenuTextColor(): Int? = SkinManager.getSkinResource()?.getColor(R.color.black40)

    override fun getBackground(): Drawable? = SkinManager.getSkinResource()?.getDrawable(R.color.skin_title_bar_bg_color)

    override fun onChanged() {
        titleBar.notifyStyleChanged()
    }
}