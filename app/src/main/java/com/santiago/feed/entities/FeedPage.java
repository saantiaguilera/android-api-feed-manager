package com.santiago.feed.entities;

import com.santiago.feed.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public abstract class FeedPage<E> {

    private Date date;
    private int index;
    private List<E> elementList;
    private boolean isFinalPage;

    public FeedPage(){
        setDefaultValues();
    }

    public FeedPage(FeedPage<E> page){
        setValuesFrom(page);
    }

    public FeedPage(JSONObject jsonObject) throws JSONException {
        setValuesFrom(jsonObject);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFinalPage(boolean isFinalPage) {
        this.isFinalPage = isFinalPage;
    }

    public void setElementList(List<E> elementList) {
        this.elementList = elementList;
    }

    public boolean isEmpty(){
        return elementList.isEmpty();
    }

    public int getIndex() {
        return index;
    }

    public Date getDate() {
        return date;
    }

    public boolean isFirstPage(){
        return index == 1;
    }

    public boolean isFinalPage(){
        return isFinalPage;
    }

    public List<E> getElementList() {
        return elementList;
    }

    public E getElement(int index){
        return elementList.get(index);
    }

    public int getElementCount(){ return elementList.size(); }

    public void setValuesFrom(JSONObject jsonObject) throws JSONException {
        if(jsonObject!=null){
            JSONObject pagination = jsonObject.optJSONObject("pagination");

            if(pagination != null){
                setIndex(pagination.getInt("current"));
                setFinalPage(getIndex() >= pagination.getInt("pages"));
            } else {
                setIndex(1);
                setFinalPage(true);
            }

            try {
                setElementList(parseElementList(jsonObject.getJSONArray("response")));

                if(jsonObject.has("timestamp"))
                    setDate(Utils.dateFromString(jsonObject.getString("timestamp")));

            } catch (ParseException e) {
                throw new JSONException("Error parsing date");
            }

        } else setDefaultValues();
    }

    private JSONObject asJsonObject() {
        //TODO
        return null;
    }


    protected abstract List<E> parseElementList(JSONArray jsonArray) throws JSONException;

    public void setValuesFrom(FeedPage page){
        if(page!=null) {
            setIndex(page.getIndex());
            setDate(page.getDate());
            setElementList(page.getElementList());
            setFinalPage(isFinalPage());
        } else {
            setDefaultValues();
        }
    }

    public void setDefaultValues(){

        setIndex(1);
        setDate(null);
        setElementList(new ArrayList<E>());
        setFinalPage(false);

    }

    @Override
    public String toString() {
        return asJson();
    }

    private String asJson() {
        return isEmpty() ? null : getElement(0).toString();
    }

}
