package com.example.peaky.repository.user;

import com.example.peaky.source.user.UserAuthenticationFirebaseDataSource;
import com.example.peaky.source.user.UserFirestoreFirebaseDataSource;

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