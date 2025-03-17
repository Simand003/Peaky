package com.example.peaky.repository.user;

public interface UserResponseCallback<T> {
    void onComplete(T result);
}
