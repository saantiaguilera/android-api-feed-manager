package com.santiago.feed;

import android.content.Context;

import com.santiago.feed.entities.Feed;
import com.santiago.feed.entities.FeedPage;
import com.santiago.http.BaseHttpRequest;
import com.santiago.http.HttpCode;
import com.santiago.http.HttpMethod;
import com.santiago.http.HttpParseException;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;

public abstract class FeedPageQuery<E> extends BaseHttpRequest<FeedPage<E>> {

    private Feed<E> feed = null;

    private int pageIndex = 1;
    private Date timestamp = null;

	public FeedPageQuery(Context context){
		super(context);
	}
/*
	public FeedPageQuery(Context context, String accessToken) {
		super(context, accessToken);
	}
*/
    public void setFeed(Feed<E> feed) {
        this.feed = feed;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    protected Feed<E> getFeed() {
        return feed;
    }

    @Override
	protected String getHttpMethod() {
		return HttpMethod.GET;
	}

    @Override
    protected String getRequestUrl() {
        return getFeed().getFeedUrl(pageIndex, timestamp);
    }

	@Override
	protected FeedPage<E> parseResponse(Response response) throws HttpParseException {
		JSONObject responseJson;
        FeedPage<E> page;

		try {
            String responseString = response.body().string();

            if(response.code()!= HttpCode.OK)
                throw new HttpParseException("Response status indicates error "+response.code());

            responseJson = new JSONObject(responseString);
            page = parsePage(responseJson);
		} catch (IOException | JSONException exception) {
			throw new HttpParseException(exception);
		}
		
		return page;
	}

    protected abstract FeedPage<E> parsePage(JSONObject jsonObject) throws JSONException;
	
}
