package com.credits.secure;

import java.lang.reflect.ReflectPermission;
import java.security.AllPermission;
import java.security.Permissions;
import java.util.PropertyPermission;

public class PermissionsManager {
    private final Permissions smartContractPermissions;

    public PermissionsManager() {
        smartContractPermissions = new Permissions();
        smartContractPermissions.add(new ReflectPermission("suppressAccessChecks"));
        smartContractPermissions.add(new RuntimePermission("accessDeclaredMembers"));
        smartContractPermissions.add(new RuntimePermission("createClassLoader"));
        smartContractPermissions.add(new RuntimePermission("getProtectionDomain"));
        smartContractPermissions.add(new RuntimePermission("defineClass"));
        smartContractPermissions.add(new PropertyPermission("sun.io.serialization.extendedDebugInfo","read"));
        smartContractPermissions.add(new PropertyPermission("java.version","read"));
    }

    public void dropSmartContractRights(Class<?> contractClass) {
        Sandbox.confine(contractClass, getSmartContractPermissions());
    }

    public void grantAllPermissions(Class<?> clazz) {
        final Permissions permissions = new Permissions();
        permissions.add(new AllPermission());
        Sandbox.confine(clazz, permissions);
    }

    public Permissions getSmartContractPermissions() {
        return smartContractPermissions;
    }
}
