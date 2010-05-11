/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.holiday.business.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class HolidayBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public HolidayBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetHolidays() throws Exception {
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", new Date(), null, RepaymentRuleTypes.fromInt(1));
        holidayDetails.disableValidation(true);
        List<Short> officeIds = new LinkedList<Short>();
        officeIds.add((short)1);
        new HolidayServiceFacadeWebTier(new OfficePersistence()).createHoliday(holidayDetails, officeIds);

        List<HolidayBO> holidays = new HolidayBusinessService().getHolidays(Calendar.getInstance().get(Calendar.YEAR));
        Assert.assertNotNull(holidays);
        Assert.assertEquals(1, holidays.size());
    }

    public void testGetRepaymentRuleTypes() throws Exception {
        List<RepaymentRuleTypes> repaymentRules = Arrays.asList(RepaymentRuleTypes.values());
        Assert.assertNotNull(repaymentRules);
        Assert.assertEquals(4, repaymentRules.size());
    }

    public void testGetDistinctYears() throws Exception {
        List<HolidayBO> distinctYears = new HolidayBusinessService().getDistinctYears();
        Assert.assertNotNull(distinctYears);
    }

}
