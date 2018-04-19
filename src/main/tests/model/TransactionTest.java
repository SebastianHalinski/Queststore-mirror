package model;

import org.junit.Test;
import java.time.LocalDate;
import static junit.framework.Assert.assertEquals;


public class TransactionTest {

    @Test
    public void constructor_createsProperTransactionObject() {
        Transaction transaction = new Transaction(1, 1, 10);
        LocalDate date  = LocalDate.now();
        String expected = String.format("\tDate: %tF, Student: 1, Item: 1, Price: 10",date);
        assertEquals(expected, transaction.toString());
    }

    @Test
    public void constructor_transactionPriceIsSetToZeroWhenNegativeNumberPassed() {
        Transaction transaction = new Transaction(1,1, -100);
        int expected = 0;
        assertEquals(expected, transaction.getItemPrice());
    }
}