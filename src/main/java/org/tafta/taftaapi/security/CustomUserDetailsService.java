package org.tafta.taftaapi.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.tafta.taftaapi.services.RoleService;
import org.tafta.taftaapi.services.SecurityService;
import org.tafta.taftaapi.services.UserService;

import java.util.*;

/**
 * @author Gathariki Ngigi
 * Created on October 21, 2023.
 * Time 0115h
 * <p>
 * It is used to load user details from configured data store; to be used by Authentication Providers.
 */
@Slf4j
@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    SecurityService securityService;
    ObjectMapper mapper = new ObjectMapper();

    /**
     * @param username unique identifier to search the user from data store
     * @return UserDetails object
     * @see UserDetails
     * @see UserDetailsService
     */
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = "", password = "", name = "", userId = "";
        boolean isActive = false;
        List<GrantedAuthority> authorities = new ArrayList<>();

        try {
            Map<String, Object> user = userService.searchUser(username);
            isActive = user != null && Boolean.parseBoolean(String.valueOf(user.getOrDefault("status", "false")));

            if(isActive){
                email = Optional.ofNullable(httpServletRequest.getHeader("username")).orElse("");
                password = Optional.ofNullable(httpServletRequest.getHeader("password")).orElse("");
                name = String.valueOf(user.get("fullname"));
                userId = String.valueOf(user.get("id"));
                boolean apiAccess = Boolean.parseBoolean(String.valueOf(user.getOrDefault("api_access", "false")));

                if (!(email.isEmpty() || password.isEmpty())) {
                    if (apiAccess){
                        if(String.valueOf(user.get("password")).equals(password)){
                            authorities = setGrantedAuthority(String.valueOf(user.get("role_id")));
                        }
                    }else if (securityService.comparePasswords(password, String.valueOf(user.get("password")))){
                        authorities = setGrantedAuthority(String.valueOf(user.get("role_id")));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new CustomUserDetails(authorities, email, name, userId, password, username, isActive,
                !isActive, !isActive, !isActive
        );
    }

    private List<GrantedAuthority> setGrantedAuthority(String userRoles){
        List<GrantedAuthority> authorities = new ArrayList<>();

        try {
            List<String> roles = Arrays.stream(userRoles.split(",")).toList();
            String userRolePermissions = String.join(",", roles);

            Map<String, Object> rolePermissions = roleService.searchRolesPermissions(List.of(userRolePermissions));

            if(String.valueOf(rolePermissions.get("response_code")).equalsIgnoreCase("200")){
                List<Map<String, Object>> permissionsFound = mapper.convertValue(rolePermissions.get("response_code"), new TypeReference<>() {});
                List<String> permissions = permissionsFound.stream()
                        .map(stringObjectMap -> String.valueOf(stringObjectMap.get("action"))).toList();
                permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.toUpperCase())));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return authorities;
    }
}
