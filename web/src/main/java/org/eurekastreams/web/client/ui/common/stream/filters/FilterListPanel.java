/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.filters;

import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.IndexedDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Shows a list of views.
 *
 */
public class FilterListPanel extends FlowPanel
{
    /**
     * The default drag sensitivity.
     */
    private static final int DRAG_SENSITIVITY = 10;
    /**
     * The tab boundary panel. Absolute panel is needed because gwt-dnd only accepts them.
     */
    private final AbsolutePanel viewBoundaryPanel = new AbsolutePanel();

    /**
     * The active item.
     */
    private FilterPanel activeItem = null;

    /**
     * The cache of the list, to look stuff up by ID on.
     */
    private final HashMap<Long, FilterPanel> listCache = new HashMap<Long, FilterPanel>();

    /**
     * Whether or not the list panel is hidden.
     */
    private boolean hidden = true;

    /**
     * The show more show less container.
     */
    private final FlowPanel showMore = new FlowPanel();

    /**
     * Show more show less link.
     */
    private final Anchor showMoreLink = new Anchor("show more");

    /**
     * The panel to hold the views.
     */
    private final VerticalPanel panel = new VerticalPanel();

    /**
     * View drag controller.
     */
    private final PickupDragController viewDragController;

    /**
     * The renderer.
     */
    private final FilterRenderStrategy renderer;

    /**
     * Whether or not weve added the hidden line.
     */
    private boolean addedHiddenLine = false;

    /**
     * Hidden line index.
     */
    private final Integer hiddenLineIndex;

    /**
     * The views.
     */
    private final List<StreamFilter> views;

    /**
     * Default constructor.
     *
     * @param inViews
     *            the views.
     * @param inHiddenLineIndex
     *            the hidden line index.
     * @param inRenderer
     *            the renderer.
     * @param readOnly
     *            flag indicating if this panel has no writable properties requiring a DialogContent.
     */
    public FilterListPanel(final List<StreamFilter> inViews, final Integer inHiddenLineIndex,
            final FilterRenderStrategy inRenderer, final boolean readOnly)
    {
        views = inViews;
        hiddenLineIndex = inHiddenLineIndex;
        renderer = inRenderer;
        showMore.add(new HTML(""));
        showMore.add(showMoreLink);

        FlowPanel header = new FlowPanel();
        Label headerLbl = new Label(renderer.getTitle());
        header.addStyleName(StaticResourceBundle.INSTANCE.coreCss().header());

        this.add(header);
        header.add(headerLbl);

        if (!readOnly)
        {
            Anchor addView = new Anchor("add");
            FlowPanel addButton = new FlowPanel();
            addButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().filterAddButton());
            addButton.insert(new HTML(""), 0);
            addButton.add(addView);
            addView.addClickListener(new ClickListener()
            {
                public void onClick(final Widget arg0)
                {
                    Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());
                    Dialog.showCentered(renderer.getDialogContent());
                }
            });

