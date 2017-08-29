package com.livingspaces.proshopper.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.utilities.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by brandenwilson on 2015-02-09.
 */
public class NetworkManager {
    private static final String TAG = NetworkManager.class.getSimpleName();

    private static NetworkManager _networkManager;

    private ImageLoader _imgLoader;
    private RequestQueue _requestQueue;
    private Context _context;

    private boolean connected;

    private NetworkManager(Context context) {
        _context = context;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        connected = activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnected();
    }

    public static void Init(Context context) {
        _networkManager = new NetworkManager(context);
    }

    public static void makeREQ(IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.sendRequest(REQcb);
    }

    public static void makeLoginREQ(String name, String pass, IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.sendLoginRequest(name, pass, REQcb);
    }

    public static void makeResetPassREQ(String email, IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.sendResetPassRequest(email, REQcb);
    }

    public static void refreshTokenREQ(IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.refreshTokenRequest(REQcb);
    }

    public static void makeCreateAccREQ(String fname,
                                   String lname,
                                   String email,
                                   String pass,
                                   String confPass,
                                   IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.sendCreateAccRequest(fname, lname, email, pass, confPass, REQcb);
    }

    /*public static void addItemToCartREQ(IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.addToCartRequest(REQcb);
    }*/

    public static ImageLoader getIMGLoader() {
        if (_networkManager == null) return null;
        return _networkManager.getImageLoader();
    }

    protected RequestQueue getRequestQueue() {
        if (_requestQueue == null) _requestQueue = Volley.newRequestQueue(_context);
        return _requestQueue;
    }

    protected ImageLoader getImageLoader() {
        if (_imgLoader == null)
            _imgLoader = new ImageLoader(getRequestQueue(), new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });

