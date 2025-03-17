package com.example.peaky.repository.user;

import com.example.peaky.source.UserAuthenticationFirebaseDataSource;
import com.example.peaky.source.UserFirestoreFirebaseDataSource;

public class UserRepositoryFactory {

    private static UserRepository userRepository;

    public static UserRepository getUserRepository() {
        if (userRepository == null) {
            UserAuthenticationFirebaseDataSource userAuthenticationFirebaseDataSource = new UserAuthenticationFirebaseDataSource();
            UserFirestoreFirebaseDataSource userFirestoreFirebaseDataSource = new UserFirestoreFirebaseDataSource();

            userRepository = new UserRepository(userAuthenticationFirebaseDataSource,
                    userFirestoreFirebaseDataSource);
        }
        return userRepository;
    }
}