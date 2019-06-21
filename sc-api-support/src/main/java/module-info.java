module com.credits.sc.api.support {
    requires com.credits.general;
    exports com.credits.scapi.v0 to com.credits.contract.executor;
    exports com.credits.scapi.v1 to com.credits.contract.executor;
    exports exception;
    exports pojo;
    exports pojo.session;
    exports pojo.apiexec;
    exports service.executor;
    exports service.node;
    exports com.credits.scapi.annotations;
}