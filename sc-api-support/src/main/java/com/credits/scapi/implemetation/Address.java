package com.credits.scapi.implemetation;

import com.credits.scapi.v2.WalletAddress;

import java.util.Objects;

public class Address implements WalletAddress {
    private static final long serialVersionUID = 6564131192814112130L;
    private final String base58Address;

    public Address(String base58Address) {
        this.base58Address = base58Address;
    }

    public static Address addressOf(String base58Address) {
        return new Address(base58Address);
    }

    @Override
    public String getBase58Address() {
        return base58Address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(base58Address, address.base58Address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base58Address);
    }
}
