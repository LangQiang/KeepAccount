package com.godq.im.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.godq.im.databinding.ImFragmentChatRoomLayoutBinding
import com.lazylite.mod.App
import com.lazylite.mod.utils.SoftKeyboardHelper
import com.lazylite.mod.widget.BaseFragment

class ChatRoomFragment: BaseFragment() {

    private var binding: ImFragmentChatRoomLayoutBinding? = null

    private val vm = ChatRoomVM()

    private val mAdapter = ChatRoomAdapter(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImFragmentChatRoomLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(AndroidBug5497Fixed()) {
            onChangedListener = {
                binding?.rv?.post {
                    (mAdapter.itemCount - 1).takeIf {
                        it >= 0
                    }?.apply {
                        binding?.rv?.smoothScrollToPosition(this)
                    }
                }
            }
            assistRootView(App.getMainActivity(), binding?.chatContentView)
        }
        binding?.titleBar?.setMainTitle("CHAT ROOM")
        binding?.titleBar?.setBackListener { close() }

        binding?.rv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        vm.onDataCallback = {
            val prePos = mAdapter.data.size
            mAdapter.data.addAll(it)
            mAdapter.notifyItemRangeInserted(prePos, it.size)
            binding?.rv?.smoothScrollToPosition(mAdapter.itemCount - 1)


        }

        vm.loadHistory()
    }

    override fun Pause() {
        super.Pause()
        binding?.editTv?.apply {
            SoftKeyboardHelper.hideKeyboard(this)
        }
    }
}