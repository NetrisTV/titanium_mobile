/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.modules.titanium.ui.widget.listview;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;

import ti.modules.titanium.ui.UIModule;
import ti.modules.titanium.ui.widget.TiUIListView;

@Kroll.proxy(
	creatableInModule = ti.modules.titanium.ui.UIModule.class,
	propertyAccessors = {
		TiC.PROPERTY_CAN_SCROLL,
		TiC.PROPERTY_CASE_INSENSITIVE_SEARCH,
		TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE,
		TiC.PROPERTY_FAST_SCROLL,
		TiC.PROPERTY_FOOTER_TITLE,
		TiC.PROPERTY_FOOTER_VIEW,
		TiC.PROPERTY_HEADER_TITLE,
		TiC.PROPERTY_HEADER_VIEW,
		TiC.PROPERTY_REFRESH_CONTROL,
		TiC.PROPERTY_SEARCH_TEXT,
		TiC.PROPERTY_SEARCH_VIEW,
		TiC.PROPERTY_SEPARATOR_COLOR,
		TiC.PROPERTY_SEPARATOR_HEIGHT,
		TiC.PROPERTY_SEPARATOR_STYLE,
		TiC.PROPERTY_SHOW_VERTICAL_SCROLL_INDICATOR,
		TiC.PROPERTY_TEMPLATES
	}
)
public class ListViewProxy extends TiViewProxy
{
	private static final String TAG = "ListViewProxy";

	private List<ListSectionProxy> sections = new ArrayList<>();
	private List<ListItemProxy> markers = new ArrayList<>();

	public ListViewProxy()
	{
		super();

		defaultValues.put(TiC.PROPERTY_CAN_SCROLL, true);
		defaultValues.put(TiC.PROPERTY_CASE_INSENSITIVE_SEARCH, true);
		defaultValues.put(TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE, UIModule.LIST_ITEM_TEMPLATE_DEFAULT);
		defaultValues.put(TiC.PROPERTY_FAST_SCROLL, false);
	}

	/**
	 * Add marker for list item.
	 * This will fire the `marker` event when the item is scrolled into view.
	 *
	 * @param markerProperties Dictionary defining marker.
	 */
	@Kroll.method
	public void addMarker(KrollDict markerProperties)
	{
		final int itemIndex = markerProperties.getInt(TiC.PROPERTY_ITEM_INDEX);
		final int sectionIndex = markerProperties.getInt(TiC.PROPERTY_SECTION_INDEX);
		final ListSectionProxy section = getSectionByIndex(sectionIndex);

		if (section != null) {
			final ListItemProxy item = section.getListItemAt(itemIndex);

			if (item != null) {
				markers.add(item);
			}
		}
	}

	/**
	 * Append sections to list.
	 *
	 * @param sections Sections to append.
	 * @param animation Ignored, for iOS parameter compatibility.
	 */
	@Kroll.method
	public void appendSection(Object sections, @Kroll.argument(optional = true) KrollDict animation)
	{
		if (sections instanceof Object[]) {

			// Append ListSection array.
			for (final Object o : (Object[]) sections) {
				if (o instanceof ListSectionProxy) {
					final ListSectionProxy section = (ListSectionProxy) o;

					section.setParent(this);
					this.sections.add(section);
				}
			}

			// Notify ListView of new sections.
			update();

		} else if (sections instanceof ListSectionProxy) {
			final ListSectionProxy section = (ListSectionProxy) sections;

			// Append ListSection.
			section.setParent(this);
			this.sections.add(section);

			// Notify ListView of new section.
			update();
		}
	}

	/**
	 * Create TiUIListView for proxy.
	 *
	 * @param activity the context activity.
	 * @return TiUIView
	 */
	@Override
	public TiUIView createView(Activity activity)
	{
		return new TiUIListView(this);
	}

	/**
	 * Remove section from list at specified index.
	 *
	 * @param index Index of section to remove.
	 * @param animation Ignored, for iOS parameter compatibility.
	 */
	@Kroll.method
	public void deleteSectionAt(int index, @Kroll.argument(optional = true) KrollDict animation)
	{
		final ListSectionProxy section = getSectionByIndex(index);

		if (section != null) {

			// Remove section from list.
			section.setParent(null);
			this.sections.remove(section);

			// Notify ListView of removed section.
			update();
		}
	}

