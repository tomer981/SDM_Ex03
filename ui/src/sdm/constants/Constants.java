package sdm.constants;

public class Constants {
    public static final String USER_NAME = "user-name";
    public static final String USER_POSITION = "user-position";
    public static final String USER_NAME_ERROR = "username_error";

    public static final String ACTION = "action";
    public static final String ZONE_NAME = "zoneName";
    public static final String CUSTOMER_ORDER = "customerOrder";

    //StoresServlet
    public static final String GET_PRODUCTS_IN_ZONE_ACTION = "getProductsInZone";
    public static final String GET_STORE_INFO_ACTION = "getStoreInfo";
    public static final String GET_PRODUCTS_IN_STORE_ACTION = "getStoreProductsInfo";

    public static final String NEW_ORDER_ACTION = "newOrder";
    public static final String CUSTOMER_SHOW_ORDER_ACTION = "showOrders";
    public static final String MANAGER_SHOW_ORDER_ACTION = "showOrderManager";
    public static final String SHOW_FEEDBACKS_ACTION= "showFeedbacks";
    public static final String NEW_STORE_ACTION = "newStore";

    //ZoneServlet
    public static final String GET_USERS_POSITION_ACTION = "getKUsersVPosition";
    public static final String GET_USER_TRANSACTIONS_ACTION = "getUserTransactions";
    public static final String GET_ZONE_INFO_ACTION = "getZoneInfo";
    public static final String GET_STORE_URL_ACTION = "getStoresUrl";
    //LoginServlet
    public static final String IS_CUSTOMER_USER_ACTION = "isCustomer";
    public static final String IS_SESSION_EXIST_ACTION = "isSessionExist";


    //CustomerServlet
    public static final String GET_STORE_PRODUCTS_ACTION =  "getStoreProducts";//POST
    public static final String GET_ZONE_PRODUCTS_ACTION = "getZoneProducts";//POST
    public static final String ADD_NEW_ORDER_STATIC_PRODUCTS_ACTION = "addStoreProducts";//POST
    public static final String ADD_NEW_ORDER_DYNAMIC_PRODUCTS_ACTION = "addDynamicProducts";//POST
    public static final String GET_DISCOUNTS_ACTION = "getDiscounts";//GET


    //ManagerServlet


}