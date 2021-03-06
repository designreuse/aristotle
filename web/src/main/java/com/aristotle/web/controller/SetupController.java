package com.aristotle.web.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aristotle.core.exception.AppException;
import com.aristotle.core.persistance.Phone;
import com.aristotle.core.persistance.Sms;
import com.aristotle.core.persistance.User;
import com.aristotle.core.persistance.Phone.PhoneType;
import com.aristotle.core.service.HttpUtil;
import com.aristotle.core.service.SmsService;
import com.aristotle.core.service.UserService;
import com.aristotle.core.service.VideoDownloader;
import com.aristotle.core.service.aws.UserSearchService;
import com.aristotle.core.service.temp.LocationUpgradeService;
import com.aristotle.web.plugin.PluginManager;
import com.aristotle.web.ui.template.UiTemplateManager;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import aws.services.cloudsearchv2.search.Hit;

@Controller
public class SetupController {

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private UiTemplateManager uiTemplateManager;

    @Autowired
    private LocationUpgradeService locationUpgradeService;

    @Autowired
    private VideoDownloader videoDownloader;

    @Autowired
    private UserService userService;
    
    @Autowired
    private SmsService smsService;

    @Autowired
    private UserSearchService userSearchService;
    
    @ExceptionHandler({ Exception.class })
    public String handleException(Exception ex) {
        ex.printStackTrace();
        return ex.getMessage();
    }

    @RequestMapping("/sc/ui/refresh")
    @ResponseBody
    public String refreshTemplates(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) {
        uiTemplateManager.refresh();
        pluginManager.refresh();
        return "Success";
    }

    @RequestMapping("/sc/plugin/update")
    @ResponseBody
    public String updatePlugins(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws AppException {
        pluginManager.updateDbWithAllPlugins();
        return "Success";
    }

    @RequestMapping("/sc/location/update")
    @ResponseBody
    public String updateLocations(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws AppException {
        locationUpgradeService.copyCountries();
        locationUpgradeService.copyCountryRegions();
        locationUpgradeService.copyCountryRegionAreas();
        locationUpgradeService.copyStates();
        locationUpgradeService.copyPcs();
        locationUpgradeService.copyDistricts();
        locationUpgradeService.copyAcs();
        return "Success";
    }

    @RequestMapping("/sc/permissions/update")
    @ResponseBody
    public String updatePermissions(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        locationUpgradeService.copyLocationTypeRoles();
        return "Success";
    }

    @RequestMapping("/sc/video/update")
    @ResponseBody
    public String downloadVideos(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        if(httpServletRequest.getParameter("updateall") == null){
            videoDownloader.refreshVideoList(false);
        }else{
            videoDownloader.refreshVideoList(true);
        }

        return "Success";
    }

    @RequestMapping("/sc/userroles/update")
    @ResponseBody
    public String updateuserRoles(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        locationUpgradeService.copyUserRoles();
        return "Success";
    }

    @RequestMapping("/sc/userlocations/update")
    @ResponseBody
    public String updateuserLocations(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        locationUpgradeService.copyUserLocations();
        return "Success";
    }

    @RequestMapping("/sc/handlebar/test")
    @ResponseBody
    public String upsddateuserLocations(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        Handlebars handlebars = new Handlebars();

        Template template = handlebars.compileInline("Hello {{this}}!");

        String result = template.apply("Handlebars.java");
        return result;

    }

    @RequestMapping("/sc/admin/sendemail")
    @ResponseBody
    public String sendUserAccountEmail(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        String email = httpServletRequest.getParameter("email");
        if (email == null) {
            return "Emai is required";
        }
        try {
            userService.generateUserLoginAccount(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }

        return "done";
    }

    @RequestMapping("/sc/admin/sendverificationemail")
    @ResponseBody
    public String sendUserVerificationEmail(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        String email = httpServletRequest.getParameter("email");
        if (email == null) {
            return "Emai is required";
        }
        try {
            userService.sendEmailConfirmtionEmail(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }

        return "done";
    }
    
    @RequestMapping("/sc/admin/sendmemberemail")
    @ResponseBody
    public String sendMembershipEmail(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        String email = httpServletRequest.getParameter("email");
        if (email == null) {
            return "Emai is required";
        }
        try {
            userService.sendMembershipConfirmtionEmail(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }

        return "done";
    }
    
    @RequestMapping("/sc/admin/sendmembersmst")
    @ResponseBody
    public String sendMembershipSms(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        return sendSms(httpServletRequest, httpServletResponse, false);
    }
    @RequestMapping("/sc/admin/sendmembersmsp")
    @ResponseBody
    public String sendMembershipSmsP(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        return sendSms(httpServletRequest, httpServletResponse, true);
    }
    private String sendSms(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean promotional){
    	String mobile = httpServletRequest.getParameter("mobile");
        String message = httpServletRequest.getParameter("message");
        if (mobile == null) {
            return "mobile is required";
        }
        if (message == null) {
            return "Message is required";
        }
        try {
        	Phone phone = new Phone();
        	phone.setConfirmed(true);
        	phone.setCountryCode("91");
        	phone.setPhoneNumber(mobile);
        	phone.setPhoneType(PhoneType.MOBILE);
        	Sms sms = new Sms();
        	sms.setMessage(message);
            sms.setPhone(phone);
            sms.setPromotional(promotional);
            sms.setStatus("PENDING");
            sms.setUser(phone.getUser());
            if(promotional){
            	smsService.sendPromotionalSms(sms);	
            }else{
            	smsService.sendTransactionalSms(sms);
            }
        	
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }

        return "done";
    }
    
    @RequestMapping("/sc/admin/members")
    @ResponseBody
    public String searchMembers(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        String query = httpServletRequest.getParameter("query");
        String result = userSearchService.searchMembers(query);
        return result;
    }
    @RequestMapping("/sc/admin/memberrefresh")
    @ResponseBody
    public String refreshMembers(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        String userId = httpServletRequest.getParameter("userId");
        userSearchService.sendUserForIndexing(userId);
        return "done";
    }
    
    @RequestMapping("/sc/admin/district/memberrefresh")
    @ResponseBody
    public String refreshDistrictMembers(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
        String districtId = httpServletRequest.getParameter("districtId");
        String message = userSearchService.sendDistrictMemberForIndexing(districtId);
        return message;
    }
    
    @RequestMapping("/sc/admin/delete/member")
    @ResponseBody
    public List<String> deleteMemberMembers(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView) throws Exception {
    	
        try{
        	User user = (User) httpServletRequest.getSession().getAttribute("loggedInUser");
        	if(user == null){
        		throw new AppException("User not logged In");
        	}
        	Set<Long> allowedUsers = new HashSet<>();
        	allowedUsers.add(2L);//Ravi
        	allowedUsers.add(38L);//Neeraj
        	if(!allowedUsers.contains(user.getId())){
        		throw new AppException(user.getName() +" you are not allowed to do this operation");
        	}
            
        	String memberId = httpServletRequest.getParameter("mid");
            if(StringUtils.isEmpty(memberId)){
            	return Lists.newArrayList("mid must be provided");
            }
            List<String> status = userService.deleteUserByMemberId(memberId);
            return status;
        }catch(Throwable ex){
        	ex.printStackTrace();
        	return Lists.newArrayList(ex.getMessage());
        }
    	
    }
    
    

}
