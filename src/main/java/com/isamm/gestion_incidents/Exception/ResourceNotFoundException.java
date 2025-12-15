package com.isamm.gestion_incidents.Exception;

public class ResourceNotFoundException extends RuntimeException { // ❌ Exception checked
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

