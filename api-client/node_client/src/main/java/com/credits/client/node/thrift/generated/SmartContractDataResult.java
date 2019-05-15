/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.credits.client.node.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-05-15")
public class SmartContractDataResult implements org.apache.thrift.TBase<SmartContractDataResult, SmartContractDataResult._Fields>, java.io.Serializable, Cloneable, Comparable<SmartContractDataResult> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SmartContractDataResult");

  private static final org.apache.thrift.protocol.TField STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("status", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField METHODS_FIELD_DESC = new org.apache.thrift.protocol.TField("methods", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField VARIABLES_FIELD_DESC = new org.apache.thrift.protocol.TField("variables", org.apache.thrift.protocol.TType.MAP, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new SmartContractDataResultStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new SmartContractDataResultTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable com.credits.general.thrift.generated.APIResponse status; // required
  public @org.apache.thrift.annotation.Nullable java.util.List<SmartContractMethod> methods; // required
  public @org.apache.thrift.annotation.Nullable java.util.Map<java.lang.String,com.credits.general.thrift.generated.Variant> variables; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    STATUS((short)1, "status"),
    METHODS((short)2, "methods"),
    VARIABLES((short)3, "variables");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // STATUS
          return STATUS;
        case 2: // METHODS
          return METHODS;
        case 3: // VARIABLES
          return VARIABLES;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.STATUS, new org.apache.thrift.meta_data.FieldMetaData("status", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.credits.general.thrift.generated.APIResponse.class)));
    tmpMap.put(_Fields.METHODS, new org.apache.thrift.meta_data.FieldMetaData("methods", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, SmartContractMethod.class))));
    tmpMap.put(_Fields.VARIABLES, new org.apache.thrift.meta_data.FieldMetaData("variables", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.credits.general.thrift.generated.Variant.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SmartContractDataResult.class, metaDataMap);
  }

  public SmartContractDataResult() {
  }

  public SmartContractDataResult(
    com.credits.general.thrift.generated.APIResponse status,
    java.util.List<SmartContractMethod> methods,
    java.util.Map<java.lang.String,com.credits.general.thrift.generated.Variant> variables)
  {
    this();
    this.status = status;
    this.methods = methods;
    this.variables = variables;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SmartContractDataResult(SmartContractDataResult other) {
    if (other.isSetStatus()) {
      this.status = new com.credits.general.thrift.generated.APIResponse(other.status);
    }
    if (other.isSetMethods()) {
      java.util.List<SmartContractMethod> __this__methods = new java.util.ArrayList<SmartContractMethod>(other.methods.size());
      for (SmartContractMethod other_element : other.methods) {
        __this__methods.add(new SmartContractMethod(other_element));
      }
      this.methods = __this__methods;
    }
    if (other.isSetVariables()) {
      java.util.Map<java.lang.String,com.credits.general.thrift.generated.Variant> __this__variables = new java.util.HashMap<java.lang.String,com.credits.general.thrift.generated.Variant>(other.variables.size());
      for (java.util.Map.Entry<java.lang.String, com.credits.general.thrift.generated.Variant> other_element : other.variables.entrySet()) {

        java.lang.String other_element_key = other_element.getKey();
        com.credits.general.thrift.generated.Variant other_element_value = other_element.getValue();

        java.lang.String __this__variables_copy_key = other_element_key;

        com.credits.general.thrift.generated.Variant __this__variables_copy_value = new com.credits.general.thrift.generated.Variant(other_element_value);

        __this__variables.put(__this__variables_copy_key, __this__variables_copy_value);
      }
      this.variables = __this__variables;
    }
  }

  public SmartContractDataResult deepCopy() {
    return new SmartContractDataResult(this);
  }

  @Override
  public void clear() {
    this.status = null;
    this.methods = null;
    this.variables = null;
  }

  @org.apache.thrift.annotation.Nullable
  public com.credits.general.thrift.generated.APIResponse getStatus() {
    return this.status;
  }

  public SmartContractDataResult setStatus(@org.apache.thrift.annotation.Nullable com.credits.general.thrift.generated.APIResponse status) {
    this.status = status;
    return this;
  }

  public void unsetStatus() {
    this.status = null;
  }

  /** Returns true if field status is set (has been assigned a value) and false otherwise */
  public boolean isSetStatus() {
    return this.status != null;
  }

  public void setStatusIsSet(boolean value) {
    if (!value) {
      this.status = null;
    }
  }

  public int getMethodsSize() {
    return (this.methods == null) ? 0 : this.methods.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<SmartContractMethod> getMethodsIterator() {
    return (this.methods == null) ? null : this.methods.iterator();
  }

  public void addToMethods(SmartContractMethod elem) {
    if (this.methods == null) {
      this.methods = new java.util.ArrayList<SmartContractMethod>();
    }
    this.methods.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<SmartContractMethod> getMethods() {
    return this.methods;
  }

  public SmartContractDataResult setMethods(@org.apache.thrift.annotation.Nullable java.util.List<SmartContractMethod> methods) {
    this.methods = methods;
    return this;
  }

  public void unsetMethods() {
    this.methods = null;
  }

  /** Returns true if field methods is set (has been assigned a value) and false otherwise */
  public boolean isSetMethods() {
    return this.methods != null;
  }

  public void setMethodsIsSet(boolean value) {
    if (!value) {
      this.methods = null;
    }
  }

  public int getVariablesSize() {
    return (this.variables == null) ? 0 : this.variables.size();
  }

  public void putToVariables(java.lang.String key, com.credits.general.thrift.generated.Variant val) {
    if (this.variables == null) {
      this.variables = new java.util.HashMap<java.lang.String,com.credits.general.thrift.generated.Variant>();
    }
    this.variables.put(key, val);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Map<java.lang.String,com.credits.general.thrift.generated.Variant> getVariables() {
    return this.variables;
  }

  public SmartContractDataResult setVariables(@org.apache.thrift.annotation.Nullable java.util.Map<java.lang.String,com.credits.general.thrift.generated.Variant> variables) {
    this.variables = variables;
    return this;
  }

  public void unsetVariables() {
    this.variables = null;
  }

  /** Returns true if field variables is set (has been assigned a value) and false otherwise */
  public boolean isSetVariables() {
    return this.variables != null;
  }

  public void setVariablesIsSet(boolean value) {
    if (!value) {
      this.variables = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((com.credits.general.thrift.generated.APIResponse)value);
      }
      break;

    case METHODS:
      if (value == null) {
        unsetMethods();
      } else {
        setMethods((java.util.List<SmartContractMethod>)value);
      }
      break;

    case VARIABLES:
      if (value == null) {
        unsetVariables();
      } else {
        setVariables((java.util.Map<java.lang.String,com.credits.general.thrift.generated.Variant>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case STATUS:
      return getStatus();

    case METHODS:
      return getMethods();

    case VARIABLES:
      return getVariables();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case STATUS:
      return isSetStatus();
    case METHODS:
      return isSetMethods();
    case VARIABLES:
      return isSetVariables();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof SmartContractDataResult)
      return this.equals((SmartContractDataResult)that);
    return false;
  }

  public boolean equals(SmartContractDataResult that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_status = true && this.isSetStatus();
    boolean that_present_status = true && that.isSetStatus();
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (!this.status.equals(that.status))
        return false;
    }

    boolean this_present_methods = true && this.isSetMethods();
    boolean that_present_methods = true && that.isSetMethods();
    if (this_present_methods || that_present_methods) {
      if (!(this_present_methods && that_present_methods))
        return false;
      if (!this.methods.equals(that.methods))
        return false;
    }

    boolean this_present_variables = true && this.isSetVariables();
    boolean that_present_variables = true && that.isSetVariables();
    if (this_present_variables || that_present_variables) {
      if (!(this_present_variables && that_present_variables))
        return false;
      if (!this.variables.equals(that.variables))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetStatus()) ? 131071 : 524287);
    if (isSetStatus())
      hashCode = hashCode * 8191 + status.hashCode();

    hashCode = hashCode * 8191 + ((isSetMethods()) ? 131071 : 524287);
    if (isSetMethods())
      hashCode = hashCode * 8191 + methods.hashCode();

    hashCode = hashCode * 8191 + ((isSetVariables()) ? 131071 : 524287);
    if (isSetVariables())
      hashCode = hashCode * 8191 + variables.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(SmartContractDataResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetStatus()).compareTo(other.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.status, other.status);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetMethods()).compareTo(other.isSetMethods());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMethods()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.methods, other.methods);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetVariables()).compareTo(other.isSetVariables());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetVariables()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.variables, other.variables);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("SmartContractDataResult(");
    boolean first = true;

    sb.append("status:");
    if (this.status == null) {
      sb.append("null");
    } else {
      sb.append(this.status);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("methods:");
    if (this.methods == null) {
      sb.append("null");
    } else {
      sb.append(this.methods);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("variables:");
    if (this.variables == null) {
      sb.append("null");
    } else {
      sb.append(this.variables);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (status != null) {
      status.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SmartContractDataResultStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SmartContractDataResultStandardScheme getScheme() {
      return new SmartContractDataResultStandardScheme();
    }
  }

  private static class SmartContractDataResultStandardScheme extends org.apache.thrift.scheme.StandardScheme<SmartContractDataResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SmartContractDataResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.status = new com.credits.general.thrift.generated.APIResponse();
              struct.status.read(iprot);
              struct.setStatusIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // METHODS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list124 = iprot.readListBegin();
                struct.methods = new java.util.ArrayList<SmartContractMethod>(_list124.size);
                @org.apache.thrift.annotation.Nullable SmartContractMethod _elem125;
                for (int _i126 = 0; _i126 < _list124.size; ++_i126)
                {
                  _elem125 = new SmartContractMethod();
                  _elem125.read(iprot);
                  struct.methods.add(_elem125);
                }
                iprot.readListEnd();
              }
              struct.setMethodsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // VARIABLES
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map127 = iprot.readMapBegin();
                struct.variables = new java.util.HashMap<java.lang.String,com.credits.general.thrift.generated.Variant>(2*_map127.size);
                @org.apache.thrift.annotation.Nullable java.lang.String _key128;
                @org.apache.thrift.annotation.Nullable com.credits.general.thrift.generated.Variant _val129;
                for (int _i130 = 0; _i130 < _map127.size; ++_i130)
                {
                  _key128 = iprot.readString();
                  _val129 = new com.credits.general.thrift.generated.Variant();
                  _val129.read(iprot);
                  struct.variables.put(_key128, _val129);
                }
                iprot.readMapEnd();
              }
              struct.setVariablesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, SmartContractDataResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.status != null) {
        oprot.writeFieldBegin(STATUS_FIELD_DESC);
        struct.status.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.methods != null) {
        oprot.writeFieldBegin(METHODS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.methods.size()));
          for (SmartContractMethod _iter131 : struct.methods)
          {
            _iter131.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.variables != null) {
        oprot.writeFieldBegin(VARIABLES_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, struct.variables.size()));
          for (java.util.Map.Entry<java.lang.String, com.credits.general.thrift.generated.Variant> _iter132 : struct.variables.entrySet())
          {
            oprot.writeString(_iter132.getKey());
            _iter132.getValue().write(oprot);
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SmartContractDataResultTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SmartContractDataResultTupleScheme getScheme() {
      return new SmartContractDataResultTupleScheme();
    }
  }

  private static class SmartContractDataResultTupleScheme extends org.apache.thrift.scheme.TupleScheme<SmartContractDataResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SmartContractDataResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetStatus()) {
        optionals.set(0);
      }
      if (struct.isSetMethods()) {
        optionals.set(1);
      }
      if (struct.isSetVariables()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetStatus()) {
        struct.status.write(oprot);
      }
      if (struct.isSetMethods()) {
        {
          oprot.writeI32(struct.methods.size());
          for (SmartContractMethod _iter133 : struct.methods)
          {
            _iter133.write(oprot);
          }
        }
      }
      if (struct.isSetVariables()) {
        {
          oprot.writeI32(struct.variables.size());
          for (java.util.Map.Entry<java.lang.String, com.credits.general.thrift.generated.Variant> _iter134 : struct.variables.entrySet())
          {
            oprot.writeString(_iter134.getKey());
            _iter134.getValue().write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SmartContractDataResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.status = new com.credits.general.thrift.generated.APIResponse();
        struct.status.read(iprot);
        struct.setStatusIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list135 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.methods = new java.util.ArrayList<SmartContractMethod>(_list135.size);
          @org.apache.thrift.annotation.Nullable SmartContractMethod _elem136;
          for (int _i137 = 0; _i137 < _list135.size; ++_i137)
          {
            _elem136 = new SmartContractMethod();
            _elem136.read(iprot);
            struct.methods.add(_elem136);
          }
        }
        struct.setMethodsIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TMap _map138 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.variables = new java.util.HashMap<java.lang.String,com.credits.general.thrift.generated.Variant>(2*_map138.size);
          @org.apache.thrift.annotation.Nullable java.lang.String _key139;
          @org.apache.thrift.annotation.Nullable com.credits.general.thrift.generated.Variant _val140;
          for (int _i141 = 0; _i141 < _map138.size; ++_i141)
          {
            _key139 = iprot.readString();
            _val140 = new com.credits.general.thrift.generated.Variant();
            _val140.read(iprot);
            struct.variables.put(_key139, _val140);
          }
        }
        struct.setVariablesIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

