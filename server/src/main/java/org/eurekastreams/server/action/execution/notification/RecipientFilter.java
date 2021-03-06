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
package org.eurekastreams.server.action.execution.notification;

import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Strategy for filtering recipients of an individual notification.
 */
public interface RecipientFilter
{
    /**
     * Determines whether a given recipient should not receive a given notification via a given notifier (transport).
     *
     * @param recipient
     *            The potential recipient.
     * @param notification
     *            The notification.
     * @param notifierType
     *            The notifier type (transport method, e.g. email).
     * @return If recipient should be removed from the list.
     */
    boolean shouldFilter(PersonModelView recipient, NotificationDTO notification, String notifierType);
}
