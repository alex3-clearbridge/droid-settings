package com.livingspaces.proshopper.networking;

/**
 * Created by rugvedambekar on 15-09-25.
 */
public class Services {

    public static final String URL_BASE = "https://www-x.livingspaces.com";
    private static final String BASE_API_URL = "https://api.livingspaces.com";

    public enum URL {
        Product(URL_BASE + "/Views/Mobile/productview.aspx?productId="),
        SignIn(URL_BASE + "/Secure/UserPortal/UserSignIn.aspx"),
        Subscribe(URL_BASE + "/landingpage.aspx?fileName=app-email-sign-up"),
        Website(URL_BASE),
        Cart(URL_BASE + "/Views/Mobile/ViewCart.aspx"),
        PrivacyPolicy(URL_BASE + "/privacy-policy-app"),
        StorePolicy(URL_BASE + "/store-policies-app"),
        Terms(URL_BASE + "/terms-of-use-app"),
        About(URL_BASE + "/about-our-ads-app"),
        ShareProduct(URL_BASE + "/landingpage.aspx?fileName=wish-list&");

        private String url;

        URL(String u) {
            url = u;
        }

        public String get() {
            return url;
        }

        public enum ForComparison {
            SignInPrompt(URL_BASE + "/SignInPrompt"),
            MobileProductView(URL_BASE + "/Views/Mobile/productview.aspx?productId="),
            ProductView(URL_BASE + "/ProductView.aspx?productId="),
            ViewCart(URL_BASE + "/ViewCart.aspx");

            private String urlForComparison;

            ForComparison(String ufc) {
                urlForComparison = ufc;
            }

            public String get() {
                return urlForComparison;
            }
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
            return BASE_API_URL + path;
        }

        public String getByZip(String s) {
            if (s == null) return BASE_API_URL + "store/getAllStores/";

            try {
                // is numeric
                Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                // not numeric
                s = "";
            }

            return BASE_API_URL + path + s;
        }
    }

}
