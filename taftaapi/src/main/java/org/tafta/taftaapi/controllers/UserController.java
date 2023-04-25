package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tafta.taftaapi.services.UserService;

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

    @RequestMapping(value ="/api/v1/partner/topup/{partner_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPartnerTopUp() {
        return null;
    }

    @RequestMapping(value ="/api/v1/partner/topup", method = RequestMethod.POST)
    public ResponseEntity<Object> addPartnerFloatTopUp() {
        return null;
    }
    @RequestMapping(value ="/api/v1/partner/topup", method = RequestMethod.PUT)
    public ResponseEntity<Object> updatePartnerFloatTopUp() {
        return null;
    }
}
