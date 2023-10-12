package org.tafta.taftaapi.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author Gathariki Ngigi
 * Created on October 12, 2023.
 * Time 1120h
 * <p>
 * The class extends the capability of the User details interface
 */
public class CustomUserDetails implements UserDetails {
    private final Collection<? extends GrantedAuthority> authorities;
    @Getter
    private final String email;
    @Getter
    private final String displayName;
    @Getter
    private final  String userId;
    @Getter
    private final String userCompanyId;
    private final String password;
    private final String username;
    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;

    public CustomUserDetails(Collection<? extends GrantedAuthority> authorities,
                             String email,
                             String displayName,
                             String id,
                             String userCompanyId,
                             String password,
                             String username,
                             boolean enabled,
                             boolean accountNonExpired,
                             boolean accountNonLocked,
                             boolean credentialsNonExpired) {
        this.authorities = authorities;
        this.email = email;
        this.userCompanyId = userCompanyId;
        this.displayName = displayName;
        this.userId = id;
        this.password = password;
        this.username = username;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}