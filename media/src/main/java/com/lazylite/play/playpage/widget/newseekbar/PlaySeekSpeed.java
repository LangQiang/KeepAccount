package com.lazylite.play.playpage.widget.newseekbar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qyh
 * @date 2022/1/21
 * describe:
 */
public class PlaySeekSpeed {
    private static List<int[]> list = new ArrayList<>();
    private static int[] highD0 = {1, 2, 5, 4, 6, 2, 3, 7, 4, 2, 5, 2, 3, 6, 7, 5, 2, 5, 7, 3, 6, 3, 3, 6, 3, 2, 1, 3, 3, 1, 2, 2, 7, 5, 3, 4, 2, 3, 6, 2, 1, 6, 4, 3, 2, 3, 4, 2, 5, 6, 3, 2, 4, 2, 1, 2, 1, 2, 6};
    private static int[] highD1 = {4, 3, 4, 3, 6, 2, 4, 2, 6, 3, 2, 3, 4, 6, 1, 3, 2, 7, 5, 2, 4, 5, 1, 4, 3, 1, 5, 6, 4, 4, 5, 3, 7, 5, 4, 3, 2, 3, 4, 3, 3, 3, 1, 3, 2, 5, 6, 6, 1, 6, 3, 2, 3, 2, 2, 2, 4, 2, 6};
    private static int[] highD2 = {2, 5, 5, 4, 2, 6, 7, 5, 3, 6, 4, 2, 5, 2, 7, 4, 6, 2, 5, 3, 3, 4, 2, 6, 3, 2, 1, 3, 1, 2, 6, 4, 7, 3, 3, 4, 2, 3, 4, 2, 1, 6, 2, 3, 2, 3, 4, 2, 1, 3, 3, 5, 3, 2, 1, 4, 1, 5, 6};
    private static int[] highD3 = {3, 6, 5, 4, 3, 2, 6, 3, 4, 3, 2, 3, 4, 5, 2, 5, 2, 4, 5, 6, 2, 3, 4, 7, 5, 7, 2, 6, 4, 7, 6, 2, 4, 5, 3, 4, 5, 3, 4, 2, 1, 5, 1, 3, 6, 3, 4, 3, 3, 6, 3, 2, 3, 5, 3, 2, 4, 2, 6};
    private static int[] highD4 = {4, 2, 5, 3, 7, 6, 4, 5, 3, 1, 3, 5, 2, 2, 3, 6, 3, 4, 2, 3, 4, 6, 5, 6, 3, 2, 1, 3, 2, 4, 3, 4, 7, 6, 2, 7, 2, 3, 6, 4, 4, 6, 3, 3, 2, 6, 5, 2, 1, 6, 3, 2, 4, 2, 4, 2, 1, 2, 6};
    private static int[] highD5 = {5, 1, 2, 4, 2, 3, 2, 4, 6, 3, 7, 3, 4, 7, 2, 6, 5, 6, 3, 2, 2, 7, 1, 2, 5, 2, 4, 1, 3, 4, 6, 5, 6, 5, 3, 5, 2, 3, 4, 2, 1, 4, 1, 4, 2, 3, 4, 5, 1, 5, 4, 2, 3, 2, 1, 2, 1, 1, 6};
    private static int[] highD6 = {6, 3, 4, 5, 2, 3, 4, 7, 5, 6, 2, 4, 5, 2, 1, 6, 2, 4, 2, 4, 1, 7, 3, 6, 3, 2, 6, 7, 5, 2, 3, 2, 7, 3, 6, 5, 2, 3, 4, 2, 5, 6, 3, 3, 7, 5, 4, 2, 6, 6, 3, 6, 3, 2, 5, 2, 3, 2, 6};
    private static int[] highD7 = {4, 3, 2, 4, 5, 4, 6, 2, 4, 3, 8, 3, 4, 1, 5, 2, 5, 6, 2, 5, 2, 5, 1, 3, 4, 6, 1, 4, 2, 4, 6, 2, 3, 5, 3, 4, 2, 3, 3, 5, 1, 2, 5, 6, 2, 3, 3, 3, 1, 4, 6, 2, 4, 5, 1, 2, 1, 3, 6};
    private static int[] highD8 = {5, 3, 2, 4, 7, 2, 4, 5, 3, 6, 2, 6, 2, 5, 1, 6, 2, 3, 4, 3, 5, 2, 5, 6, 4, 2, 6, 3, 4, 5, 2, 6, 7, 4, 7, 4, 2, 3, 4, 2, 3, 6, 1, 3, 4, 3, 4, 2, 4, 6, 3, 4, 3, 2, 2, 2, 1, 2, 6};
    private static int[] highD9 = {3, 4, 6, 4, 2, 6, 3, 5, 4, 3, 5, 3, 2, 3, 4, 1, 3, 7, 2, 6, 5, 3, 1, 5, 4, 2, 4, 3, 5, 4, 6, 2, 7, 5, 3, 7, 2, 3, 6, 2, 1, 5, 4, 3, 2, 7, 4, 5, 1, 6, 3, 5, 3, 2, 1, 5, 1, 2, 6};


    static {
        list.add(highD0);
        list.add(highD1);
        list.add(highD2);
        list.add(highD3);
        list.add(highD4);
        list.add(highD5);
        list.add(highD6);
        list.add(highD7);
        list.add(highD8);
        list.add(highD9);
    }

    public static int[] getSpeed(long id) {
        if (id <= 0) {
            return highD0;
        }
        int tailNum = (int) id % 10;
        if (list != null && list.size() > tailNum) {
            return list.get(tailNum);
        }
        return highD0;
    }
}
