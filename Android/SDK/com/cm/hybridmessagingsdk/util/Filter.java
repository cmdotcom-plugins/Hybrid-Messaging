package com.cm.hybridmessagingsdk.util;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Custom Filter for the HybridMessagingSDK based on OData
 */
public class Filter {

    public static String OPTION_EXPAND = "expand";
    public static String OPTION_FILTER = "filter";
    public static String OPTION_ORDER_BY = "orderby";
    public static String OPTION_SELECT = "select";
    public static String OPTION_SKIP = "skip";
    public static String OPTION_TOP = "top";

    private LinkedHashMap<String, String> mFilter;

    /**
     * Get the query string for the url to match with the OData standards
     * @param filter
     * @return
     */
    public static String buildFilter(Filter filter) {
        LinkedHashMap<String, String> params = filter.getFilter();

        if(params == null) { return null; }

        StringBuilder sb = new StringBuilder();
        Set<String> keys = params.keySet();
        int index = 0 ;

        for(String key : keys) {

            // Append no & sign when its the first parameter for ODATA.
            if(index == 0) { sb.append("?$"); }
            else { sb.append("&$"); }

            sb.append(key).append("=").append(params.get(key));
            index++;
        }

        return sb.toString();
    }

    // Constructor
    public Filter() {
        mFilter = new LinkedHashMap<String, String>();
    }

    /**
     * Add a filter to specify which or how to retrieve the data from the SDK
     * The filter is based on OData. Check their docs for more info.
     * @param option the action on the data (e.g. OPTION_SELECT)
     * @param query specify the action for the specified option
     */
    public void addFilter(String option, String query) {
        mFilter.put(option, query);
    }

    public LinkedHashMap<String, String> getFilter() {
        return mFilter;
    }
}