	@Override
	public String getApiName()
	{
		return "Ti.UI.ListView";
	}

	public List<ListItemProxy> getCurrentItems()
	{
		final TiListView listView = getListView();

		if (listView != null) {
			final ListViewAdapter adapter = listView.getAdapter();

			if (adapter != null) {
				return adapter.getModels();
			}
		}
		return null;
	}

	/**
	 * Get index for specified section.
	 *
	 * @param section Section of index to obtain.
	 * @return Integer of index.
	 */
	public int getIndexOfSection(ListSectionProxy section)
	{
		return this.sections.indexOf(section);
	}

	/**
	 * Get native ListView implementation.
	 *
	 * @return TiListView
	 */
	private TiListView getListView()
	{
		final TiUIListView view = (TiUIListView) this.view;

		if (view != null) {
			return view.getListView();
		}
		return null;
	}

	/**
	 * Get section for specified index.
	 *
	 * @param index Index of section to obtain.
	 * @return ListSectionProxy.
	 */
	public ListSectionProxy getSectionByIndex(int index)
	{
		final ListSectionProxy section = this.sections.get(index);

		if (section != null) {
			return section;
		}
		return null;
	}

	/**
	 * Get current section count.
	 *
	 * @return Number of sections in list.
	 */
	@Kroll.getProperty
	public int getSectionCount()
	{
		return sections.size();
	}

	/**
	 * Get current sections.
	 *
	 * @return Array of ListSections.
	 */
	@Kroll.method
	@Kroll.getProperty
	public ListSectionProxy[] getSections()
	{
		return this.sections.toArray(new ListSectionProxy[this.sections.size()]);
	}

	/**
	 * Is ListView currently filtered by search results.
	 *
	 * @return Boolean
	 */
	public boolean isFiltered()
	{
		final TiListView listView = getListView();

		if (listView != null) {
			return listView.isFiltered();
		}

		return false;
	}

	/**
	 * Handle setting of property.
	 *
	 * @param name Property name.
	 * @param value Property value.
	 */
	@Override
	public void setProperty(String name, Object value)
	{
		super.setProperty(name, value);

		if (name.equals(TiC.PROPERTY_SECTIONS)) {

			// Set list sections.
			setSections((Object[]) value);
		}
	}

	/**
	 * Set sections for list.
	 *
	 * @param sections Array of sections to set.
	 */
	@Kroll.method
	@Kroll.setProperty
	public void setSections(Object sections)
	{
		this.sections.clear();

		if (sections instanceof Object[]) {
			for (Object o : (Object[]) sections) {
				if (o instanceof ListSectionProxy) {
					final ListSectionProxy section = (ListSectionProxy) o;

					// Add section.
					section.setParent(this);
					this.sections.add(section);
				}
			}
		}

		update();
	}

	/**
	 * Override `handleGetView()` to update table if it has been re-used.
	 * (removed and re-added to a view)
	 *
	 * @return TiUIView
	 */
	@Override
	protected TiUIView handleGetView()
	{
		final TiUIView view = super.handleGetView();

		// Update table if being re-used.
		if (view != null) {
			update();
		}

		return view;
	}

	/**
	 * Determine if `marker` event should be fired for specified item.
	 *
	 * @param item Item to handle for `marker` event.
	 */
	public void handleMarker(ListItemProxy item)
	{
		if (markers.contains(item)) {
			final int itemIndex = item.getIndexInSection();
			final int sectionIndex = this.sections.indexOf(item.getParent());
			final KrollDict data = new KrollDict();

			// Create and fire marker event.
			data.put(TiC.PROPERTY_SECTION_INDEX, sectionIndex);
			data.put(TiC.PROPERTY_ITEM_INDEX, itemIndex);
			fireEvent(TiC.EVENT_MARKER, data, false);

			// One time event, remove marker.
			markers.remove(item);
		}
	}

