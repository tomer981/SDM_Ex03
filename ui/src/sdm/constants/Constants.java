package sdm.constants;

public class Constants {
    public static final String USER_NAME = "user-name";
    public static final String USER_POSITION = "user-position";
    public static final String USER_NAME_ERROR = "username_error";

    public static final String ACTION = "action";
    public static final String ZONE_NAME = "zoneName";
    public static final String CUSTOMER_ORDER = "customerOrder";
    public static final String FEEDBACK_STORES_IDS = "feedbackStores";
    public static final String FEEDBACK_STORES_DATE = "feedbackDate";

    //StoresServlet
    public static final String GET_PRODUCTS_IN_ZONE_ACTION = "getProductsInZone";
    public static final String GET_STORE_INFO_ACTION = "getStoreInfo";
    public static final String GET_PRODUCTS_IN_STORE_ACTION = "getStoreProductsInfo";

    public static final String NEW_ORDER_ACTION = "newOrder";
    public static final String CUSTOMER_SHOW_ORDER_ACTION = "showOrders";
    public static final String MANAGER_SHOW_ORDER_ACTION = "showOrdersManager";
    public static final String SHOW_FEEDBACKS_ACTION= "showFeedbacks";
    public static final String NEW_STORE_ACTION = "newStore";
    public static final String GET_ORDERS_IN_STORE_ACTION = "getOrdersInStore";

    //ZoneServlet
    public static final String GET_USERS_POSITION_ACTION = "getKUsersVPosition";
    public static final String GET_USER_TRANSACTIONS_ACTION = "getUserTransactions";
    public static final String GET_ZONE_INFO_ACTION = "getZoneInfo";
    //LoginServlet
    public static final String IS_CUSTOMER_USER_ACTION = "isCustomer";
    public static final String IS_SESSION_EXIST_ACTION = "isSessionExist";


    //CustomerServlet
    public static final String GET_STORE_PRODUCTS_ACTION =  "getStoreProducts";//POST
    public static final String GET_ZONE_PRODUCTS_ACTION = "getZoneProducts";//POST
    public static final String ADD_NEW_ORDER_STATIC_PRODUCTS_ACTION = "addStoreProducts";//POST
    public static final String ADD_NEW_ORDER_DYNAMIC_PRODUCTS_ACTION = "addDynamicProducts";//POST
    public static final String GET_DISCOUNTS_ACTION = "getDiscounts";//GET
    public static final String ADD_DISCOUNT_TO_ORDER_ACTION = "addDiscountToOrder";//POST
    public static final String GET_STORE_ORDER_DETAILS_ACTION = "getStoreOrderDetails";//GET
    public static final String GET_STORES_IN_ORDER_DETAILS_ACTION = "getStoreInOrderInfo";//GET


    //ManagerServlet


    //OrderServlet
    public static final String GET_IN_PROGRESS_ORDER_ACTION =  "getInProgressOrder";//GET
    public static final String GET_DISPLAY_ORDER_ACTION =  "getDisplayOrder";//POST
    public static final String GET_SUB_ORDERS_CUSTOMER_ACTION =  "getSubOrdersCustomer";//POST
    public static final String GET_SUB_ORDERS_PRODUCTS_ACTION =  "getSubOrdersProducts";//POST
    public static final String CONFIRM_ORDER_ACTION =  "confirmOrder";//GET
    public static final String GET_ALL_ORDERS_IDS_IN_ZONE_CUSTOMER_ACTION =  "getAllOrdersIdsInZoneForCustomer";//GET
    public static final String LAST_STORE_INDEX = "lastStoreId";
    public static final String GET_SUB_ORDERS_INFO_FOR_MANAGER = "getSubOrdersInfoForManager";
    public static final String GET_SUB_ORDER_PRODUCTS_INFO_FOR_MANAGER = "getSubOrderProductsInfoForManager";

    //FeedbackServlet
    public static final String GET_FEEDBACK_IN_SYSTEM_ACTION = "getFeedbacksInSystem";
    public static final String  ADD_FEEDBACK_ACTION= "addFeedback";
    public static final String  GET_FEEDBACKS_IN_ZONE_TO_MANAGER_ACTION= "getFeedbacksInZoneToManager";


}