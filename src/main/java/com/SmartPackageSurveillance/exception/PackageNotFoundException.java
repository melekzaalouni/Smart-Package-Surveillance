package com.SmartPackageSurveillance.exception;

public class PackageNotFoundException extends RuntimeException {

    public PackageNotFoundException(String message) {
        super(message);
    }
}