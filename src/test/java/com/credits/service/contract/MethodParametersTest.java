package com.credits.service.contract;

import com.credits.exception.ContractExecutorException;
import com.credits.service.ServiceTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MethodParametersTest extends ServiceTest {
    private byte[] contractBytecode;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        contractBytecode = compileSourceCode("/methodParametersTest/Contract.java");
    }

    @Test
    public void primitiveExecutionTest() throws ContractExecutorException {
        String[] params = {"\"test string\"", "200", "3f"};
        ceService.execute(address, contractBytecode,null, "foo", params);
    }

    @Test
    public void objectExecutionTest() throws ContractExecutorException {
        String[] params = {"\"test string\"", "200d", "3"};
        ceService.execute(address, contractBytecode,null, "foo", params);
    }

    @Test
    public void arrayExecutionTest() throws ContractExecutorException {
        String[] params = {"{\"test1\", \"test2\", \"test3\"}"};
        ceService.execute(address, contractBytecode,null, "main", params);

        params = new String[] {"{1, 2, 3}"};
        ceService.execute(address, contractBytecode,null, "main", params);

        params = new String[] {"{1d, 2d, 3d}"};
        ceService.execute(address, contractBytecode,null, "main", params);
    }

    @Test
    public void arrayBooleanTest() throws ContractExecutorException {
        String[] params = {"{false, true}"};
        ceService.execute(address, contractBytecode,null, "foo", params);
    }

    @Test
    public void arrayIntTest() throws ContractExecutorException {
        String[] params = {"{1, 2}"};
        ceService.execute(address, contractBytecode,null, "foo", params);
    }

    @Test
    public void arrayShortTest() throws ContractExecutorException {
        String[] params = {"{(short)1, (short)2}"};
        ceService.execute(address, contractBytecode,null, "foo", params);
    }

    @Test
    public void arrayLongTest() throws ContractExecutorException {
        String[] params = {"{1l, 2l}"};
        ceService.execute(address, contractBytecode,null, "foo", params);
    }

    @Test
    public void arrayFloatTest() throws ContractExecutorException {
        String[] params = {"{1f, .2f}"};
        ceService.execute(address, contractBytecode,null, "foo", params);
    }

    @Ignore
    @Test
    public void moreVariousParameters() throws ContractExecutorException {
        String[] params = {
            "3f", "4f", "1", "2", "200d", "220d",
            "{\"string01\",\"string02\",\"string03\"},{1,2,3,4},{5,6,7,8},{1d,2d,3d},{4d,5d,6d},{true,true,false},{true,true,false},{(short)1,(short)2},{1l,2l,3l},{4l,5l,6l},{1f,.2f},{3f,.4f}"
        };
        ceService.execute(address,contractBytecode,null,"foo",params);
    }
}
