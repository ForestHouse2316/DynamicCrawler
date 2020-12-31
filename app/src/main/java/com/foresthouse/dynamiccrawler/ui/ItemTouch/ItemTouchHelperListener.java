package com.foresthouse.dynamiccrawler.ui.ItemTouch;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperListener {
    boolean onItemMove(int from, int to);

    void onItemSwipe(int pos, int direction);

    void onLeftClick(int position, RecyclerView.ViewHolder viewHolder);

    void onRightClick(int position, RecyclerView.ViewHolder viewHolder);

}
