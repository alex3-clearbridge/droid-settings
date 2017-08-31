/*
package com.livingspaces.proshopper.data;

import org.json.JSONException;
import org.json.JSONObject;

*/
/**
 * Created by rugvedambekar on 15-09-21.
 *//*

public class Item {
    public String title, sku, imgUrl;
    public int price;

    public Item(JSONObject jOBJ) {
        if (jOBJ == null) return;

        try {
            sku = jOBJ.getString(JSONKey.rowId.name());
            title = jOBJ.getString(JSONKey.description.name());
            price = jOBJ.getInt(JSONKey.price.name());


            if(JSONKey.signatureImage == null)
            {
                imgUrl = "";
            }
            else {
                JSONObject sigIMG = jOBJ.getJSONObject(JSONKey.signatureImage.name());
                imgUrl = sigIMG.getString(JSONKey.mediumUrl.name());
            }


        } catch (JSONException e) {
            e.printStackTrace(); }
    }

    public enum JSONKey {
        rowId, product, mediumUrl, description, price, signatureImage
    }
}
*/
