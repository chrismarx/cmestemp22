/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.service.actions.strategies.PersonCreator;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;

/**
 * Factory to produce a ServiceAction that will create a Person record.
 */
public class CreatePersonActionFactory
{
    /**
     * TabMapper.
     */
    private TabMapper tabMapper;

    /**
     * OrganizationMapper.
     */
    private OrganizationMapper organizationMapper;

    /**
     * Mapper to get the readonly streams.
     */
    private DomainMapper<Long, List<Stream>> readonlyStreamsMapper;

    /**
     * List of the names of readonly streams to add to a person, in order.
     */
    private List<String> readOnlyStreamsNameList;

    /**
     * List of Strings describing the start pages to be added to a new user.
     */
    private final List<String> startPageTabs;
    
    /**
     * Constructor.
     *
     * @param inTabMapper
     *            TabMapper.
     * @param inOrganizationMapper
     *            OrganizationMapper.
     * @param inReadonlyStreamsMapper
     *            mapper to get back all readonly streams
     * @param inReadOnlyStreamsNameList
     *            list of stream names to add to the new user, in order
     * @param inStartPageTabs
     *            list of tabs to be added to the start page.
     */
    public CreatePersonActionFactory(final TabMapper inTabMapper, final OrganizationMapper inOrganizationMapper,
            final DomainMapper<Long, List<Stream>> inReadonlyStreamsMapper, //
            final List<String> inReadOnlyStreamsNameList,
            final List<String> inStartPageTabs)
    {
        tabMapper = inTabMapper;
        organizationMapper = inOrganizationMapper;
        readonlyStreamsMapper = inReadonlyStreamsMapper;
        readOnlyStreamsNameList = inReadOnlyStreamsNameList;
        startPageTabs = inStartPageTabs;
    }

    /**
     * Get a PersistResourceAction set up for Person.
     *
     * @param inPersonMapper
     *            the person mapper to be injected into the action
     * @param inUpdater
     *            Updater
     * @return the newly built action
     */
    public PersistResourceExecution<Person> getCreatePersonAction(final PersonMapper inPersonMapper,
            final UpdaterStrategy inUpdater)
    {
        return new PersistResourceExecution<Person>(inPersonMapper, this, inUpdater, new PersonCreator(inPersonMapper,
                tabMapper, organizationMapper, readonlyStreamsMapper, readOnlyStreamsNameList, startPageTabs));
    }
}
