package kr.co.cntt.core.model.notification;

public class Notification {
    private String type;
    private String id;
    private String addr;
    private String storeName;
    private String title;
    private String message;
    private String chat_user_id;
    private int rider_id;
    private String chat_room_id;       // 19.09.18 push 전송 시 채팅방번호 전송


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    // 19.09.18 push 전송 시 채팅방번호 전송
    public String getChat_room_id(){
        return chat_room_id;
    }

    // 19.09.18 push 전송 시 채팅방번호 전송
    public void setChat_room_id(String chat_room_id){
        this.chat_room_id = chat_room_id;
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
        public static String RIDER_WORKING_OFF= "rider_working_off";
        // 22.01.17 서버에서 라이더에게 도착지에 근접했다고 알림
        public static String RIDER_NEAR_ORDER = "rider_near_order";
        // 22.01.17 라이더가 자기 자신에게 PUSH를 보낼 때 사용
        public static String BEACON_PUSH = "beacon_push";
    }
}


