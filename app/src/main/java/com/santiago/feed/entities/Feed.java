package com.santiago.feed.entities;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feed<E> {

    public static final int DEFAULT_ELEMENTS_PER_PAGE = 3;

    private String baseFeedUrl = null;

    private List<FeedPage<E>> pageList = new ArrayList<>();
    private int elementsPerPage = DEFAULT_ELEMENTS_PER_PAGE;

    private Map<String, String> additionalParameters = new HashMap<>();

    private boolean isComplete = false;

    public int getElementsPerPage() {
        return elementsPerPage;
    }

    public List<E> getTotalElementList(){
        List<E> resultList = new ArrayList<>();

        for(FeedPage<E> page:pageList)
            resultList.addAll(page.getElementList());

        return resultList;
    }

    public int getTotalElementCount(){
        int result = 0;

        for(FeedPage<E> page:pageList)
            result += page.getElementCount();

        return result;
    }

    public E getElement(int position){
        int aux = 0;

        for(FeedPage<E> page:pageList){
            if(position - aux < page.getElementCount())
                return page.getElement(position - aux);
            else aux += page.getElementCount();
        }

        return null;
    }

    public boolean isEmpty(){
        for(FeedPage<E> page:pageList)
            if(!page.isEmpty())
                return false;

        return true;
    }

    public Date getFirstPageDate(){
        return pageList.isEmpty() ? null : pageList.get(0).getDate();
    }

    public boolean isComplete(){
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public List<E> getLastPageElementList(){
        return getLastPage().getElementList();
    }

    public FeedPage<E> getLastPage(){
        return pageList.get(getPageCount() - 1);
    }

    public int getPageCount(){
        return pageList.size();
    }

    public int getLastPageIndex(){
        return getLastPage().getIndex();
    }

    public int getNextPageIndex(){
        return pageList.isEmpty() ? 1 : getLastPageIndex()+1;
    }

    public void setAdditionalParameters(Map<String, String> additionalParameters) {
        if(additionalParameters == null)
            this.additionalParameters = new HashMap<>();
        else this.additionalParameters = new HashMap<>(additionalParameters);
    }

    public void setAdditionalParameter(String key, String value){
        if(key==null)
            return;

        additionalParameters.put(key, value);
    }

    public void removeAdditionalParameter(String key) {
        additionalParameters.remove(key);
    }

    public void setElementsPerPage(int elementsPerPage) {
        this.elementsPerPage = elementsPerPage;
    }

    public void clean(){
        pageList.clear();
        isComplete = false;
    }

    public void addFeedPage(FeedPage<E> page){
        if(page!=null){
            pageList.add(page);

            if(page.isFinalPage())
                isComplete = true;
        }
    }

    public String getBaseFeedUrl() {
        return baseFeedUrl;
    }

    public void setBaseFeedUrl(String baseFeedUrl) {
        this.baseFeedUrl = baseFeedUrl;
    }

    public String getNextPageFeedUrl() {
        return getFeedUrl(getNextPageIndex(), getFirstPageDate());
    }

    public String getFirstPageFeedUrl() {
        return getFeedUrl(1);
    }

    public String getFeedUrl(int pageIndex){
        return getFeedUrl(pageIndex, null);
    }

    public String getFeedUrl(int pageIndex, Date timestamp) {
        StringBuilder builder = new StringBuilder();

        builder.append(getBaseFeedUrl());

        builder.append("?page=").append(pageIndex);
        builder.append("&per_page=").append(getElementsPerPage());

        if(timestamp!=null)
            builder.append("&timestamp=").append(timestamp.getTime());

        for(Map.Entry<String, String> entry : additionalParameters.entrySet()){
            builder.append('&')
                   .append(Uri.encode(entry.getKey()))
                   .append('=')
                   .append(Uri.encode(entry.getValue()));
        }

        return builder.toString();
    }
}
