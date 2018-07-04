package com.credits.thrift;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class ContractExecutorServer implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ContractExecutorServer.class);

    @Resource
    private ContractExecutorHandler handler;

    @Value("${executor.server.port}")
    private int port;

    private ContractExecutor.Processor processor;

    @PostConstruct
    public void setUp() {
        processor = new ContractExecutor.Processor(handler);
        new Thread(this).start();
    }

    @Override
    public void run() {
        serverStart(processor);
    }

    private void serverStart(ContractExecutor.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            logger.info("Starting the Thrift server on port {}...", port);
            server.serve();
        } catch (TTransportException e) {
            logger.error("Cannot start Thrift server on port " + port + ". " + e.getMessage(), e);
        }
    }
}
