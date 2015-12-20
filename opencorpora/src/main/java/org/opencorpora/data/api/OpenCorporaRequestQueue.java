package org.opencorpora.data.api;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class OpenCorporaRequestQueue {
    private static OpenCorporaRequestQueue mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    private OpenCorporaRequestQueue(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized OpenCorporaRequestQueue getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new OpenCorporaRequestQueue(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mContext.getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }

        return mRequestQueue;
    }
}
