package kr.co.cntt.api.security;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
		actor.setIp(request.getRemoteAddr());
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
		return authorities;
	}

	public Collection<String> getAuthorityIds() {
		return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
	}
}