        return _imgLoader;
    }

    protected <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    protected void cancelPendingRequests(Object tag) {
        if (_requestQueue != null) _requestQueue.cancelAll(tag);
    }

    protected void sendLoginRequest (final String name, final String pass, final IREQCallback REQcb) {
        if (REQcb == null) return;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REQcb.getURL(),
                response -> {
                    Log.d(TAG, "RSP Success :: " + response);
                    REQcb.onRSPSuccess(response);
                },
                error -> {
                    Log.d(TAG, error.toString());
                    REQcb.onRSPFail();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("grant_type", "password");
                params.put("username", name);
                params.put("password", pass);
                params.put("Client_id", "mobileapidev.livingspaces.com");
                params.put("Client_secret", "lsfsecret");

                return params;
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("grant_type", "password");
                headers.put("username", name);
                headers.put("password", pass);
                headers.put("Client_id", "mobileapidev.livingspaces.com");
                headers.put("Client_secret", "lsfsecret");

                return headers;
            }*/
        };

        Log.d(TAG, "REQ: " + stringRequest.toString() + " end");
        addToRequestQueue(stringRequest);
    }

    /*protected void addToCartRequest (final IREQCallback REQcb) {
        if (REQcb == null) return;

        JsonRequest<String> request = new JsonRequest<String>(Request.Method.GET, REQcb.getURL(), "",
                data -> {
                    Log.d(TAG, "RSP Success :: " + data);
                    REQcb.onRSPSuccess(data);
                },
                error -> REQcb.onRSPFail()) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String dataString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                return getDefHeaders(true);
            }
        };

        Log.d(TAG, "REQ: " + request.getUrl());
        addToRequestQueue(request);
    }*/

    protected void refreshTokenRequest (final IREQCallback REQcb) {
        if (REQcb == null) return;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REQcb.getURL(),
                response -> {
                    Log.d(TAG, "RSP Success :: " + response);
                    REQcb.onRSPSuccess(response);
                },
                error -> {
                    Log.d(TAG, error.toString());
                    REQcb.onRSPFail();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", Global.Prefs.getRefreshToken());
                params.put("Client_Id", "mobileapidev.livingspaces.com");
                params.put("client_secret", "lsfsecret");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("grant_type", "refresh_token");
                headers.put("refresh_token", Global.Prefs.getRefreshToken());
                headers.put("Client_Id", "mobileapidev.livingspaces.com");
                headers.put("client_secret", "lsfsecret");

                return headers;
            }
        };

        Log.d(TAG, "REQ: " + stringRequest.toString() + " end");
        addToRequestQueue(stringRequest);
    }

    protected void sendResetPassRequest (final String email, final IREQCallback REQcb) {
        if (REQcb == null) return;

        String finalUrl = REQcb.getURL() + "email=" + email;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, finalUrl,
                response -> {
                    Log.d(TAG, "RSP Success :: " + response);
                    REQcb.onRSPSuccess(response);

                },
                error -> {
                    Log.d(TAG, error.toString());
                    REQcb.onRSPFail();
                }) {
        };

        Log.d(TAG, "REQ: " + stringRequest.toString() + " end");
        addToRequestQueue(stringRequest);
    }

    protected void sendCreateAccRequest (final String fname,
                                    final String lname,
                                    final String email,
                                    final String pass,
                                    final String confPass,
                                    final IREQCallback REQcb) {
        if (REQcb == null) return;

        //if (!isConnectedToNetwork()) return;

        String finalUrl = REQcb.getURL() + "firstName=" + fname + "&" +
                "lastName=" + lname + "&" +
                "emailAddress=" + email + "&" +
                "password=" + pass + "&" +
                "confirmPass=" + confPass + "&" +
                "wantsNews=" + "false";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, finalUrl,
                response -> {
                    Log.d(TAG, "RSP Success :: " + response);
                    REQcb.onRSPSuccess(response);

                },
                error -> {
                    Log.d(TAG, error.toString());
                    REQcb.onRSPFail();
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(TAG, "calling getHeaders");

                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String dataString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        Log.d(TAG, "REQ: " + stringRequest.toString() + " end");
        addToRequestQueue(stringRequest);
    }



    protected void sendRequest(final IREQCallback REQcb) {
        Log.d(TAG, "sendRequest: " + REQcb.getURL());

        if (REQcb == null) return;

        JsonRequest<String> request = new JsonRequest<String>(Request.Method.GET, REQcb.getURL(), "",
                data -> {
                    Log.d(TAG, "RSP Success :: " + data);
                    REQcb.onRSPSuccess(data);
                },
                error -> REQcb.onRSPFail()) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String dataString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                return getDefHeaders(true);
            }
        };

        Log.d(TAG, "REQ: " + request.getUrl());
        addToRequestQueue(request);
    }

    public static Map<String, String> getDefHeaders(boolean forAPI) {
        Map<String, String> headers = new Hashtable<>();
        if (forAPI) {
            Log.d(TAG, "getDefHeaders: X_AUTH");
            headers.put(KeyValues.X_AUTH.first, KeyValues.X_AUTH.second) ;

        }
        else {
            Log.d(TAG, "getDefHeaders: MOB_APP");

            headers.put(KeyValues.MOB_APP.first, KeyValues.MOB_APP.second);
            if (Global.Prefs.hasToken()) {
                headers.put(KeyValues.TOKEN.first, KeyValues.TOKEN.second);
                headers.put(KeyValues.USERNAME.first, KeyValues.USERNAME.second);
            }
            else {
                headers.put(KeyValues.TOKEN.first, "");
                headers.put(KeyValues.USERNAME.first, "");
            }
            if (Global.Prefs.hasStore()) headers.put(KeyValues.STORE_ID.first, KeyValues.STORE_ID.second);
            else headers.put(KeyValues.STORE_ID.first, "");
            if (Global.Prefs.hasUserZip()) headers.put(KeyValues.USERZIP.first, KeyValues.USERZIP.second);
            else headers.put(KeyValues.USERZIP.first, "");
        }
        return headers;
    }

    public boolean isConnectedToNetwork() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public static class KeyValues {
        public static Pair<String, String> MOB_APP = new Pair<>("mobileApp", "android");
        public static Pair<String, String> X_AUTH = new Pair<>("X-Auth-Token", "3CCE9BEB-AC66-4F12-BF37-B3FA66E08325");
        public static Pair<String, String> TOKEN = new Pair<>("Authorization", "Bearer " + Global.Prefs.getAccessToken());
        public static Pair<String, String> USERNAME = new Pair<>("CustomerUsername", Global.Prefs.getUserId());
        public static Pair<String, String> STORE_ID = new Pair<>("StoreId", Global.Prefs.getStore().getId());
        public static Pair<String, String> USERZIP = new Pair<>("Zipcode", Global.Prefs.getUserZip());

    }
}
