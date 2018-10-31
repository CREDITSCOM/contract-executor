package com.credits.client.executor.pojo;

import java.util.List;

/**
 * Created by Igor Goryunov on 08.10.2018
 */
public class MethodDescriptionData {
    public String name;
    public List<String> argTypes;
    public String returnType;

    public MethodDescriptionData(){}

    public MethodDescriptionData(String name, List<String> argTypes, String returnType) {
        this.name = name;
        this.argTypes = argTypes;
        this.returnType = returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MethodDescriptionData that = (MethodDescriptionData) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (argTypes != null ? !argTypes.equals(that.argTypes) : that.argTypes != null) {
            return false;
        }
        return returnType != null ? returnType.equals(that.returnType) : that.returnType == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (argTypes != null ? argTypes.hashCode() : 0);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodDescriptionData{" + "name='" + name + '\'' + ", argTypes=" + argTypes + ", returnType='" + returnType + '\'' + '}';
    }

}
