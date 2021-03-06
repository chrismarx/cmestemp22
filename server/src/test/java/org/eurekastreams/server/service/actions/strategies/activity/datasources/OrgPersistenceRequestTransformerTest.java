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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests transforming an org request.
 */
public class OrgPersistenceRequestTransformerTest
{
    /**
     * System under test.
     */
    private static OrgPersistenceRequestTransformer sut;

    /**
     * Setup test fixtures.
     */
    @BeforeClass
    public static void setup()
    {
        sut = new OrgPersistenceRequestTransformer();
    }

    /**
     * Tests executing.
     */
    @Test
    public void testTransform()
    {
        final String orgShortName = "orgShortName";

        final JSONObject request = new JSONObject();
        request.accumulate("organization", orgShortName);

        Assert.assertEquals(orgShortName, (String) sut.transform(request, 1L));
    }
}
