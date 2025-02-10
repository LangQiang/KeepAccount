package com.lazylite.play.playpage;

import android.net.Uri;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.godq.deeplink.route.AbsRouter;
import com.lazylite.annotationlib.DeepLink;
import com.lazylite.media.R;
import com.lazylite.mod.fragmentmgr.FragmentOperation;
import com.lazylite.mod.fragmentmgr.StartMode;
import com.lazylite.mod.fragmentmgr.StartParameter;

/**
 * @author qyh
 * @date 2022/1/10
 * describe:
 */

@DeepLink(path = "/play")
public class PlayPageRouter extends AbsRouter {
    private String from;
    private String taskId;
    private String taskType;
    private String albumId;

    @Override
    protected void parse(Uri uri) {
        from = uri.getQueryParameter("from");
        if (!TextUtils.isEmpty(from) && from.equals("task")) {
            taskId = uri.getQueryParameter("taskId");
            taskType = uri.getQueryParameter("taskType");
            albumId = uri.getQueryParameter("albumId");
        }
    }

    @Override
    protected boolean route() {

        Fragment topF = FragmentOperation.getInstance().getTopFragment();
        if (topF instanceof PlayPageFragment) {
            return false;
        }

        FragmentOperation.getInstance().showFullFragment(PlayPageFragment.newInstance(from),
                new StartParameter.Builder()
                        .withEnterAnimation(R.anim.lrlite_base_bottom_in)
                        .withOuterAnimation(R.anim.lrlite_base_bottom_out)
                        .withStartMode(StartMode.SINGLE_INSTANCE)
                        .build());
        savePlayInfo();
        return true;
    }

    private void savePlayInfo() {
//        ConfMgr.setStringValue("", "", null, false);
    }
}
