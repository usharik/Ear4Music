package ru.usharik.ear4music;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by au185034 on 16/03/2018.
 */
public final class Utilities {
    public static int getScrollPosition(RecyclerView list) {
        return ((LinearLayoutManager) list.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }
}
