/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.credits.client.executor.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-05-15")
public class MethodHeader implements org.apache.thrift.TBase<MethodHeader, MethodHeader._Fields>, java.io.Serializable, Cloneable, Comparable<MethodHeader> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MethodHeader");

  private static final org.apache.thrift.protocol.TField METHOD_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("methodName", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField PARAMS_FIELD_DESC = new org.apache.thrift.protocol.TField("params", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new MethodHeaderStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new MethodHeaderTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.lang.String methodName; // required
  public @org.apache.thrift.annotation.Nullable java.util.List<com.credits.general.thrift.generated.Variant> params; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    METHOD_NAME((short)1, "methodName"),
    PARAMS((short)2, "params");

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
        case 1: // METHOD_NAME
          return METHOD_NAME;
        case 2: // PARAMS
          return PARAMS;
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
    tmpMap.put(_Fields.METHOD_NAME, new org.apache.thrift.meta_data.FieldMetaData("methodName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PARAMS, new org.apache.thrift.meta_data.FieldMetaData("params", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.credits.general.thrift.generated.Variant.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MethodHeader.class, metaDataMap);
  }

  public MethodHeader() {
  }

  public MethodHeader(
    java.lang.String methodName,
    java.util.List<com.credits.general.thrift.generated.Variant> params)
  {
    this();
    this.methodName = methodName;
    this.params = params;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MethodHeader(MethodHeader other) {
    if (other.isSetMethodName()) {
      this.methodName = other.methodName;
    }
    if (other.isSetParams()) {
      java.util.List<com.credits.general.thrift.generated.Variant> __this__params = new java.util.ArrayList<com.credits.general.thrift.generated.Variant>(other.params.size());
      for (com.credits.general.thrift.generated.Variant other_element : other.params) {
        __this__params.add(new com.credits.general.thrift.generated.Variant(other_element));
      }
      this.params = __this__params;
    }
  }

  public MethodHeader deepCopy() {
    return new MethodHeader(this);
  }

  @Override
  public void clear() {
    this.methodName = null;
    this.params = null;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getMethodName() {
    return this.methodName;
  }

  public MethodHeader setMethodName(@org.apache.thrift.annotation.Nullable java.lang.String methodName) {
    this.methodName = methodName;
    return this;
  }

  public void unsetMethodName() {
    this.methodName = null;
  }

  /** Returns true if field methodName is set (has been assigned a value) and false otherwise */
  public boolean isSetMethodName() {
    return this.methodName != null;
  }

  public void setMethodNameIsSet(boolean value) {
    if (!value) {
      this.methodName = null;
    }
  }

  public int getParamsSize() {
    return (this.params == null) ? 0 : this.params.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<com.credits.general.thrift.generated.Variant> getParamsIterator() {
    return (this.params == null) ? null : this.params.iterator();
  }

  public void addToParams(com.credits.general.thrift.generated.Variant elem) {
    if (this.params == null) {
      this.params = new java.util.ArrayList<com.credits.general.thrift.generated.Variant>();
    }
    this.params.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<com.credits.general.thrift.generated.Variant> getParams() {
    return this.params;
  }

  public MethodHeader setParams(@org.apache.thrift.annotation.Nullable java.util.List<com.credits.general.thrift.generated.Variant> params) {
    this.params = params;
    return this;
  }

  public void unsetParams() {
    this.params = null;
  }

  /** Returns true if field params is set (has been assigned a value) and false otherwise */
  public boolean isSetParams() {
    return this.params != null;
  }

  public void setParamsIsSet(boolean value) {
    if (!value) {
      this.params = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case METHOD_NAME:
      if (value == null) {
        unsetMethodName();
      } else {
        setMethodName((java.lang.String)value);
      }
      break;

    case PARAMS:
      if (value == null) {
        unsetParams();
      } else {
        setParams((java.util.List<com.credits.general.thrift.generated.Variant>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case METHOD_NAME:
      return getMethodName();

    case PARAMS:
      return getParams();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case METHOD_NAME:
      return isSetMethodName();
    case PARAMS:
      return isSetParams();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof MethodHeader)
      return this.equals((MethodHeader)that);
    return false;
  }

  public boolean equals(MethodHeader that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_methodName = true && this.isSetMethodName();
    boolean that_present_methodName = true && that.isSetMethodName();
    if (this_present_methodName || that_present_methodName) {
      if (!(this_present_methodName && that_present_methodName))
        return false;
      if (!this.methodName.equals(that.methodName))
        return false;
    }

    boolean this_present_params = true && this.isSetParams();
    boolean that_present_params = true && that.isSetParams();
    if (this_present_params || that_present_params) {
      if (!(this_present_params && that_present_params))
        return false;
      if (!this.params.equals(that.params))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetMethodName()) ? 131071 : 524287);
    if (isSetMethodName())
      hashCode = hashCode * 8191 + methodName.hashCode();

    hashCode = hashCode * 8191 + ((isSetParams()) ? 131071 : 524287);
    if (isSetParams())
      hashCode = hashCode * 8191 + params.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(MethodHeader other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetMethodName()).compareTo(other.isSetMethodName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMethodName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.methodName, other.methodName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetParams()).compareTo(other.isSetParams());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetParams()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.params, other.params);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("MethodHeader(");
    boolean first = true;

    sb.append("methodName:");
    if (this.methodName == null) {
      sb.append("null");
    } else {
      sb.append(this.methodName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("params:");
    if (this.params == null) {
      sb.append("null");
    } else {
      sb.append(this.params);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
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

  private static class MethodHeaderStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MethodHeaderStandardScheme getScheme() {
      return new MethodHeaderStandardScheme();
    }
  }

  private static class MethodHeaderStandardScheme extends org.apache.thrift.scheme.StandardScheme<MethodHeader> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MethodHeader struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // METHOD_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.methodName = iprot.readString();
              struct.setMethodNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PARAMS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list52 = iprot.readListBegin();
                struct.params = new java.util.ArrayList<com.credits.general.thrift.generated.Variant>(_list52.size);
                @org.apache.thrift.annotation.Nullable com.credits.general.thrift.generated.Variant _elem53;
                for (int _i54 = 0; _i54 < _list52.size; ++_i54)
                {
                  _elem53 = new com.credits.general.thrift.generated.Variant();
                  _elem53.read(iprot);
                  struct.params.add(_elem53);
                }
                iprot.readListEnd();
              }
              struct.setParamsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, MethodHeader struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.methodName != null) {
        oprot.writeFieldBegin(METHOD_NAME_FIELD_DESC);
        oprot.writeString(struct.methodName);
        oprot.writeFieldEnd();
      }
      if (struct.params != null) {
        oprot.writeFieldBegin(PARAMS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.params.size()));
          for (com.credits.general.thrift.generated.Variant _iter55 : struct.params)
          {
            _iter55.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MethodHeaderTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MethodHeaderTupleScheme getScheme() {
      return new MethodHeaderTupleScheme();
    }
  }

  private static class MethodHeaderTupleScheme extends org.apache.thrift.scheme.TupleScheme<MethodHeader> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MethodHeader struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetMethodName()) {
        optionals.set(0);
      }
      if (struct.isSetParams()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetMethodName()) {
        oprot.writeString(struct.methodName);
      }
      if (struct.isSetParams()) {
        {
          oprot.writeI32(struct.params.size());
          for (com.credits.general.thrift.generated.Variant _iter56 : struct.params)
          {
            _iter56.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MethodHeader struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.methodName = iprot.readString();
        struct.setMethodNameIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list57 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.params = new java.util.ArrayList<com.credits.general.thrift.generated.Variant>(_list57.size);
          @org.apache.thrift.annotation.Nullable com.credits.general.thrift.generated.Variant _elem58;
          for (int _i59 = 0; _i59 < _list57.size; ++_i59)
          {
            _elem58 = new com.credits.general.thrift.generated.Variant();
            _elem58.read(iprot);
            struct.params.add(_elem58);
          }
        }
        struct.setParamsIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

