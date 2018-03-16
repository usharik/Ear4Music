package com.example.macbook.ear4music;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by au185034 on 16/03/2018.
 */

public final class Utilits {
    public static int getScrollPosition(RecyclerView list) {
        return ((LinearLayoutManager) list.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }
}
