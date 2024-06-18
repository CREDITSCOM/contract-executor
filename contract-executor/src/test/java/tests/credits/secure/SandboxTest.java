package tests.credits.secure;

import com.credits.secure.Sandbox;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FilePermission;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.security.AccessControlException;
import java.security.AllPermission;
import java.security.Permissions;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.fail;


//@Disabled
public class SandboxTest {

    @Test
    public void confineTest() throws IOException {
        System.out.println("confineTest");
        UnsafeClass unsafeClass = new UnsafeClass();

        //unsafeClass.openSocket(1500);
        Permissions permissions = new Permissions();
        Sandbox.confine(unsafeClass.getClass(), permissions);

        try {
            unsafeClass.openSocket(1500);
//        } catch (SecurityException e) {
//            System.out.println("confineTest - test passed: " + e.getMessage());
//            return;
        } catch (java.lang.ExceptionInInitializerError e) {
            System.out.println("confineTest - exception: " + e.getMessage());
            return;
        }
        fail("confineTest: UnsafeClass can use socket");
    }

    @Test
    public void getPermission() {
        //System.out.println("getPermissionTest");
        UnsafeClass unsafeClass = new UnsafeClass();

        Sandbox.confine(unsafeClass.getClass(), new Permissions());
        unsafeClass.setValue(10);
        try {
            unsafeClass.addMorePermissions();
        } catch (SecurityException e) {
            System.out.println("getPermissionTest - test passed: " + e.getMessage());
            return;
        }
        fail("getPermissionTest: UnsafeClass add yourself permissions");
    }

    @Test
    public void fileSaveOpen() throws Exception {
        //System.out.println("fileOpenTest");
        String appPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        UnsafeClass unsafeClass = new UnsafeClass();

        Permissions permissions = new Permissions();
        //permissions.add(new FilePermission(appPath + "\\-", "read"));
        Sandbox.confine(unsafeClass.getClass(), permissions);
        try{
            unsafeClass.readWriteFile();
        } catch (SecurityException e) {
            System.out.println("fileOpenTest: test passed " + e.getMessage());
            return;
        }
        fail("fileOpenTest: UnsafeClass can write/read file");
        
    }
    
    @Disabled
    public void callChildMethod() throws Exception {
        System.out.println("callChildMethodTest");
        //String appPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        UnsafeClass unsafeClass = new UnsafeClass();

        Permissions permissions = new Permissions();
        //permissions.add(new FilePermission(appPath + "\\-", "read"));
        Sandbox.confine(unsafeClass.getClass(), permissions);
        try{
            unsafeClass.callChildMethod();   
        } catch (SecurityException e) {
            System.out.println("callChildMethodTest: not successfull - test passed");
            return;
        }
        fail("callChildMethodTest: UnsafeClass callChildMethod ok");
        
    }

    @Test
    public void reflectionUse() throws InstantiationException, IllegalAccessException {
        //System.out.println("reflectionUseTest");
        UnsafeClass unsafeClass = new UnsafeClass();
        Sandbox.confine(unsafeClass.getClass(), new Permissions());

        UnsafeClass instance = null;
        int failCount = 0;
        try {
            instance = unsafeClass.createInstance();
            instance.setValue(1);
            failCount += 4;
            //fail("Fail 1: reflectionUseTest - create instances - ok");
        } catch (SecurityException e) {
            System.out.println("reflectionUseTest: instance not created - test passed");

        }

        try {
            Method method = instance.getClass().getMethod("setValue", int.class);
            method.invoke(null, 1);
            failCount += 2;
            //fail("Fail 2: reflectionUseTest - get method - ok");
        } catch (SecurityException e) {
            System.out.println("reflectionUseTest: method not get - test passed");
        } catch (Exception e) {
            System.out.println("some exception: " + e.getMessage());
        }
        
        try {
            unsafeClass.invokeConstructor(UnsafeClass.class);
            failCount += 1;
        }
         catch (SecurityException e) {
            System.out.println("reflectionUseTest: constructor not invoked - test passed");
            return ;
        }
        if(failCount > 0){
            fail("reflectionUseTest: constructor not invoked - reflectionUse - Failed");   
        }
        System.out.println("reflectionUseTest: All tests passed");
    }

