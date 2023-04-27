package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tafta.taftaapi.services.UserService;

import java.util.Map;

/**
 * @author Gathariki Ngigi
 * Created on April 25, 2023.
 * Time 1514h
 */

@RestController
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value ="/api/v1/users/{user_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getUser() {
        return null;
    }

    // query param - scope(one, all), page(default = 1), per_page(default = 10), search, order(default = desc), orderby(default = date)
    @RequestMapping(value ="/api/v1/users", method = RequestMethod.GET)
    public ResponseEntity<Object> searchUser() {
        return null;
    }

    @RequestMapping(value ="/api/v1/users", method = RequestMethod.POST)
    public ResponseEntity<Object> registerUser(@RequestBody Map<String, Object> body) {
        return null;
    }
    @RequestMapping(value ="/api/v1/users/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateUser() {
        return null;
    }
    @RequestMapping(value ="/api/v1/users/{user_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser() {
        return null;
    }
}
