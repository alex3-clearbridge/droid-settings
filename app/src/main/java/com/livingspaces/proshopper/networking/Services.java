package com.livingspaces.proshopper.networking;

/**
 * Created by rugvedambekar on 15-09-25.
 */
public class Services {

    //private static final String URL_BASE = "http://api.livingspaces.com/api/v1/"; //"http://apidev.livingspaces.com/api/v1/"//"http://mobileapidev.livingspaces.com/"
    private static final String URL_BASE = "http://apiark.livingspaces.com";

    public enum URL {
        //Product("http://www.livingspaces.com/ProductView.aspx?productId="), // "http://dev.livingspaces.com/ProductView.aspx?productId="
        Product("http://ark.livingspaces.com/Views/Mobile/productview.aspx?productId="),
        //SignIn("https://www.livingspaces.com/Secure/UserPortal/UserSignIn.aspx"),//"https://dev.livingspaces.com/Secure/UserPortal/UserSignIn.aspx"
        SignIn("https://ark.livingspaces.com/Secure/UserPortal/UserSignIn.aspx"),
        Subscribe("http://www.livingspaces.com/landingpage.aspx?fileName=app-email-sign-up"),
        Website("http://ark.livingspaces.com"),
        //ShareProduct("http://www.livingspaces.com/landingpage.aspx?fileName=wish-list&")
        Cart("http://ark.livingspaces.com/Views/Mobile/ViewCart.aspx"),
        PrivacyPolicy("http://ark.livingspaces.com/privacy-policy-app"),
        StorePolicy("http://ark.livingspaces.com/store-policies-app"),
        Terms("http://ark.livingspaces.com/terms-of-use-app"),
        About("http://ark.livingspaces.com/about-our-ads-app"),
        ShareProduct("http://ark.livingspaces.com/landingpage.aspx?fileName=wish-list&"); //"http://dev.livingspaces.com/landingpage.aspx?fileName=wish-list&"

        private String url;

        private URL(String u) {
            url = u;
        }

        public String get() {
            return url;
        }
    }

    public enum API {
        Product("api/Product/"),
        Main(""),
        Products("products/"),
        Stores("store/getAllStores/"),
        StoresWithZip("store/getAllStoresByZip/"),
        Token("token"),
        //AddToCart("api/Product/addToCart?"),
        CreateAccount("api/account/createAccount?"),
        ResetPassword("api/account/forgotPassword?");

        private String path;

        private API(String p) {
            path = p;
        }

        public String get() {
            return URL_BASE + path;
        }

        public String getByZip(String s) {
            if (s == null) return URL_BASE + "store/getAllStores/";

            try {
                // is numeric
                Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                // not numeric
                s = "";
            }

            return URL_BASE + path + s;
        }
    }

}
