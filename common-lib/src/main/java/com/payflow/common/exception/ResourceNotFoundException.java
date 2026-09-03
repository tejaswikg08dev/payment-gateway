package com.payflow.common.exception;

public class ResourceNotFoundException extends PayflowException {

    public ResourceNotFoundException(String resource, String id) {
        super("RESOURCE_NOT_FOUND", String.format("Resource '%s' with id '%s' not found", resource, id));
    }

    public ResourceNotFoundException(String message){
        super("RESOURCE_NOT_FOUND", message);
    }
}
