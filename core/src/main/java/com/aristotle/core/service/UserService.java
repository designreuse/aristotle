package com.aristotle.core.service;

import java.util.List;

import com.aristotle.core.exception.AppException;
import com.aristotle.core.persistance.User;
import com.aristotle.core.persistance.UserLocation;
import com.aristotle.core.service.dto.SearchUser;
import com.aristotle.core.service.dto.UserContactBean;
import com.aristotle.core.service.dto.UserRegisterBean;
import com.aristotle.core.service.dto.UserSearchResult;

public interface UserService {

    void registerUserQuick(UserContactBean userContactBean) throws AppException;

    void registerUser(UserRegisterBean userRegisterBean) throws AppException;

    List<UserSearchResult> searchUsers(SearchUser searchUser) throws AppException;

    User login(String userName, String password) throws AppException;

    List<UserLocation> getUserLocations(Long userId) throws AppException;

    List<UserSearchResult> searchUserByEmail(String emailId) throws AppException;

    List<UserSearchResult> searchNriUserForVolunteerIntrest(List<Long> intrests) throws AppException;

    List<UserSearchResult> searchGlobalUserForVolunteerIntrest(List<Long> intrests) throws AppException;

    List<UserSearchResult> searchLocationUserForVolunteerIntrest(Long locationId, List<Long> intrests) throws AppException;
}
