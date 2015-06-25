package com.bibsmobile.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bibsmobile.model.UserProfile;

@Controller
public class PasswordController {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private SimpleMailMessage forgotPasswordMessage;

    @Autowired
    private StandardPasswordEncoder encoder;

    @Value("${email.forgotPassword.text}")
    private String resetPasswordText;

    @Value("${email.forgotPassword.url}")
    private String resetPasswordUrl;

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public String forgotPassword() {
        return "password/forgotPassword";
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ModelAndView forgotPassword(@RequestParam String email) {
        ModelAndView modelAndView = new ModelAndView("password/forgotPassword");
        if (UserProfile.countFindUserProfilesByEmailEquals(email) > 0) {
            UserProfile userProfile = UserProfile.findUserProfilesByEmailEquals(email).getSingleResult();
            String forgotPasswordCode = UUID.randomUUID().toString();
            userProfile.setForgotPasswordCode(forgotPasswordCode);
            userProfile.persist();
            String body = this.resetPasswordText.replace("{link}", this.resetPasswordUrl + forgotPasswordCode);
            this.forgotPasswordMessage.setText(body);
            this.forgotPasswordMessage.setTo(userProfile.getEmail());
            try {
                this.mailSender.send(this.forgotPasswordMessage);
            } catch (Exception e) {
                System.out.println("EXCEPTION: Email Send Fail - " + e.getMessage());
            }
            modelAndView.addObject("success", true);
            return modelAndView;
        }
        modelAndView.addObject("error", "User with such email was not found");
        return modelAndView;
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public ModelAndView resetPassword(@RequestParam String code) {
        ModelAndView modelAndView = new ModelAndView("password/resetPassword");
        if (UserProfile.countFindUserProfilesByForgotPasswordCodeEquals(code) > 0) {
            modelAndView.addObject("code", code);
            return modelAndView;
        }
        modelAndView.addObject("error", "Invalid code");
        return modelAndView;
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView doResetPassword(@RequestParam String password, @RequestParam String code) {
        ModelAndView modelAndView = new ModelAndView("password/resetPassword");
        if (UserProfile.countFindUserProfilesByForgotPasswordCodeEquals(code) > 0) {
            UserProfile userProfile = UserProfile.findUserProfilesByForgotPasswordCodeEquals(code).getSingleResult();
            userProfile.setPassword(encoder.encode(password));
            userProfile.setForgotPasswordCode(null);
            userProfile.persist();
            modelAndView.addObject("success", true);
            return modelAndView;
        }
        modelAndView.addObject("error", "invalid code");
        return modelAndView;
    }
}