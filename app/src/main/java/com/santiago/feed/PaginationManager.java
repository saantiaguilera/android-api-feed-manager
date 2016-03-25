package com.santiago.feed;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.santiago.feed.entities.Feed;
import com.santiago.feed.entities.FeedPage;
import com.santiago.http.BaseHttpRequest;
import com.squareup.okhttp.Response;

import java.util.Map;

public abstract class PaginationManager<E> {

    private Context context;

    private LinearLayoutManager layoutManager = null;
    private int visibleThreshold = 1;

    private FeedAdapter<E, ?> adapter = null;

    private Feed<E> feed;

    private boolean loadingPage = false;
    private boolean currentFeedValid = true;

    private boolean paginationEnable = true;

    private PageDownloadListener pageDownloadListener = null;

    public PaginationManager(Context context){
        if(context==null)
            throw new NullPointerException("context cannot be null in " + this.getClass().getSimpleName());

        this.context = context;
        feed = new Feed<>();
    }

    public Context getContext() {
        return context;
    }

    public boolean isLoadingPage() {
        return loadingPage;
    }

    public boolean isFeedComplete() {
        return feed.isComplete();
    }

    public boolean isFeedEmpty() {
        return feed.isEmpty();
    }

    public boolean isPaginationEnable() {
        return paginationEnable;
    }

    public int getFeedElementCount(){
        return feed.getTotalElementCount();
    }

    public E getFeedElement(int index){
        return feed.getElement(index);
    }

    protected Feed<E> getFeed(){
        return feed;
    }

    public void setPaginationEnable(boolean paginationEnable) {
        this.paginationEnable = paginationEnable;

        if(isLoadingPage()){
            loadingPage = false;
            callOnPageDownloadSuccessListener();
        }
    }

    public void setPageDownloadListener(PageDownloadListener pageDownloadListener) {
        this.pageDownloadListener = pageDownloadListener;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        if(recyclerView == null)
            throw new NullPointerException("recyclerView cannot be null in " + this.getClass().getSimpleName());

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if(layoutManager instanceof LinearLayoutManager)
            this.layoutManager = (LinearLayoutManager) layoutManager;
        else throw new IllegalStateException("The RecyclerView asociated to the PaginationManager must use a LinearLayoutManager");
        //TODO Make grid layout possible too. Create another FeedView for it and let it decide. Care for the scrolling detection
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onScroll(dx, dy);
            }
        });
    }

    public void setAdapter(FeedAdapter<E, ?> adapter) {
        if(adapter == null)
            throw new NullPointerException("adapter cannot be null in PaginationManager");

        this.adapter = adapter;
    }

    protected void callOnDownloadPageListener(){
        if(pageDownloadListener!=null)
            pageDownloadListener.onDownloadPage();
    }

    protected void callOnPageDownloadSuccessListener(){
        if(pageDownloadListener!=null)
            pageDownloadListener.onPageDownloadSuccess();
    }

    protected void callOnPageDownloadFailureListener(Response httpResponse, Exception exception){
        if(pageDownloadListener!=null)
            pageDownloadListener.onPageDownloadFailure(httpResponse, exception);
    }

    public void setBaseFeedUrl(String feedUrl){
        feed.setBaseFeedUrl(feedUrl);
    }

    public void setFeedAdditionalParameter(String key, String value){
        feed.setAdditionalParameter(key, value);
    }

    public void removeFeedAdditionalParameter(String key) {
        feed.removeAdditionalParameter(key);
    }

    public void setFeedAdditionalParameters(Map<String,String> parameters){
        feed.setAdditionalParameters(parameters);
    }

    public void setElementsPerPage(int elementsPerPage){
        feed.setElementsPerPage(elementsPerPage);
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public void cleanFeed(){
        feed.clean();
        feed.setComplete(true);

        adapter.clearElements();
        adapter.notifyDataSetChanged();
    }

    public void refreshFeed(){
        if(!isPaginationEnable())
            return;

        if(feed.getBaseFeedUrl() == null)
            return;

        if(isLoadingPage())
            currentFeedValid = false;
        else requestFeedFirstPageQuery();
    }

    protected void onScroll(int dx, int dy){
        if(!isPaginationEnable())
            return;

        int lastVisibleItem  = adapter.getLastFeedElementIndexFromLastVisibleViewIndex(layoutManager.findLastVisibleItemPosition());
        int remainingItems = adapter.getFeedElementCount() - lastVisibleItem;

        if(remainingItems<=visibleThreshold && !loadingPage && feed.getBaseFeedUrl() != null && !feed.isComplete())
            requestFeedNextPageQuery();
    }

    protected void requestFeedFirstPageQuery(){
        if(!isPaginationEnable())
            return;

        requestFeedPageQuery(getFeedFirstPageQuery());
    }

    protected abstract FeedPageQuery<E> getFeedFirstPageQuery();

    protected void requestFeedNextPageQuery(){
        if(!isPaginationEnable())
            return;

        requestFeedPageQuery(getFeedNextPageQuery());
    }

    protected abstract FeedPageQuery<E> getFeedNextPageQuery();

    private void requestFeedPageQuery(FeedPageQuery<E> pageQuery){
        callOnDownloadPageListener();

        loadingPage = true;

        pageQuery.setFeed(feed);
        pageQuery.setSuccessListener(new BaseHttpRequest.HttpRequestSuccessListener<FeedPage<E>>() {
            @Override
            public void onHttpRequestSuccess(BaseHttpRequest<FeedPage<E>> request, FeedPage<E> result) {
                onPageDownloadSuccess(result);
            }
        });
        pageQuery.setFailureListener(new BaseHttpRequest.HttpRequestFailureListener<FeedPage<E>>() {
            @Override
            public void onHttpRequestFailure(BaseHttpRequest<FeedPage<E>> request, Response httpResponse, Exception exception) {
                onPageDownloadFailure(httpResponse, exception);
            }
        });
        pageQuery.execute();
    }

    protected void onPageDownloadSuccess(FeedPage<E> newPage){
        if(!isPaginationEnable())
            return;

        if(newPage.isFirstPage()){
            if(newPage.isEmpty())
                onEmptyFirstPageDownloaded(newPage);
            else onFirstPageDownloaded(newPage);
        } else {
            if(newPage.isEmpty())
                onEmptyNewPageDownloaded(newPage);
            else onNewPageDownloaded(newPage);
        }

        loadingPage = false;

        callOnPageDownloadSuccessListener();

        if(!currentFeedValid){
            currentFeedValid = true;
            refreshFeed();
        }
    }

    protected void onEmptyFirstPageDownloaded(FeedPage<E> firstEmptyPage){
        cleanFeed();
    }

    protected void onFirstPageDownloaded(FeedPage<E> firstPage){
        feed.clean();
        feed.addFeedPage(firstPage);
        adapter.clearElements();
        adapter.addElementsFromFeedPage(firstPage);
        adapter.notifyDataSetChanged();
    }

    protected void onEmptyNewPageDownloaded(FeedPage<E> newEmptyPage){
        feed.setComplete(true);
    }

    protected void onNewPageDownloaded(FeedPage<E> newPage){
        feed.addFeedPage(newPage);
        adapter.addElementsFromFeedPage(newPage);
        adapter.notifyDataSetChanged();
    }

    protected void onPageDownloadFailure(Response httpResponse, Exception exception){
        loadingPage = false;

        callOnPageDownloadFailureListener(httpResponse, exception);
    }

    public interface PageDownloadListener{
        void onDownloadPage();
        void onPageDownloadSuccess();
        void onPageDownloadFailure(Response httpResponse, Exception exception);
    }

}
