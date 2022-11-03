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

    private var androidBug5497Fixed: AndroidBug5497Fixed? = null

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
        androidBug5497Fixed = AndroidBug5497Fixed().apply {
            onChangedListener = {
                binding?.rv?.post {
                    (mAdapter.itemCount - 1).takeIf {
                        it >= 0
                    }?.apply {
                        binding?.rv?.smoothScrollToPosition(this)
                    }
                }
            }

        }
        binding?.titleBar?.setMainTitle("CHAT ROOM")
        binding?.titleBar?.setBackListener { close() }

        mAdapter.setOnItemChildClickListener { adapter, itemView, position ->
            vm.onItemChildClick(adapter.data[position] as? MessageEntity, itemView)
        }

        binding?.rv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        vm.onDataCallback = {
            val prePos = mAdapter.data.size
            mAdapter.data.addAll(it)
            mAdapter.notifyItemRangeInserted(prePos, it.size)

            val count = mAdapter.itemCount - 1
            if (count >= 0) {
                if (it.size > 10) {
                    binding?.rv?.scrollToPosition(mAdapter.itemCount - 1)
                } else {
                    binding?.rv?.smoothScrollToPosition(mAdapter.itemCount - 1)
                }
            }

        }

        vm.loadHistory()
    }

    override fun Pause() {
        super.Pause()
        binding?.editTv?.apply {
            SoftKeyboardHelper.hideKeyboard(this)
        }
    }

    override fun onStart() {
        super.onStart()
        androidBug5497Fixed?.assistRootView(App.getMainActivity(), binding?.chatContentView)
    }

    override fun onStop() {
        super.onStop()
        androidBug5497Fixed?.release()
    }


    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(vm)
    }
}