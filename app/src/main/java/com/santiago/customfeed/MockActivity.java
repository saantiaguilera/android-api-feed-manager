package com.santiago.customfeed;

/**
 * Created by santiago on 25/03/16.
 */
public class MockActivity {

    /**
     * Usage:
     * 
     * Create classes for 
     * - FeedPage:
     *

         public class EntityFeedPage extends FeedPage<Entity> {
    
             public EntityFeedPage(){
                super();
             }
        
             public EntityFeedPage(EntityFeedPage page){
                super(page);
             }
        
             public EntityFeedPage(JSONObject jsonObject) throws JSONException {
                super(jsonObject);
             }
        
             @Override
             protected List<Entity> parseElementList(JSONArray jsonArray) throws JSONException {
                return Entity.ListFromJSONArray(jsonArray);
             }
    
         }
     *
     * The feed page is what a feed page will be about. In this case it will be about entities
     * 
     * SimpleFeedAdapter or FeedAdapter if it doesnt meet your requirements:
     *

         public class EntityAdapter extends SimpleFeedAdapter<Entity> {
    
             private EntityView.EntityViewListener listener = null;
    
             public EntityAdapter(Context context) {
                super(context);
             }
    
             public void setListener(EntityView.EntityViewListener listener) {
                this.listener = listener;
             }
        
             @Override
             protected View createElementView(ViewGroup parent) {
                EntityView view = new EntityView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.entity_view_height));
                view.setLayoutParams(params);
                view.setListener(listener);
                return view;
             }
        
             @Override
             protected void bindView(View view, int position) {
                if(view instanceof EntityView)
                    ((EntityView) view).setEntity(getFeedElement(getFeedElementIndexFromItemIndex(position)));
             }
    
         }
     *
     * The feed adapter is the one in charge of creating and binding views. This is as usual. Nothing new
     * 
     * FeedPageQuery. You will have to do a class that knows how to parse the query. And 2 classes, one for the initial request, and another for the "NextPage":
     *

         //Class that knows how to parse the Entity Feed Page 
         public abstract class EntityFeedPageQuery extends FeedPageQuery<Entity> {

             public EntityFeedPageQuery(Context context) {
                super(context);
             }

             public EntityFeedPageQuery(Context context, String accessToken) {
                super(context, accessToken);
             }
        
             @Override
             protected FeedPage<Entity> parsePage(JSONObject jsonObject) throws JSONException {
                return new EntityFeedPage(jsonObject);
             }

         }

         //First request
         public class EntityFeedFirstPageQuery extends EntityFeedPageQuery {

             public EntityFeedFirstPageQuery(Context context) {
                super(context);
             }

             public EntityFeedFirstPageQuery(Context context, String accessToken) {
                super(context, accessToken);
             }

             @Override
             protected String getRequestUrl() {
                return getFeed().getFirstPageFeedUrl();
             }
        
             @Override
             protected FeedPage<Entity> getTestingResponse() {
                return null;
             }
             
        }

        //Second request
        public class EntityFeedNextPageQuery extends EntityFeedPageQuery {

             public EntityFeedNextPageQuery(Context context) {
                super(context);
             }

             public EntityFeedNextPageQuery(Context context, String accessToken) {
                super(context, accessToken);
             }
        
             @Override
             protected String getRequestUrl() {
                return getFeed().getNextPageFeedUrl();
             }
        
             @Override
             protected FeedPage<Entity> getTestingResponse() {
                return null;
             }
             
        }
     *
     * This classes know what request url to do for asking the REST and what feed page to instantiate
     *
     * Pagination Manager:
     *

             public class EntityPaginationManager extends PaginationManager<Entity> {

             private String accessToken = null;

             public EntityPaginationManager(Context context) {
                super(context);
             }

             public void setAccessToken(String accessToken) {
                this.accessToken = accessToken;
             }

             @Override
             protected FeedPageQuery<Entity> getFeedFirstPageQuery() {
                return new EntityFeedFirstPageQuery(getContext(), accessToken);
             }

             @Override
             protected FeedPageQuery<Entity> getFeedNextPageQuery() {
                return new EntityFeedNextPageQuery(getContext(), accessToken);
             }
        }
     *
     * The pagination manager knows which of the request should be done (first or x page)
     *
     * Finally, the feed view:
     *

             public abstract class EntityFeedView<E> extends FeedView<E> {

                     private String baseUrl;
                     private String searchKey;

                     public EntityFeedView(Context context) {
                        super(context);
                     }

                     public EntityFeedView(Context context, AttributeSet attrs) {
                        super(context, attrs);

                        setProgressBackgroundResource(R.color.white);
                     }

                     public String getSearchKey() {
                        return searchKey;
                     }

                     public void setSearchKey(String key) {
                         if(key==null || key.isEmpty()){
                             this.searchKey=null;
                             removeFeedAdditionalParameter(ConstantsUrl.URL_ADDITIONAL_KEY);
                         } else {
                             this.searchKey=key;
                             setFeedAdditionalParameter(ConstantsUrl.URL_ADDITIONAL_KEY,key);
                         }
             
                         setBaseFeedUrl(baseUrl);
                         refreshFeed();
                     }


                     @Override
                     public void setBaseFeedUrl(String url) {
                         this.baseUrl = url;
                         cleanFeed();
                         if (baseUrl != null && getSearchKey() != null && !baseUrl.endsWith(EntityUrl.URL_SUFFIX_SEARCH)) {
                             super.setBaseFeedUrl(baseUrl + EntityUrl.URL_SUFFIX_SEARCH);
                             setEmptyView(getSearchEmptyView());
                         } else {
                             super.setBaseFeedUrl(baseUrl);
                             setEmptyView(getEmptyView());
                         }
                     }

                     abstract protected View getSearchEmptyView();

                     abstract protected View getEmptyView();

             }
     *
     * Its a common view that extends from FeedView.In this case its still abstract and it lets us set an emptyview and a search one.
     */
    
}