    @Disabled
    public void threadSafetyTest() throws InterruptedException {
        System.out.println("threadSafetyTest");
        int amountThreads = 10_000;
        CountDownLatch count = new CountDownLatch(amountThreads);

        UnsafeClass[] unsafeClasses =
            IntStream.range(0, amountThreads).mapToObj(i -> new UnsafeClass()).toArray(UnsafeClass[]::new);

        Thread[] locks = IntStream.range(0, amountThreads).mapToObj(i -> new Thread(() -> {
            try {
                Sandbox.confine(unsafeClasses[i].getClass(), new Permissions());
                fail("threadSafetyTest:  Failed");
            } catch (SecurityException ignored) {
                System.out.println("threadSafetyTest: test passed");
            }

        })).toArray(Thread[]::new);

        Thread[] checks = IntStream.range(0, amountThreads).mapToObj(i -> new Thread(() -> {
            try {
                unsafeClasses[i].openSocket(1000 + i);
            } catch (AccessControlException e) {
                count.countDown();
            } catch (Exception e) {
                fail(ExceptionUtils.getRootCauseMessage(e));
            }
        })).toArray(Thread[]::new);

        for (int i = 0; i < amountThreads; i++) {
            locks[i].start();
            checks[i].start();
        }
        count.await(1, TimeUnit.SECONDS);
        //        assertEquals(0, count.getCount());
    }

    @Disabled
    public void test() throws Exception {
        class SomeExternalClassLodaer extends ClassLoader {
            @Override
            protected Class<?> findClass(String s) throws ClassNotFoundException {
                return super.findClass(s);
            }
        }
        System.out.println("ClassLoader_test");
        System.out.println(getClass().getClassLoader());
        UnsafeClass privelegedClass = (UnsafeClass) Class.forName("com.credits.secure.SandboxTest$UnsafeClass", false,
                                                                  new SomeExternalClassLodaer()).getDeclaredConstructor().newInstance();
        Sandbox.confine(privelegedClass.getClass(), new Permissions());
        System.out.println(privelegedClass.getClass().getClassLoader());
        privelegedClass.createInstance();
    }

    static class UnsafeClass {

        private int value = 0;

        public void openSocket(int port) throws IOException {
            System.out.println("opening socket...");
            new ServerSocket(port);
            System.out.println("success");
        }

        public void addMorePermissions() {
            Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            Sandbox.confine(UnsafeClass.class, permissions);
        }
        
        public void readWriteFile() {
            String testClass = "public class SomeClass {\n" +
                "\n" +
                "    public SomeClass() {\n" +
                "        System.out.println(\"Hello World!!\"); ;\n" +
                "    }\n" +
                "}";
            String everything = "";
            
            try {
                    File file = new File("myTestClass.java");
                    // if file doesnt exists, then create it
                    if (!file.exists()) {
                      file.createNewFile();
                    }

                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(testClass);
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
            }
            
            try{
                BufferedReader br = new BufferedReader(new FileReader("myTestClass.java"));
                try {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    everything = sb.toString();
                } finally {
                    br.close();
                }            
            } catch (IOException ex){
                System.out.println("Some errors occured: " + ex.getMessage());
            }
        }

        public void setValue(int val) {
            value = val;
        }

        public UnsafeClass createInstance() {
            return new UnsafeClass();
        }

        public void callChildMethod() throws Exception {
            new Child().foo();
        }

        public void invokeConstructor(Class clazz) throws IllegalAccessException, InstantiationException {
            clazz.newInstance();
            System.out.println("new instance created");
        }

    }

    static class Child extends UnsafeClass {
        public Child() {
            super();
        }

        public void foo() throws Exception {
            System.out.println("method foo invoked ");
        }

        public void openSocket() throws Exception {
            new ServerSocket(1500);
        }
    }
}