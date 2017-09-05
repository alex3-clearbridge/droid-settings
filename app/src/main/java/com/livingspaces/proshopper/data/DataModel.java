package com.livingspaces.proshopper.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rugvedambekar on 15-09-25.
 */
public class DataModel {
    private static final String TAG = DataModel.class.getSimpleName();

  /*  public static Item parseItem(String jsonSTR) {
        Item item = null;

        try {
            JSONObject jsonOBJ = new JSONObject(jsonSTR);
            item = new Item(jsonOBJ.getJSONObject(Item.JSONKey.product.name()));

        } catch (Exception e) { e.printStackTrace(); }

        return item;
    }

    public static List<Item> parseItems(String jsonSTR) {
        ArrayList<Item> items = new ArrayList<>();

        try {
            JSONArray jsonARRY = new JSONArray(jsonSTR);
            for (int i = 0; i < jsonARRY.length(); i++)
                items.add(new Item(jsonARRY.getJSONObject(i)));

        } catch (Exception e) { e.printStackTrace(); }

        Log.d(TAG, "Items Parsed: " + items.size());
        return items;
    }*/

    /*public static Store[] parseStores(String jsonSTR) {
        Store[] stores = null;

        try {
            JSONArray jsonARRY = new JSONArray(jsonSTR);
            stores = new Store[jsonARRY.length()];

            for (int i = 0; i < stores.length; i++) stores[i] = new Store(jsonARRY.getJSONObject(i));

        } catch (Exception e) { e.printStackTrace(); }

        Log.d(TAG, "Stores Parsed: " + (stores == null ? "NULL" : stores.length));
        return stores;

    }*/
}
