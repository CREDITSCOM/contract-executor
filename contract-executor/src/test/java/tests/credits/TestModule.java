package tests.credits;

import com.credits.secure.PermissionsManager;
import com.credits.secure.Sandbox;
import com.credits.service.contract.ContractExecutorServiceImpl;
import com.credits.service.node.apiexec.NodeApiExecInteractionServiceImpl;
import com.credits.service.node.apiexec.NodeThriftApiExec;
import dagger.Module;
import dagger.Provides;
import service.executor.ContractExecutorService;
import service.node.NodeApiExecStoreTransactionService;

import javax.inject.Singleton;
import java.io.FilePermission;
import java.security.Permission;
import java.security.Permissions;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Module
public class TestModule {

    @Provides
    @Singleton
    ContractExecutorService provideContractExecutorService(NodeApiExecStoreTransactionService nodeApi, PermissionsManager permissionsManager) {
        return spy(new ContractExecutorServiceImpl(nodeApi, permissionsManager));
    }

    @Provides
    @Singleton
    NodeApiExecStoreTransactionService provideMockNodeApiStoreTransactionService(NodeThriftApiExec nodeThriftApiExec, ExecutorService executorService) {
        return spy(new NodeApiExecInteractionServiceImpl(nodeThriftApiExec, executorService));
    }

    @Provides
    @Singleton
    ExecutorService provideExecutorService(){
        return Executors.newCachedThreadPool();
    }
    @Provides
    @Singleton
    NodeThriftApiExec provideNodeThriftApiExecService(){
        return mock(NodeThriftApiExec.class);
    }

    @Singleton
    @Provides
    PermissionsManager providesPermissionsManager() {
        PermissionsManager permissionsManager = spy(PermissionsManager.class);
        doAnswer(invocation -> {
            final Class<?> contractClass = invocation.getArgument(0);
            final Permissions permissions = new Permissions();
            final Enumeration<Permission> permissionEnumeration = permissionsManager.getSmartContractPermissions().elements();
            while (permissionEnumeration.hasMoreElements()) {
                permissions.add(permissionEnumeration.nextElement());
            }
            permissions.add(new FilePermission("<<ALL FILES>>", "read"));
            Sandbox.confine(contractClass, permissions);
            return invocation;
        }).when(permissionsManager).dropSmartContractRights(any());
        return permissionsManager;
    }

}
