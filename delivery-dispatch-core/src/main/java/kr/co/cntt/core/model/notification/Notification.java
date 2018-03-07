package kr.co.cntt.core.model.notification;

public class Notification {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    }
}