	/**
	 * Insert sections at specified index.
	 *
	 * @param index Index to insert sections at.
	 * @param sections Sections to insert into list.
	 * @param animation Ignored, for iOS parameter compatibility.
	 */
	@Kroll.method
	public void insertSectionAt(int index, Object sections,
								@Kroll.argument(optional = true) KrollDict animation)
	{
		final int rawIndex = this.sections.indexOf(getSectionByIndex(index));

		if (rawIndex > -1) {
			if (sections instanceof Object[]) {

				// Insert ListSection array.
				for (final Object o : (Object[]) sections) {
					if (o instanceof ListSectionProxy) {
						final ListSectionProxy section = (ListSectionProxy) o;

						// Inset ListSection.
						section.setParent(this);
						this.sections.add(rawIndex, section);
					}
				}

				// Notify ListView of new sections.
				update();

			} else if (sections instanceof ListSectionProxy) {
				final ListSectionProxy section = (ListSectionProxy) sections;

				// Insert ListSection.
				section.setParent(this);
				this.sections.add(rawIndex, section);

				// Notify ListView of new section.
				update();
			}
		}
	}

	/**
	 * Release all views and items.
	 */
	@Override
	public void release()
	{
		releaseViews();

		if (this.sections != null) {
			this.sections.clear();
			this.sections = null;
		}

		if (this.markers != null) {
			this.markers.clear();
			this.markers = null;
		}

		super.release();
	}

	/**
	 * Release all sections.
	 */
	public void releaseSections()
	{
		for (ListSectionProxy section : this.sections) {
			section.releaseViews();
		}
	}

	/**
	 * Release all views associated with ListView.
	 */
	@Override
	public void releaseViews()
	{
		super.releaseViews();

		if (hasPropertyAndNotNull(TiC.PROPERTY_SEARCH)) {
			final TiViewProxy search = (TiViewProxy) getProperty(TiC.PROPERTY_SEARCH);
			search.releaseViews();
		}

		// Release all section views.
		releaseSections();
	}

	/**
	 * Replace section at specified index.
	 *
	 * @param index Index of section to replace.
	 * @param section Sections replace with.
	 * @param animation Ignored, for iOS parameter compatibility.
	 */
	@Kroll.method
	public void replaceSectionAt(int index, ListSectionProxy section,
								 @Kroll.argument(optional = true) KrollDict animation)
	{
		final ListSectionProxy previousSection = getSectionByIndex(index);
		final int rawIndex = this.sections.indexOf(previousSection);

		if (rawIndex > -1) {

			// Replace section.
			previousSection.setParent(null);
			section.setParent(this);
			this.sections.remove(rawIndex);
			this.sections.add(rawIndex, section);

			// Notify ListView of section.
			update();
		}
	}

	/**
	 * Scroll to item in list.
	 *
	 * @param sectionIndex Index of section for item.
	 * @param itemIndex Index of item in section.
	 */
	@Kroll.method
	public void scrollToItem(int sectionIndex, int itemIndex)
	{
		final TiListView listView = getListView();

		if (listView != null) {
			final ListSectionProxy section = getSectionByIndex(sectionIndex);

			if (section != null) {
				final ListItemProxy item = section.getListItemAt(itemIndex);

				if (item != null) {
					listView.getRecyclerView().smoothScrollToPosition(listView.getAdapterIndex(item.index));
				}
			}
		}
	}

	/**
	 * Select item in list.
	 *
	 * @param sectionIndex Index of section for item.
	 * @param itemIndex Index of item in section.
	 */
	@Kroll.method
	public void selectItem(int sectionIndex, int itemIndex)
	{
		scrollToItem(sectionIndex, itemIndex);

		final TiListView listView = getListView();

		if (listView != null) {
			final ListSectionProxy section = getSectionByIndex(sectionIndex);

			if (section != null) {
				final ListItemProxy item = section.getListItemAt(itemIndex);

				if (item != null) {
					((ListViewAdapter) listView.getRecyclerView().getAdapter()).getTracker().select(item);
				}
			}
		}
	}

	/**
	 * Set marker for list item.
	 * This will fire the `marker` event when the item is scrolled into view.
	 *
	 * @param markerProperties Dictionary defining marker.
	 */
	@Kroll.method
	public void setMarker(KrollDict markerProperties)
	{
		this.markers.clear();
		addMarker(markerProperties);
	}

	/**
	 * Notify ListView to update all adapter items.
	 */
	public void update()
	{
		final TiListView listView = getListView();

		if (listView != null) {
			listView.update();
		}
	}
}
