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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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

    public static void makePostREQ(String name, String pass, IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.sendPostRequest(name, pass, REQcb);
    }

    public static void makePostREQ(String email, IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.sendPostRequest(email, REQcb);
    }

    public static void makePostREQ(String fname,
                                   String lname,
                                   String email,
                                   String pass,
                                   String confPass,
                                   IREQCallback REQcb) {
        if (_networkManager == null) return;
        _networkManager.sendPostRequest(fname, lname, email, pass, confPass, REQcb);
    }

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

    protected void sendPostRequest (final String name, final String pass, final IREQCallback REQcb) {
        if (REQcb == null) return;

        //if (!isConnectedToNetwork()) return;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REQcb.getURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "RSP Success :: " + response);
                        REQcb.onRSPSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                    REQcb.onRSPFail();
                }
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
        };

        Log.d(TAG, "REQ: " + stringRequest.toString() + " end");
        addToRequestQueue(stringRequest);
    }

    protected void sendPostRequest (final String email, final IREQCallback REQcb) {
        if (REQcb == null) return;

        //if (!isConnectedToNetwork()) return;

        String finalUrl = REQcb.getURL() + "email=" + email;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, finalUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "RSP Success :: " + response);
                        REQcb.onRSPSuccess(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        REQcb.onRSPFail();
                    }
                }) {
        };

        Log.d(TAG, "REQ: " + stringRequest.toString() + " end");
        addToRequestQueue(stringRequest);
    }

    protected void sendPostRequest (final String fname,
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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "RSP Success :: " + response);
                        REQcb.onRSPSuccess(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        REQcb.onRSPFail();
                    }
                }) {

            /*@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG, "calling getParams");
                Log.d(TAG, fname + " " + lname + " " +email + " " +pass + " " +confPass);

                Map<String, String>  params = new HashMap<String, String>();
                params.put("firstName", fname);
                params.put("lastName", lname);
                params.put("emailAddress", email);
                params.put("password", pass);
                params.put("confirmPass", confPass);
                params.put("wantsNews", "false");

                return params;
            }*/

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
        if (REQcb == null) return;

        JsonRequest<String> request = new JsonRequest<String>(Request.Method.GET, REQcb.getURL(), "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        Log.d(TAG, "RSP Success :: " + data);
                        REQcb.onRSPSuccess(data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        REQcb.onRSPFail();
                    }
                }) {
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
        if (forAPI) headers.put(KeyValues.X_AUTH.first, KeyValues.X_AUTH.second) ;
        else
            headers.put(KeyValues.MOB_APP.first, KeyValues.MOB_APP.second);
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
    }
}
