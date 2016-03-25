package com.santiago.feed;

import android.support.v7.widget.RecyclerView;

import com.santiago.feed.entities.Feed;
import com.santiago.feed.entities.FeedPage;

public abstract class FeedAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    public abstract int getLastFeedElementIndexFromLastVisibleViewIndex(int lastVisibleViewIndex);

    public abstract int getFeedElementCount();

    public abstract void setElementsFromFeed(Feed<E> feed);

    public abstract void addElementsFromFeedPage(FeedPage<E> feedPage);

    public abstract void clearElements();

    public abstract E getFeedElement(int position);


}
