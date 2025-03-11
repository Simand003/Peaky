package com.example.peaky.util;

import android.app.Application;

import com.example.peaky.repository.IUserRepository;
import com.example.peaky.repository.UserRepository;
import com.example.peaky.source.BaseUserAuthenticationRemoteDataSource;
import com.example.peaky.source.UserAuthenticationFirebaseDataSource;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    /**
     * Returns an instance of ServiceLocator class.
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }
/*
    public IUserRepository getUserRepository(Application application) {
        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationFirebaseDataSource();

        return new UserRepository(userRemoteAuthenticationDataSource);
    }

 */
}
