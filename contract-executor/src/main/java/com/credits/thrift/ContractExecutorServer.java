package com.credits.thrift;

import com.credits.ApplicationProperties;
import com.credits.client.executor.thrift.generated.ContractExecutor;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.credits.utils.Constants.*;

public class ContractExecutorServer implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ContractExecutorServer.class);
    private ContractExecutor.Processor processor;

    private final ApplicationProperties properties;

    @SuppressWarnings("unchecked")
    @Inject
    public ContractExecutorServer(ContractExecutorHandler contractExecutorHandler, ApplicationProperties applicationProperties) {
        processor = new ContractExecutor.Processor(contractExecutorHandler);
        properties = applicationProperties;
    }

    public void start() {
        validateContractExecutorVersion();
        new Thread(this).start();
    }

    private void validateContractExecutorVersion() {
        final var usingJdkVersion = System.getProperty("java.version");
        final var versionOnlyDecimal = usingJdkVersion.replaceAll("(^\\d*\\.\\d*\\.\\d*)(.*)","$1");
        if (!versionOnlyDecimal.equals(JDK_VERSION)) {
            logger.error("Incorrect jdk version. Using version is {} but expected {}", versionOnlyDecimal, JDK_VERSION);
            System.exit(INCORRECT_JDK_VERSION);
        }
    }

    @Override
    public void run() {
        logger.info("Contract Executor {} build {} commit {} is running...", properties.tag, properties.buildVersion, properties.commitId);
        serverStart(processor);
    }

    private void serverStart(ContractExecutor.Processor processor) {
        try {
            final var transport = properties.readClientTimeout > 0
                                        ? new TServerSocket(properties.executorPort, properties.readClientTimeout)
                                        : new TServerSocket(properties.executorPort);
            final var server = new TThreadPoolServer(new TThreadPoolServer.Args(transport).processor(processor));
            logger.info("Starting the Thrift server on port {}...", properties.executorPort);
            server.serve();
        } catch (TTransportException e) {
            logger.error("Cannot start Thrift server on port " + properties.executorPort + ". " + e.getMessage(), e);
            System.exit(CONTRACT_EXECUTOR_SERVER_START_ERROR);
        }
    }
}
