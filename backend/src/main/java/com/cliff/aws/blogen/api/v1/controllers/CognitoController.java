package com.cliff.aws.blogen.api.v1.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller handles redirects from a cognito authentication server in order to make sure the index.html page
 * is served to our single page application. NOTE THAT THIS CONTROLLER IS ONLY CALLED WHEN USING THE HOSTING SERVICES
 * PROVIDED BY COGNITO
 *
 * @author Cliff
 */
@Controller
public class CognitoController {

    @RequestMapping(
            method = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST},
            path = {"/cognito-signin*", "/cognito-signout*"} )
    public String forwardAngularPaths() {
        return "forward:/index.html";
    }
}
