package org.tafta.taftaapi.security;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author Gathariki Ngigi
 * Created on October 12, 2023.
 * Time 1120h
 * <p>
 * Models core user information retrieved by a UserDetailsService.
 */
@Slf4j
public class CustomUserDetails implements UserDetails {
    final Collection<? extends GrantedAuthority> authorities;
    @Getter
    final String email;
    @Getter
    final String displayName;
    @Getter
    final  String userId;
    final String password;
    final String username;
    final boolean enabled;
    final boolean accountNonExpired;
    final boolean accountNonLocked;
    final boolean credentialsNonExpired;

    public CustomUserDetails(Collection<? extends GrantedAuthority> authorities,
                             String email,
                             String displayName,
                             String id,
                             String password,
                             String username,
                             boolean enabled,
                             boolean accountNonExpired,
                             boolean accountNonLocked,
                             boolean credentialsNonExpired) {
        this.authorities = authorities;
        this.email = email;
        this.displayName = displayName;
        this.userId = id;
        this.password = password;
        this.username = username;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * @return Returns the authorities granted to the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * @return Returns the password used to authenticate the user.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * @return Returns the username used to authenticate the user.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * @return Indicates whether the user's account has expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * @return Indicates whether the user is locked or unlocked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * @return Indicates whether the user's credentials (password) has expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * @return Indicates whether the user is enabled or disabled.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}