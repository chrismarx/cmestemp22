/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.search.bridge;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests class bridge to get for activity author.
 */
public class ActivityAuthorClassBridgeTest
{
    /**
     * Context for building mock objects.
     */
    private Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private ActivityAuthorClassBridge sut = new ActivityAuthorClassBridge();

    /**
     * Mocked GetDomainGroupsByShortNames.
     */
    private GetDomainGroupsByShortNames getDomainGroupsByShortNamesMock;

    /**
     * Mapper to lookup person ids by account ids.
     */
    private PeopleAccountIdsToIdsMapperFake peopleAccountIdsToIdsMapper;

    /**
     * Account id of the person we're testing for.
     */
    private String testPersonAccountId = "abcdefg";

    /**
     * Short name of the domain group we're testing for.
     */
    private String testDomainGroupShortName = "hijklmno";

    /**
     * ID of the test person.
     */
    private final long testPersonId = 382L;

    /**
     * ID of the test group.
     */
    private final long testDomainGroupId = 8784L;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        peopleAccountIdsToIdsMapper = new PeopleAccountIdsToIdsMapperFake();
        ActivityAuthorClassBridge.setPeopleAccountIdsToIdsMapper(peopleAccountIdsToIdsMapper);

        getDomainGroupsByShortNamesMock = context.mock(GetDomainGroupsByShortNames.class);
        ActivityAuthorClassBridge.setGetDomainGroupsByShortNames(getDomainGroupsByShortNamesMock);
    }

    /**
     * Fixture teardown.
     */
    @AfterClass
    public static void afterClass()
    {
        ActivityAuthorClassBridge.setPeopleAccountIdsToIdsMapper(null);
        ActivityAuthorClassBridge.setGetDomainGroupsByShortNames(null);
    }

    /**
     * Test objectToString for group.
     */
    @Test
    public void testObjectToStringForGroupFromActivityObject()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(getDomainGroupsByShortNamesMock).fetchId(testDomainGroupShortName);
                will(returnValue(testDomainGroupId));
            }
        });

        Activity activity = new Activity();
        activity.setActorType(EntityType.GROUP);
        activity.setActorId(testDomainGroupShortName);
        assertEquals("g" + testDomainGroupId, sut.objectToString(activity));
        context.assertIsSatisfied();
    }

    /**
     * Test objectToString for person.
     */
    @Test
    public void testObjectToStringForPersonFromActivityObject()
    {
        Activity activity = new Activity();
        activity.setActorType(EntityType.PERSON);
        activity.setActorId(testPersonAccountId);

        peopleAccountIdsToIdsMapper.setIdToReturn(testPersonId);
        assertEquals("p" + testPersonId, sut.objectToString(activity));
        assertEquals(testPersonAccountId, peopleAccountIdsToIdsMapper.getAccountIdPassedIn());
    }

    /**
     * Test objectToString for a not set entity type.
     */
    @Test(expected = RuntimeException.class)
    public void testObjectToStringForNotSetFromActivityObject()
    {
        Activity activity = new Activity();
        activity.setActorType(EntityType.NOTSET);
        activity.setActorId(testPersonAccountId);
        assertEquals("p" + testPersonId, sut.objectToString(activity));
        context.assertIsSatisfied();
    }

    /**
     * Test that the appropriate exception is thrown when the bridge doesn't have a person cache.
     */
    @Test(expected = RuntimeException.class)
    public void testObjectToStringWithoutPeopleByAccountIdMapper()
    {
        ActivityAuthorClassBridge.setPeopleAccountIdsToIdsMapper(null);
        sut.objectToString(new Activity());
    }

    /**
     * Test that the appropriate exception is thrown when the bridge doesn't have a domain group cache.
     */
    @Test(expected = RuntimeException.class)
    public void testObjectToStringWithoutDomainGroupCache()
    {
        ActivityAuthorClassBridge.setGetDomainGroupsByShortNames(null);
        sut.objectToString(new Activity());
    }

    /**
     * Test objectToString with an invalid input type.
     */
    @Test(expected = RuntimeException.class)
    public void testObjectToStringForInvalidType()
    {
        sut.objectToString(new PersonModelView());
    }

    /**
     * Fake to help make sure the proper accountid was passed in.
     */
    private class PeopleAccountIdsToIdsMapperFake implements DomainMapper<List<String>, List<Long>>
    {
        /**
         * the account id that was passed into execute is stored here.
         */
        private String accountIdPassedIn;

        /**
         * The id to return from execute.
         */
        private Long idToReturn;

        @Override
        public List<Long> execute(final List<String> inRequest)
        {
            accountIdPassedIn = inRequest.get(0);

            List ret = new ArrayList<Long>();
            ret.add(idToReturn);
            return ret;
        }

        /**
         * @return the accountIdPassedIn
         */
        public String getAccountIdPassedIn()
        {
            return accountIdPassedIn;
        }

        /**
         * @param inAccountIdPassedIn
         *            the accountIdPassedIn to set
         */
        public void setAccountIdPassedIn(final String inAccountIdPassedIn)
        {
            accountIdPassedIn = inAccountIdPassedIn;
        }

        /**
         * @return the idToReturn
         */
        public Long getIdToReturn()
        {
            return idToReturn;
        }

        /**
         * @param inIdToReturn
         *            the idToReturn to set
         */
        public void setIdToReturn(final Long inIdToReturn)
        {
            idToReturn = inIdToReturn;
        }

    }
}
