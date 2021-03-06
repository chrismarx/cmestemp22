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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.BackgroundMapper;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class contains the test suite for the {@link UpdatePersonExecution} class.
 * 
 */
public class UpdatePersonExecutionTest
{
    /**
     * System under test.
     */
    private UpdatePersonExecution sut;

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked instance of the {@link OrganizationMapper}.
     */
    private final OrganizationMapper orgMapperMock = context.mock(OrganizationMapper.class);

    /**
     * Mocked instance of the {@link BackgroundMapper}.
     */
    private final BackgroundMapper backgroundMapperMock = context.mock(BackgroundMapper.class);

    /**
     * Mocked instance of the {@link PersonMapper}.
     */
    private final PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * Mocked instance of the {@link PersistResourceExecution}.
     */
    private final PersistResourceExecution persistResourceExecutionMock = context.mock(PersistResourceExecution.class);

    /**
     * Mocked instance of the {@link Organization}.
     */
    private final Organization orgMock = context.mock(Organization.class);

    /**
     * Mocked instance of the {@link Person}.
     */
    private final Person personMock = context.mock(Person.class);

    /**
     * Mocked isntance of the {@link Principal}.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked instance of the {@link PrincipalActionContext}.
     */
    private final PrincipalActionContext principalActionContextMock = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of the {@link TaskHandlerActionContext}.
     */
    private final TaskHandlerActionContext taskHandlerActionContextMock = context.mock(TaskHandlerActionContext.class);

    /**
     * The organization hierarchy traverser builder.
     */
    private final OrganizationHierarchyTraverserBuilder orgTraverserBuilder = context
            .mock(OrganizationHierarchyTraverserBuilder.class);

    /**
     * Test fields to use in this suite.
     */
    private HashMap<String, Serializable> fields;

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new UpdatePersonExecution(orgMapperMock, personMapperMock, persistResourceExecutionMock,
                backgroundMapperMock, orgTraverserBuilder);
        fields = new HashMap<String, Serializable>();
        fields.put("skills", "stuff, things");
        fields.put(PersonModelView.ORG_PARENT_KEY, "orgShortName");

        ArrayList<String> relatedOrgShortnames = new ArrayList<String>();
        relatedOrgShortnames.add("testshortname");
        fields.put("relatedOrganizations", relatedOrgShortnames);
    }

    /**
     * Perform successful test of execute.
     */
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(fields));

                allowing(orgMapperMock).findByShortName(with(any(String.class)));
                will(returnValue(orgMock));

                oneOf(persistResourceExecutionMock).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(personMock));

                oneOf(personMock).getOpenSocialId();
                will(returnValue("53"));
                oneOf(backgroundMapperMock).findOrCreatePersonBackground("53");

                oneOf(personMock).getParentOrganization();
                will(returnValue(orgMock));

                oneOf(orgMock).getShortName();
                will(returnValue("orgShortName"));

                oneOf(personMock).setRelatedOrganizations(with(any(List.class)));

                oneOf(personMapperMock).flush();
            }
        });

        sut.execute(taskHandlerActionContextMock);
        context.assertIsSatisfied();
    }

    /**
     * Perform successful test of execute with skills.
     */
    @Test
    public void testExecuteWithSkills()
    {
        fields.put("skills", "stuff, things");

        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(fields));

                allowing(orgMapperMock).findByShortName(with(any(String.class)));
                will(returnValue(orgMock));

                oneOf(persistResourceExecutionMock).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(personMock));

                oneOf(personMock).getOpenSocialId();
                will(returnValue("53"));
                oneOf(backgroundMapperMock).findOrCreatePersonBackground("53");

                oneOf(personMock).getParentOrganization();
                will(returnValue(orgMock));

                oneOf(orgMock).getShortName();
                will(returnValue("orgShortName"));

                oneOf(personMock).setRelatedOrganizations(with(any(List.class)));

                oneOf(personMapperMock).flush();
            }
        });

        sut.execute(taskHandlerActionContextMock);
        context.assertIsSatisfied();

    }

    /**
     * Perform successful test of execute with skills.
     */
    @Test
    public void testExecuteWithNoSkills()
    {
        fields.put("skills", "");

        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(fields));

                allowing(orgMapperMock).findByShortName(with(any(String.class)));
                will(returnValue(orgMock));

                oneOf(persistResourceExecutionMock).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(personMock));

                oneOf(personMock).getOpenSocialId();
                will(returnValue("53"));
                oneOf(backgroundMapperMock).findOrCreatePersonBackground("53");

                oneOf(personMock).getParentOrganization();
                will(returnValue(orgMock));

                oneOf(orgMock).getShortName();
                will(returnValue("orgShortName"));

                oneOf(personMock).setRelatedOrganizations(with(any(List.class)));

                oneOf(personMapperMock).flush();
            }
        });

        sut.execute(taskHandlerActionContextMock);
        context.assertIsSatisfied();
    }

    /**
     * Perform failure test of execute.
     */
    @Test(expected = ValidationException.class)
    public void testExecuteFailure()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(fields));

                allowing(orgMapperMock).findByShortName(with(any(String.class)));
                will(returnValue(null));

                oneOf(persistResourceExecutionMock).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(personMock));

                oneOf(personMock).getOpenSocialId();
                will(returnValue("53"));
                oneOf(backgroundMapperMock).findOrCreatePersonBackground("53");

                oneOf(personMock).getParentOrganization();
                will(returnValue(orgMock));

                oneOf(orgMock).getShortName();
                will(returnValue("newOrgShortName"));
            }
        });

        sut.execute(taskHandlerActionContextMock);
        context.assertIsSatisfied();
    }
}
