package com.credits.ioc;

import com.credits.ApplicationProperties;
import com.credits.secure.PermissionsManager;
import com.credits.service.contract.ContractExecutorServiceImpl;
import com.credits.service.node.apiexec.NodeApiExecInteractionServiceImpl;
import com.credits.service.node.apiexec.NodeThriftApiExec;
import com.credits.service.node.apiexec.NodeThriftApiExecClient;
import dagger.Module;
import dagger.Provides;
import service.executor.ContractExecutorService;
import service.node.NodeApiExecStoreTransactionService;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Module(includes = AppModule.class)
public class CEServiceModule {

    @Singleton
    @Provides
    public ContractExecutorService provideContractExecutorService(NodeApiExecStoreTransactionService nodeApiExecService,
                                                                  PermissionsManager permissionManager,
                                                                  ExecutorService executorService,
                                                                  ApplicationProperties properties) {
        return new ContractExecutorServiceImpl(nodeApiExecService, permissionManager, executorService, properties);
    }

    @Singleton
    @Provides
    public NodeApiExecStoreTransactionService provideNodeApiExecInteractionService(NodeThriftApiExec nodeThriftApiClient, ExecutorService executorService) {
        return new NodeApiExecInteractionServiceImpl(nodeThriftApiClient, executorService);
    }

    @Singleton
    @Provides
    public NodeThriftApiExec provideNodeThriftApi(ApplicationProperties properties) {
        return new NodeThriftApiExecClient(properties.nodeApiHost, properties.nodeApiPort);
    }

    @Singleton
    @Provides
    public ExecutorService provideExecutorService(){
        return Executors.newCachedThreadPool();
    }
}
