package kr.co.cntt.deliverydispatchadmin.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ActorDetails implements UserDetails {
    private static final long serialVersionUID = 1L;
    private Actor actor;
    private String password;
    private Collection<GrantedAuthority> authorities;

    public ActorDetails(Actor actor, Collection<GrantedAuthority> authorities) {
        this.actor = actor;
        this.password = actor.getPassword();
        this.authorities = authorities;
    }

    public Actor getActor() {
        return this.actor;
    }

    public ActorDetails bindRequestInfo(HttpServletRequest request) {
        //actor.setIp(request.getRemoteAddr());
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return actor.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // add Role
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (actor.getLevel().equals("1")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (actor.getLevel().equals("2")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_STORE"));
        } else if (actor.getLevel().equals("3")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_RIDER"));
        } else if (actor.getLevel().equals("4")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TRACKER"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }

    public Collection<String> getAuthorityIds() {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
