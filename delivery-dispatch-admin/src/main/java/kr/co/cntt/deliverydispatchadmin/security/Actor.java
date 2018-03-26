package kr.co.cntt.deliverydispatchadmin.security;

import java.util.UUID;

public class Actor {
    private String loginId;
    private String loginPw;
    private String uuid;
    private String time;
    private String token;
    private String level;

    public Actor(String loginId, String loginPw, String level) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.uuid = UUID.randomUUID().toString().replaceAll("-", "");
        this.time = String.valueOf(System.currentTimeMillis());
        this.token = getTime();
        this.level = level;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getLoginPw() {
        return loginPw;
    }

    public String getUsername() {
        //return this.uuid;
        return this.loginId;
    }

    public String getPassword() {
        //return this.token;
        return this.loginPw;
    }

    public String getTime() {
        return this.time;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getLevel() {
        return level;
    }
}
