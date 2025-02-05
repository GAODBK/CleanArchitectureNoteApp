package dev.lyg.cp.stacklib;

public interface IScrollListener {

    /**
     * 由父ScrollView等通知其（子）发生滚动
     *
     * @param scrollX 当前滚动量  对应 ScrollY
     * @param scrollY 当前滚动量  对应 ScrollX
     */
    void onScrollChanged(int scrollX, int scrollY);

    /**
     * 容器(IScrollSubscription)的可视大小发生了改变，会回调这个方法来通知
     *
     * @param visibleWidth  滚动容器的可视宽度
     * @param visibleHeight 滚动容器的可视高度
     */
    void onVisibleSizeChanged(int visibleWidth, int visibleHeight);
}
