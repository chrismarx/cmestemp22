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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Reusable cache refresher - uses a CacheKeySuffixTransformer to generate the cache key.
 * 
 * @param <Request>
 *            the request that the data was found with
 * @param <Response>
 *            the data found by the previous level
 */
public class SingleValueCacheRefreshStrategy<Request, Response> extends CachedDomainMapper implements
        RefreshStrategy<Request, Response>
{
    /**
     * Cache key suffix.
     */
    private final String cacheKeyPrefix;

    /**
     * Cache key suffix transformer.
     */
    private final Transformer<Request, String> cacheKeySuffixTransformer;

    /**
     * Constructor.
     * 
     * @param inCacheKeyPrefix
     *            cache key prefix
     * @param inCacheKeySuffixTransformer
     *            the cache key suffix transformer
     */
    public SingleValueCacheRefreshStrategy(final String inCacheKeyPrefix,
            final Transformer<Request, String> inCacheKeySuffixTransformer)
    {
        cacheKeyPrefix = inCacheKeyPrefix;
        cacheKeySuffixTransformer = inCacheKeySuffixTransformer;
    }

    @Override
    public void refresh(final Request inRequest, final Response inResponse)
    {
        String cacheKeySuffix = cacheKeySuffixTransformer.transform(inRequest);
        getCache().set(cacheKeyPrefix + cacheKeySuffix, inResponse);
    }
}
