module com.credits.sc.api.support {
    requires com.credits.general;
    requires com.credits.sc.api.internal;
    exports com.credits.scapi.v0 to com.credits.contract.executor;
    exports com.credits.scapi.v1 to com.credits.contract.executor;
    exports com.credits.scapi.v2 to com.credits.contract.executor;
    exports com.credits.scapi.v3 to com.credits.contract.executor;
    exports exception to com.credits.contract.executor;
    exports pojo to com.credits.contract.executor;
    exports pojo.session to com.credits.contract.executor;
    exports pojo.apiexec to com.credits.contract.executor;
    exports service.executor to com.credits.contract.executor;
    exports service.node to com.credits.contract.executor;
    exports com.credits.scapi.annotations to com.credits.contract.executor;
    exports com.credits.scapi.misc to com.credits.contract.executor;
}