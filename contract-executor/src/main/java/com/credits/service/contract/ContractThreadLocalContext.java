package com.credits.service.contract;

import java.util.ArrayList;
import java.util.List;

public class ContractThreadLocalContext {
    private static final ThreadLocal<List<DiffBalancesCollector>> diffBalanceCollector = ThreadLocal.withInitial(ArrayList::new);

    public static void addDiffBalanceCollector(DiffBalancesCollector balancesCollector){
        getDiffBalancesCollectorList().add(balancesCollector);
    }

    public static List<DiffBalancesCollector> getDiffBalancesCollectorList() {
        return diffBalanceCollector.get();
    }
}
