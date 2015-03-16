package com.ludicrus.ludicrus.interfaces;

import android.widget.AbsListView;

/**
 * Created by jpgarduno on 3/14/15.
 */
public interface PagerScroller {

    void adjustScroll(int scrollHeight);

    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);
}
