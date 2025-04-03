package com.example.peaky.source.user;

import com.example.peaky.model.user.User;
import com.example.peaky.repository.user.UserResponseCallback;

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String email, String password);
    public abstract void login(String email, String password);
    public abstract void authenticateWithGoogle(String idToken);
}
