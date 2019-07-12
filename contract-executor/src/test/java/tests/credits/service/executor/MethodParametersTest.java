package tests.credits.service.executor;

import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.thrift.generated.Variant;
import com.credits.pojo.MethodData;
import exception.ContractExecutorException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.credits.service.ContractExecutorTestContext;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.credits.general.serialize.Serializer.deserialize;
import static com.credits.general.thrift.generated.Variant.*;
import static com.credits.utils.ContractExecutorServiceUtils.castValues;
import static com.credits.utils.ContractExecutorServiceUtils.getMethodArgumentsValuesByNameAndParams;
import static java.util.Arrays.asList;
import static tests.credits.TestContract.MethodParameterTestContract;

public class MethodParametersTest extends ContractExecutorTestContext {

    private Class<?> contractClass;
    private ByteCodeContractClassLoader byteCodeContractClassLoader;
    private byte[] contractState;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        final var smartContract = smartContractsRepository.get(MethodParameterTestContract);
        contractState = deploySmartContract(smartContract).newContractState;
        contractClass = smartContract.getContractClass();
        byteCodeContractClassLoader = (ByteCodeContractClassLoader) contractClass.getClassLoader();
    }

    @Test
    public void findVoidMethod() throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        Variant[] voidParams = {};
        MethodData voidMethod = getMethodArgumentsValuesByNameAndParams(contractClass, "foo", voidParams, getClass().getClassLoader());

        Assert.assertEquals(voidMethod.method.toString(), "public static java.lang.Integer MethodParametersTestContract.foo()");

        Integer invokeResult = (Integer) voidMethod.method.invoke(
                deserialize(contractState, byteCodeContractClassLoader),
                castValues(voidMethod.argTypes, voidParams, getClass().getClassLoader()));
        Assert.assertEquals(Integer.valueOf(1), invokeResult);
    }

    @Test
    public void findSimpleMethod() throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {

        Variant[] simpleParams = {v_double(3f), v_double_box(4f), v_int(1), v_int_box(2), v_double(200d), v_double_box(220d)};
        MethodData simpleMethod = getMethodArgumentsValuesByNameAndParams(contractClass, "foo", simpleParams, getClass().getClassLoader());
        Assert.assertEquals(
                simpleMethod.method.toString(),
                "public java.lang.Integer MethodParametersTestContract.foo(double,java.lang.Double,int,java.lang.Integer,double,java.lang.Double)");
        Object invoke = simpleMethod.method
                .invoke(
                        deserialize(contractState, byteCodeContractClassLoader),
                        castValues(simpleMethod.argTypes, simpleParams, getClass().getClassLoader()));
        Integer invokeResult = (Integer) invoke;
        Assert.assertEquals(Integer.valueOf(1), invokeResult);

    }

    @Test
    public void findMethodWithArrayList() throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {

        Variant[] arrayList = {v_list(new ArrayList<>(asList(v_int(1), v_int(2), v_int(3))))};

        MethodData arrayListMethod = getMethodArgumentsValuesByNameAndParams(contractClass, "foo", arrayList, getClass().getClassLoader());
        Assert.assertEquals(
                arrayListMethod.method.toString(),
                "public java.lang.Integer MethodParametersTestContract.foo(java.util.List)");
        Object invoke = arrayListMethod.method
                .invoke(
                        deserialize(contractState, byteCodeContractClassLoader),
                        castValues(arrayListMethod.argTypes, arrayList, getClass().getClassLoader()));
        Integer invokeResult = (Integer) invoke;
        Assert.assertEquals(Integer.valueOf(1), invokeResult);
    }

    @Test
    public void findAnotherMethodWithArrayList() throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {

        Variant[] arrayList = {v_list(new ArrayList<>(asList(v_int(1), v_int(2), v_int(3))))};
        MethodData arrayListMethod = getMethodArgumentsValuesByNameAndParams(contractClass, "fooInteger",
                                                                             arrayList, getClass().getClassLoader());
        Assert.assertEquals(
                arrayListMethod.method.toString(),
                "public java.lang.Integer MethodParametersTestContract.fooInteger(java.util.List)");
        Object invoke = arrayListMethod.method
                .invoke(
                        deserialize(contractState, byteCodeContractClassLoader),
                        castValues(arrayListMethod.argTypes, arrayList, getClass().getClassLoader()));
        Integer invokeResult = (Integer) invoke;
        Assert.assertEquals(Integer.valueOf(1), invokeResult);

    }


    @Test
    public void findMethodWithSimpleParamsAndArrayList() throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {

        List<Variant> list = new ArrayList<>();
        list.add(v_string("string01"));
        list.add(v_string("string01"));
        list.add(v_string("string01"));

        Variant[] simpleParamsWithList =
                {v_double(3f), v_double(4f), v_int(1), v_int(2), v_double(200d), v_double(220d), v_list(list)};

        MethodData simpleAndArrayListMethod = getMethodArgumentsValuesByNameAndParams(contractClass,
                                                                                      "foo",
                                                                                      simpleParamsWithList,
                                                                                      getClass().getClassLoader());

        Assert.assertEquals(
                simpleAndArrayListMethod.method.toString(),
                "public java.lang.Integer MethodParametersTestContract.foo(double,java.lang.Double,int,java.lang.Integer,double,java.lang.Double,java.util.ArrayList)");
        Object invoke = simpleAndArrayListMethod.method
                .invoke(
                        deserialize(contractState, byteCodeContractClassLoader),
                        castValues(simpleAndArrayListMethod.argTypes, simpleParamsWithList, getClass().getClassLoader()));
        Integer invokeResult = (Integer) invoke;
        Assert.assertEquals(Integer.valueOf(1), invokeResult);
    }

    @Test
    public void moreVariousParameters()
    throws ContractExecutorException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        List<Variant> list = new LinkedList<>();
        list.add(v_string("string01"));
        list.add(v_string("string01"));
        list.add(v_string("string01"));
        Variant[] params =
                {
                        v_double(3f), v_double(4f), v_int(1), v_int(2), v_double(200d), v_double(220d), v_list(list),
                        v_list(new ArrayList<>(asList(v_int(1), v_int(2), v_int(3), v_int(4)))),
                        v_list(new ArrayList<>(asList(v_int(5), v_int(6), v_int(7), v_int(8)))),
                        v_list(new ArrayList<>(asList(v_double(1d), v_double(2d), v_double(3d)))),
                        v_list(new ArrayList<>(asList(v_double(4d), v_double(5d), v_double(6d)))),
                        v_list(new ArrayList<>(asList(v_boolean(true), v_boolean(true), v_boolean(false)))),
                        v_list(new ArrayList<>(asList(v_boolean(true), v_boolean(true), v_boolean(false)))),
                        v_list(new ArrayList<>(asList(v_int((short) 1), v_int((short) 2)))),
                        v_list(new ArrayList<>(asList(v_long(1L), v_long(2L), v_long(3L)))),
                        v_list(new ArrayList<>(asList(v_long(4L), v_long(5L), v_long(6L)))),
                        v_list(new ArrayList<>(asList(v_double(1f), v_double(.2f)))),
                        v_list(new ArrayList<>(asList(v_double(3f), v_double(.4f))))
                };

        MethodData moreVariousParametersMethod = getMethodArgumentsValuesByNameAndParams(contractClass, "foo", params, getClass().getClassLoader());
        Assert.assertEquals(
                moreVariousParametersMethod.method.toString(),
                "public java.lang.Integer MethodParametersTestContract.foo(double,java.lang.Double,int,java.lang.Integer,double,java.lang.Double,java.util.List,java.util.List,java.util.List,java.util.List,java.util.List,java.util.List,java.util.List,java.util.List,java.util.List,java.util.List,java.util.List,java.util.List)");
        Object invoke = moreVariousParametersMethod.method
                .invoke(
                        deserialize(contractState, byteCodeContractClassLoader),
                        castValues(moreVariousParametersMethod.argTypes, params, getClass().getClassLoader()));
        Integer invokeResult = (Integer) invoke;
        Assert.assertEquals(Integer.valueOf(1), invokeResult);
    }

}
