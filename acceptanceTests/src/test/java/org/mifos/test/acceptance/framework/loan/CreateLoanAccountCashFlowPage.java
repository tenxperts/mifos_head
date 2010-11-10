package org.mifos.test.acceptance.framework.loan;

import com.thoughtworks.selenium.Selenium;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.testng.Assert;

import java.util.Locale;

public class CreateLoanAccountCashFlowPage extends AbstractPage{
    public CreateLoanAccountCashFlowPage(Selenium selenium) {
        super(selenium);
    }

    public CreateLoanAccountCashFlowPage verifyPage() {
        this.verifyPage("");
        return this;
    }

    public CreateLoanAccountCashFlowPage validateCashFlowMonths(DateTime disbursalDate, int installment, int frequencyType) {
        DateTime firstInstallmentDate;
        DateTime lastInstallmentDate;
        if (frequencyType == DefineNewLoanProductPage.SubmitFormParameters.WEEKS) {
            firstInstallmentDate = disbursalDate.plusWeeks(1);
            lastInstallmentDate = disbursalDate.plusWeeks(installment);
        } else {
            firstInstallmentDate = disbursalDate.plusMonths(1);
            lastInstallmentDate = disbursalDate.plusMonths(installment);
        }

        DateTime firstCashFlowDate = firstInstallmentDate.minusMonths(1);
        DateTime lastCashFlowDate = lastInstallmentDate.plusMonths(1);

        DateTime currentIteratorDate = firstCashFlowDate;
        int rowCount = 1;
        while (!currentIteratorDate.isAfter(lastCashFlowDate)) {
            Assert.assertEquals(selenium.getText("//tr[" + rowCount + "]/td[1]"), DateTimeFormat.forPattern("MMMMMMMM yyyy").withLocale(Locale.ENGLISH).print(currentIteratorDate));
            currentIteratorDate = currentIteratorDate.plusMonths(1);
            rowCount++;
        }
        //ToDo last month is not accounted
        return this;
    }

    public CreateLoanAccountCashFlowPage enterValidData(int incremental, int cashFlowBase) {
        int noOfMonths = selenium.getXpathCount("//input[contains(@id,'expense')]").intValue();
        for (int rowIndex = 1; rowIndex <= noOfMonths ; rowIndex++) {
            selenium.type("//tr[" + rowIndex + "]/td[2]/input","100");
            selenium.type("//tr[" + rowIndex + "]/td[3]/input", String.valueOf(cashFlowBase + incremental));
            selenium.type("//tr[" + rowIndex + "]/td[4]/input","notes" + rowIndex);
        }
        return this;
    }

    public ViewInstallmentDetailsPage clickContinue() {
        selenium.click("_eventId_capture");
        waitForPageToLoad();
        return new ViewInstallmentDetailsPage(selenium);
    }
}
