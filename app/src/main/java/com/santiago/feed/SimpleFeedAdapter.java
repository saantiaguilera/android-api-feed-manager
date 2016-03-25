package com.santiago.feed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.santiago.feed.entities.Feed;
import com.santiago.feed.entities.FeedPage;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleFeedAdapter<E> extends FeedAdapter<E, SimpleViewHolder> {

    private static final int TYPE_ELEMENT = -1;

    private Context context = null;
    private List<View> headerList = new ArrayList<>();
    private List<E> elementList = new ArrayList<>();

    public SimpleFeedAdapter(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void addHeaderAtBeginning(View header){
        if(header!=null)
            headerList.add(0, header);
    }

    public void addHeader(View header){
        if(header!=null)
            headerList.add(header);
    }

    public void setElementList(List<E> elementList) {
        if(elementList !=null)
            this.elementList = new ArrayList<>(elementList);
        else this.elementList = new ArrayList<>();
    }

    public void addElementList(List<E> elementList){
        if(elementList!=null){
            this.elementList.addAll(elementList);
        }
    }

    public List<E> getElementList(){
        return elementList;
    }

    @Override
    public void clearElements(){
        elementList.clear();
    }

    public void addElement(E element){
        if(element!=null)
            elementList.add(element);
    }

    @Override
    public int getItemCount() {
        return headerList.size() + elementList.size();
    }

    @Override
    public E getFeedElement(int position) {
        return elementList.get(position);
    }

    protected int getFeedElementIndexFromItemIndex(int position){
        return Math.max(position - headerList.size(), 0);
    }

    @Override
    public int getLastFeedElementIndexFromLastVisibleViewIndex(int lastVisibleViewIndex) {
        return Math.max(lastVisibleViewIndex - headerList.size(), 0);
    }

    @Override
    public int getFeedElementCount() {
        return elementList.size();
    }

    @Override
    public void setElementsFromFeed(Feed<E> feed) {
        if(feed!=null)
            addElementList(feed.getTotalElementList());
    }

    @Override
    public void addElementsFromFeedPage(FeedPage<E> feedPage) {
        if(feedPage!=null)
            addElementList(feedPage.getElementList());
    }


    @Override
    public int getItemViewType(int position) {
        return position<headerList.size()?position:TYPE_ELEMENT;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ELEMENT)
            return new SimpleViewHolder(createElementView(parent));

        return new SimpleViewHolder(headerList.get(viewType));
    }

    protected abstract View createElementView(ViewGroup parent);

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        if(holder.getItemViewType() == TYPE_ELEMENT)
            bindView(holder.itemView, position);
    }

    protected abstract void bindView(View view, int position);

}
