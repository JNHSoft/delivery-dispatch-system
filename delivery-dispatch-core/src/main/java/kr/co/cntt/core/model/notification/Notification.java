package kr.co.cntt.core.model.notification;

public class Notification {
    private String type;
    private int id;
    private String addr;
    private String storeName;
    private String title;
    private String message;
    private String chat_user_id;
    private int rider_id;


    public int getRider_id() {
        return rider_id;
    }

    public void setRider_id(int rider_id) {
        this.rider_id = rider_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChat_user_id() {
        return chat_user_id;
    }

    public void setChat_user_id(String chat_user_id) {
        this.chat_user_id = chat_user_id;
    }

    public interface NOTI{
        public static String ORDER_NEW = "order_new";
        public static String CHAT_SEND  ="chat_send";
        public static String ORDER_CHANGE = "order_change";
        public static String NOTICE_MASTER = "notice_master";
        public static String NOTICE_BRANCH = "notice_branch";
        public static String RIDER_LOCATION= "rider_location";
        public static String ORDER_ASSIGN= "order_assign";
        public static String ORDER_ASSIGN_AUTO= "order_assign_auto";
        public static String ORDER_PICKUP= "order_pickup";
        public static String ORDER_COMPLET= "order_complete";
        public static String ORDER_HIDDEN= "order_hidden";
        public static String ORDER_CANCEL= "order_cancle";
        public static String ORDER_ASSIGN_CANCEL = "order_assignment_cancel";
        public static String ORDER_ASSIGN_OTHER = "order_assign_other";
    }
}


