package com.example.peaky.model;

public abstract class Result<T> {

    public static class Success<T> extends Result<T> {
        private final T data;
        public Success(T data) {
            this.data = data;
        }
        public T getData() {
            return data;
        }
    }

    public static class Error<T> extends Result<T> {
        private final Exception exception;
        public Error(Exception exception) {
            this.exception = exception;
        }
        public Exception getException() {
            return exception;
        }
    }
}
