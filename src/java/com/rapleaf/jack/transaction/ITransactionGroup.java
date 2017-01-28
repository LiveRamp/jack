package com.rapleaf.jack.transaction;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.TransactionCreationFailureException;

public interface ITransactionGroup<T extends ITransaction<? extends IDb>> {

  T createTransaction() throws TransactionCreationFailureException;

}
