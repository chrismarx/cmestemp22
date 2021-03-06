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
package org.eurekastreams.server.persistence.mappers.opensocial;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Delete an oauth domain entry.
 */
public class DeleteOAuthEntry extends BaseArgDomainMapper<String, Boolean>
{
    /**
     * Deletes a oauth entry.
     * 
     * @param inRequest
     *            The token pointing to the oauth entry to delete.
     * @return true.
     */
    @Override
    public Boolean execute(final String inRequest)
    {
        Query q = getEntityManager().createQuery("DELETE from OAuthDomainEntry e where e.token = :token").setParameter(
                "token", inRequest);
        q.executeUpdate();

        return Boolean.TRUE;
    }
}
