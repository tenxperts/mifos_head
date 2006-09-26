package org.mifos.application.customer.struts.action;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerAction extends MifosMockStrutsTestCase {

	private UserContext userContext;

	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	private AccountBO account;

	private LoanBO groupAccount;

	private LoanBO clientAccount;

	private SavingsBO centerSavingsAccount;

	private SavingsBO groupSavingsAccount;

	private SavingsBO clientSavingsAccount;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/customer/struts-config.xml")
					.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(centerSavingsAccount);
		TestObjectFactory.cleanUp(groupSavingsAccount);
		TestObjectFactory.cleanUp(clientSavingsAccount);
		TestObjectFactory.cleanUp(groupAccount);
		TestObjectFactory.cleanUp(clientAccount);
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testForwardWaiveChargeDue() {
		createInitialObjects();
		setRequestPathInfo("/customerAction.do");
		addRequestParameter("method", "waiveChargeDue");
		addRequestParameter("type", "Client");
		AccountBO accountBO = client.getCustomerAccount();
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("waiveChargesDue_Success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testForwardWaiveChargeOverDue() {
		createInitialObjects();
		setRequestPathInfo("/customerAction.do");
		addRequestParameter("method", "waiveChargeOverDue");
		addRequestParameter("type", "Client");
		AccountBO accountBO = client.getCustomerAccount();
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("waiveChargesOverDue_Success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testGetAllActivity() {
		createInitialObjects();
		setRequestPathInfo("/customerAction.do");
		addRequestParameter("method", "getAllActivity");
		addRequestParameter("type", "Client");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("viewClientActivity");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testGetAllClosedAccounts() throws Exception {
		getCustomer();
		groupAccount.changeStatus(AccountState.LOANACC_CANCEL.getValue(),
				AccountStateFlag.LOAN_WITHDRAW.getValue(),
				"WITHDRAW LOAN ACCOUNT");
		clientAccount.changeStatus(AccountState.LOANACC_CANCEL.getValue(),
				AccountStateFlag.LOAN_WITHDRAW.getValue(),
				"WITHDRAW LOAN ACCOUNT");
		clientSavingsAccount.changeStatus(AccountState.SAVINGS_ACC_CANCEL
				.getValue(), AccountStateFlag.SAVINGS_REJECTED.getValue(),
				"WITHDRAW LOAN ACCOUNT");
		TestObjectFactory.updateObject(groupAccount);
		TestObjectFactory.updateObject(clientAccount);
		TestObjectFactory.updateObject(clientSavingsAccount);
		HibernateUtil.commitTransaction();
		setRequestPathInfo("/customerAction.do");
		addRequestParameter("method", "getAllClosedAccounts");
		addRequestParameter("customerId", client.getCustomerId().toString());
		actionPerform();
		verifyForward(ActionForwards.viewAllClosedAccounts.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Size of closed loan accounts should be 1", 1,
				((List<AccountBO>) SessionUtils.getAttribute(
						AccountConstants.CLOSEDLOANACCOUNTSLIST,
						request.getSession())).size());
		assertEquals("Size of closed savings accounts should be 1", 1,
				((List<AccountBO>) SessionUtils.getAttribute(
						AccountConstants.CLOSEDSAVINGSACCOUNTSLIST,
						request.getSession())).size());
	}
	
	public void testGetAllClosedAccountsOfCenter() throws Exception {
		getCustomer();
		centerSavingsAccount.changeStatus(AccountState.SAVINGS_ACC_CANCEL
				.getValue(), AccountStateFlag.SAVINGS_REJECTED.getValue(),
				"WITHDRAW SAVINGS ACCOUNT");
		TestObjectFactory.updateObject(centerSavingsAccount);
		HibernateUtil.commitTransaction();
		setRequestPathInfo("/customerAction.do");
		addRequestParameter("method", "getAllClosedAccounts");
		addRequestParameter("customerId", center.getCustomerId().toString());
		actionPerform();
		verifyForward(ActionForwards.viewAllClosedAccounts.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Size of closed savings accounts should be 1", 1,
				((List<AccountBO>) SessionUtils.getAttribute(
						AccountConstants.CLOSEDSAVINGSACCOUNTSLIST,
						request.getSession())).size());
	}

	private void getCustomer() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering(
				"Loan123", "fasd",Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering(
				"Loanew23232","23fd", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		groupAccount = TestObjectFactory.createLoanAccount("42423142341",
				group, Short.valueOf("5"),
				new Date(System.currentTimeMillis()), loanOffering1);
		clientAccount = TestObjectFactory.createLoanAccount("3243", client,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering2);
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering("SavingPrd123","qwe2", Short.valueOf("2"),
						new Date(System.currentTimeMillis()), Short
								.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		SavingsOfferingBO savingsOffering1 = TestObjectFactory
				.createSavingsOffering("SavingP23rd1","4324", Short.valueOf("2"),
						new Date(System.currentTimeMillis()), Short
								.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		centerSavingsAccount = TestObjectFactory.createSavingsAccount("432434",
				center, Short.valueOf("16"), new Date(System
						.currentTimeMillis()), savingsOffering);
		clientSavingsAccount = TestObjectFactory.createSavingsAccount("432434",
				client, Short.valueOf("16"), new Date(System
						.currentTimeMillis()), savingsOffering1);
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("13"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1", group, new Date(System.currentTimeMillis()));
	}

}
