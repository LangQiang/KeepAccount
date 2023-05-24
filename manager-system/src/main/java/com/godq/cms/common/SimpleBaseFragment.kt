package com.godq.cms.common

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IFragment


/**
 * @author  GodQ
 * @date  2023/5/18 4:23 下午
 */
open class SimpleBaseFragment: Fragment(), IFragment {

    override fun setFragmentType(type: Int) {

    }

    override fun tag(): String {
        return this.javaClass.name
    }

    override fun onNewIntent(args: Bundle?) {
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return KeyEvent.KEYCODE_MENU == keyCode && !FragmentOperation.getInstance().isMainLayerShow
    }
}