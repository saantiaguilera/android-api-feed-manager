package com.santiago.feed;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.santiago.customfeed.R;
import com.squareup.okhttp.Response;

public abstract class FeedView<E> extends FrameLayout implements PaginationManager.PageDownloadListener {

    private RecyclerView recyclerView = null;
    private FrameLayout emptyFrame = null;
    private SwipeRefreshLayout refreshLayout = null;

    private RecyclerView.ItemDecoration decoration;
    private View emptyView = null;
    private View standByView = null;

    private boolean onlineMode = true;

    private PaginationManager<E> paginationManager = null;
    private FeedAdapter<E, SimpleViewHolder> adapter = null;

    public FeedView(Context context) {
        this(context, null);
    }

    public FeedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.view_feed_layout, this);

        emptyFrame = (FrameLayout) findViewById(R.id.view_feed_empty_frame);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.view_feed_swipe);
        recyclerView = (RecyclerView) findViewById(R.id.view_feed_recyclerview);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });

        paginationManager = getPaginationManager();
        adapter = getFeedAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        paginationManager.setRecyclerView(recyclerView);
        paginationManager.setAdapter(adapter);
        paginationManager.setPageDownloadListener(this);

    }

    protected abstract PaginationManager<E> getPaginationManager();
    protected abstract FeedAdapter<E, SimpleViewHolder> getFeedAdapter();

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public void setBaseFeedUrl(String url){
          paginationManager.setBaseFeedUrl(url);
    }

    public void setElementsPerPage(int elementsPerPage){
        paginationManager.setElementsPerPage(elementsPerPage);

    }

    public void setDecoration(RecyclerView.ItemDecoration decoration) {
        this.decoration = decoration;

        if(recyclerView!=null && decoration!=null)
            recyclerView.addItemDecoration(decoration);
    }

    public void setVisibleThreshold(int threshold){
        paginationManager.setVisibleThreshold(threshold);
    }

    protected void setFeedAdditionalParameter(String key, String value){
        paginationManager.setFeedAdditionalParameter(key, value);
    }

    public void setOnlineMode(boolean onlineMode) {
        this.onlineMode = onlineMode;
        getPaginationManager().setPaginationEnable(onlineMode);
        refreshLayout.setEnabled(onlineMode);
    }

    protected void removeFeedAdditionalParameter(String key) {
        paginationManager.removeFeedAdditionalParameter(key);
    }

    public void refreshFeed(){
        if(isOnlineMode())
            paginationManager.refreshFeed();
    }

    public void cleanFeed(){
        paginationManager.cleanFeed();
    }

    public void setProgressBackgroundColor(int color){
        refreshLayout.setProgressBackgroundColorSchemeColor(color);
    }

    public void setProgressBackgroundResource(int res){
        refreshLayout.setProgressBackgroundColorSchemeResource(res);
    }

    public void setProgressSchemeColors(int... colors){
        refreshLayout.setColorSchemeColors(colors);
    }

    public void setProgressSchemeResources(int... res){
        refreshLayout.setColorSchemeResources(res);
    }

    public void setProgressViewOffset(boolean scale, int start, int end){
        refreshLayout.setProgressViewOffset(scale, start, end);
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public void setStandByView(View standByView) {
        this.standByView = standByView;
    }

    @Override
    public void onDownloadPage() {
        showLoadingUI();
    }

    @Override
    public void onPageDownloadSuccess() {
        hideLoadingUI();

        if(paginationManager.isFeedComplete() && paginationManager.isFeedEmpty())
            showEmptyView();
        else removeOtherViews();
    }

    public void showStandByView(){
        emptyFrame.removeAllViews();

        if(standByView!=null)
            emptyFrame.addView(standByView);
    }

    public void showEmptyView() {
        emptyFrame.removeAllViews();

        if (emptyView != null)
            emptyFrame.addView(emptyView);
    }


    public void removeOtherViews(){
        emptyFrame.removeAllViews();
    }

    @Override
    public void onPageDownloadFailure(Response httpResponse, Exception exception) {
        hideLoadingUI();
    }

    public void hideLoadingUI(){
        refreshLayout.setRefreshing(false);
    }

    public void showLoadingUI(){
        refreshLayout.setRefreshing(true);
    }

    public int getFeedElementCount(){
        return paginationManager.getFeedElementCount();
    }

    public E getFeedElement(int position){
        return paginationManager.getFeedElement(position);
    }

}
