package com.lazylite.play.playpage.widget.newseekbar;

/**
 * @author qyh
 * @date 2022/1/7
 * describe:
 */
public class SpectrumData {
    private float left;
    private float right;
    private float top;
    private float bottom;


    public SpectrumData(float left, float right, float top, float bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

}

