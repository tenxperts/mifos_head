package org.mifos.accounts.fees.servicefacade;

import java.util.List;

import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public interface FeeService {

    public FeeEntity createOneTimeFee(UserContext userContext, String feeName, boolean isCustomerDefaultFee, boolean isRateFee,
            Double rate, Money feeMoney, FeeCategory categoryType, FeeFormula feeFormula,
            GLCodeEntity glCode, OfficeBO office, FeePayment feePaymentType) throws PersistenceException, FeeException;

    public FeeEntity createPeriodicFee(UserContext userContext, String feeName, boolean isCustomerDefaultFee, boolean isRateFee,
            Double rate, Money feeMoney, FeeCategory categoryType, FeeFormula feeFormula,
            GLCodeEntity glCode, OfficeBO office, RecurrenceType feeRecurrenceType, Short recurAfter) throws PersistenceException, FeeException;

    public List<FeeEntity> retrieveCustomerFees();
    public List<FeeEntity> retrieveProductFees();
    public FeeEntity findFeeById(Short feeId);
}
