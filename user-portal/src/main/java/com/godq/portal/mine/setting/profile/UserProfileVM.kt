package com.godq.portal.mine.setting.profile

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableField
import com.godq.accountsa.IAccountService
import com.godq.portal.UserPortalLinkHelper
import com.godq.portal.constants.getUpdateUrl
import com.lazylite.mod.App
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import org.json.JSONObject


/**
 * @author  GodQ
 * @date  2024/6/6 13:37
 */
class UserProfileVM {

    var nickName = ObservableField("")

    fun init() {
        nickName.set(UserPortalLinkHelper.getAccountInfo()?.getNickName())
    }

    fun onNickNameChangeClick() {
        showInputDialog(App.getMainActivity(), "修改昵称") { tNickName ->
            val json = JSONObject()
            json.putOpt("nick_name", tNickName)
            val req = RequestInfo.newPost(getUpdateUrl(), mapOf("Content-Type" to "application/json"), json.toString().toByteArray())
            KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
                if (!it.isSuccessful) return@asyncPost
                //更新缓存
                UserPortalLinkHelper.getAccountInfo()?.updateInfo(tNickName, null)
                //本地持久化
                UserPortalLinkHelper.saveLocalLoginData(UserPortalLinkHelper.getAccountInfo())
                //全局广播登录消息
                MessageManager.getInstance().asyncNotify(
                    IAccountService.IAccountObserver.EVENT_ID,
                    object : MessageManager.Caller<IAccountService.IAccountObserver>() {
                        override fun call() {
                            ob.onUpdate()
                        }
                    })
                //更新ui nickName
                nickName.set(tNickName)
            }
        }
    }

    private fun showInputDialog(context: Context?, title: String, onTextEntered: (String) -> Unit) {
        context?: return
        val input = EditText(context)
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("提交") { _, _ ->
                onTextEntered(input.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

}