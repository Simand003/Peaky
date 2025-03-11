package com.example.peaky.repository;

import com.example.peaky.source.UserAuthenticationFirebaseDataSource;
import com.example.peaky.source.UserRealtimeDatabaseFirebaseDataSource;

public class UserRepositoryFactory {

    private static UserRepository userRepository;

    public static UserRepository getUserRepository() {
        if (userRepository == null) {
            UserAuthenticationFirebaseDataSource userAuthenticationFirebaseDataSource = new UserAuthenticationFirebaseDataSource();
            UserRealtimeDatabaseFirebaseDataSource userRealtimeDatabaseFirebaseDataSource = new UserRealtimeDatabaseFirebaseDataSource();

            userRepository = new UserRepository(userAuthenticationFirebaseDataSource,
                    userRealtimeDatabaseFirebaseDataSource);
        }
        return userRepository;
    }
}