            header.add(addButton);
        }

        renderer.setUpEvents(this);

        IndexedDropController tabDropController = new IndexedDropController(panel);
        viewDragController = new PickupDragController(viewBoundaryPanel, false);

        viewDragController.setBehaviorConstrainedToBoundaryPanel(true);
        viewDragController.setBehaviorMultipleSelection(false);
        viewDragController.setBehaviorDragStartSensitivity(DRAG_SENSITIVITY);
        viewDragController.addDragHandler(new FilterDragHandler(this, renderer.getReorderableModel()));
        viewDragController.registerDropController(tabDropController);

        int count = 0;

        for (StreamFilter view : views)
        {
            FilterPanel listItem = renderer.getFilterPanel(view);
            if (count > hiddenLineIndex)
            {
                ((Widget) listItem).addStyleName(StaticResourceBundle.INSTANCE.coreCss().hide());
            }
            if (count == hiddenLineIndex + 1)
            {
                addHiddenLine();
            }
            listCache.put(view.getId(), listItem);
            panel.add((Widget) listItem);
            viewDragController.makeDraggable((Widget) listItem, listItem.getMoveHandle());
            count++;
        }

        if (!addedHiddenLine && views.size() > 0)
        {
            addedHiddenLine = true;
            addHiddenLine();
            showMore.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hideText());
        }

        viewBoundaryPanel.add(panel);
        this.add(viewBoundaryPanel);

        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        String filterId = event.getParameters().get(renderer.getFilterHistoryToken());

                        if (filterId != null)
                        {
                            listCache.get(Long.valueOf(filterId)).activate();

                            // checks if the item being navigated to is hidden below the hiddenLineIndex and expand.
                            int i = 0;
                            for (StreamFilter view : views)
                            {
                                if (view.getId() == Long.valueOf(filterId))
                                {
                                    if (i > hiddenLineIndex)
                                    {
                                        unhide();
                                    }
                                    break;
                                }
                                i++;
                            }
                        }
                    }

                }, true);

    }

    /**
     * Add the hidden line.
     */
    private void addHiddenLine()
    {
        final FilterListPanel thisBuffered = this;
        addedHiddenLine = true;

        showMore.setStyleName(StaticResourceBundle.INSTANCE.coreCss().showMore());
        showMoreLink.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                if (hidden)
                {
                    unhide();
                }
                else
                {
                    hidden = true;
                    thisBuffered.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().unhide());
                    showMoreLink.setText("show more");
                    showMore.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().showLess());
                }
            }
        });
        panel.add(showMore);
    }

    /**
     * hides the text on the hidden line if its the last one.
     *
     * @param inHiddenLineIndex
     *            the hidden line index.
     */
    public void hideTextOnHiddenLine(final int inHiddenLineIndex)
    {
        if (inHiddenLineIndex == listCache.size())
        {
            showMore.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hideText());
        }
    }

    /**
     * Show the text on the hidden line.
     */
    public void showTextOnHiddenLine()
    {
        showMore.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hideText());
    }

    /**
     * Activate a filter.
     *
     * @param filter
     *            the filter.
     */
    public void activateFilter(final StreamFilter filter)
    {
        listCache.get(filter.getId()).activate();
    }

    /**
     * Unactivate all filters.
     */
    public void unactivateAll()
    {
        if (activeItem != null)
        {
            activeItem.unActivate();
            activeItem = null;
        }
    }

    /**
     * Adds a filter.
     *
     * @param filter
     *            the filter.
     */
    public void addFilter(final StreamFilter filter)
    {
        unhide();
        FilterPanel listItem = renderer.getFilterPanel(filter);
        ((Widget) listItem).addStyleName(StaticResourceBundle.INSTANCE.coreCss().hide());
        listCache.put(filter.getId(), listItem);
        panel.add((Widget) listItem);
        viewDragController.makeDraggable((Widget) listItem, listItem.getMoveHandle());

        listItem.updateHistory();

        if (!addedHiddenLine && listCache.size() > hiddenLineIndex)
        {
            addedHiddenLine = true;
            addHiddenLine();
            showMore.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hideText());
        }
        else
        {
            showMore.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hideText());
        }
    }

    /**
     * Activates the first item in the panel.
     */
    public void activateFirst()
    {
        ((FilterPanel) panel.getWidget(0)).updateHistory();
    }

    /**
     * Removes a filter.
     *
     * @param filter
     *            the filter.
     */
    public void removeFilter(final StreamFilter filter)
    {
        panel.remove((Widget) listCache.get(filter.getId()));
        listCache.remove(filter.getId());

        if (panel.getWidgetCount() > 0)
        {
            fixHiddenLine();
        }
        if (panel.getWidgetCount() > 0 && panel.getWidget(0) != null)
        {
            activateFirst();
        }
    }

    /**
     * Updates the filter.
     *
     * @param filter
     *            the filter.
     */
    public void updateFilter(final StreamFilter filter)
    {
        listCache.get(filter.getId()).setFilter(filter);
        activateFilter(filter);
    }

    /**
     * Fix the hidden line.
     */
    public void fixHiddenLine()
    {
        int itemIndex = panel.getWidgetIndex(showMore);

        if (itemIndex == 0)
        {
            panel.remove(showMore);
            addedHiddenLine = false;
            if (panel.getWidgetCount() > 0)
            {
                panel.insert(showMore, 1);
                panel.getWidget(0).removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hide());
            }
        }
        if (itemIndex == panel.getWidgetCount() - 1)
        {
            showMore.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hideText());
        }
        else
        {
            showMore.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hideText());
        }
    }

    /**
     * Get drop panel.
     *
     * @return the drop panel.
     */
    public VerticalPanel getDropPanel()
    {
        return panel;
    }

    /**
     * Gets the hidden line.
     *
     * @return the hidden line.
     */
    public Integer getHiddenLineIndex()
    {
        return panel.getWidgetIndex(showMore);
    }


    /**
     * Unhide.
     */
    public void unhide()
    {
        hidden = false;
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().unhide());
        showMoreLink.setText("show less");
        showMore.addStyleName(StaticResourceBundle.INSTANCE.coreCss().showLess());
    }

    /**
     * Get the views.
     *
     * @return the views.
     */
    public List<StreamFilter> getViews()
    {
        return views;
    }
}
