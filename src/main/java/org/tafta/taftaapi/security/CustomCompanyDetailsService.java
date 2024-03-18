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
import org.tafta.taftaapi.services.CompanyService;
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
public class CustomCompanyDetailsService implements UserDetailsService {
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CompanyService companyService;
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
        String apiKey, apiPassword = "", companyName = "", userId = "", companyEmail = "";
        boolean isActive = false, isApiAccessAllowed;
        List<GrantedAuthority> authorities = new ArrayList<>();

        try {
            Map<String, Object> company = companyService.searchCompany(username);
            isActive = company != null && Boolean.parseBoolean(String.valueOf(company.getOrDefault("status", "in-active")));
            isApiAccessAllowed = company != null && Boolean.parseBoolean(String.valueOf(company.getOrDefault("api_access", "false")));

            if(company != null && isActive && isApiAccessAllowed){
                apiKey = Optional.ofNullable(httpServletRequest.getHeader("api_key")).orElse("");
                apiPassword = Optional.ofNullable(httpServletRequest.getHeader("api_password")).orElse("");

                companyName = String.valueOf(company.get("company_name"));
                companyEmail = String.valueOf(company.get("company_email"));

                if (!(apiKey.isEmpty() || apiPassword.isEmpty())) {
                     if (securityService.comparePasswords(apiPassword, String.valueOf(company.get("api_password")))){
                        authorities = setGrantedAuthority(String.valueOf(company.get("role_id")));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new CustomUserDetails(authorities, companyEmail, companyName, userId, apiPassword, username, isActive,
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
