package com.johannesbrodwall.recyclerviewdemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DemoCategorizedListModel {

    private ItemChangeListener itemChangeListener = new NullItemChangeListener();

    private final List<DemoItem> items = new ArrayList<>();
    private final List<DemoCategory> categories = new ArrayList<>();
    private final Map<UUID, List<DemoItem>> itemsPerCategory = new HashMap<>();

    private boolean isGroupedByCategory = true;
    private final Map<UUID, Boolean> isExpanded = new HashMap<>();

    private List<Object> displayedRows = new ArrayList<>();
    private String filter;

    public DemoCategorizedListModel(List<DemoCategory> categories, List<DemoItem> items) {
        this.categories.addAll(categories);
        this.items.addAll(items);

        for (DemoCategory category : categories) {
            isExpanded.put(category.getId(), true);
            itemsPerCategory.put(category.getId(), new ArrayList<DemoItem>());
        }
        for (DemoItem item : items) {
            itemsPerCategory.get(item.getCategoryId()).add(item);
        }

        displayWithCategories();
    }

    public void toggleCategories() {
        toggleCategories(!isGroupedByCategory);
    }

    public void toggleCategories(boolean isGroupedByCategory) {
        this.isGroupedByCategory = isGroupedByCategory;
        redisplay();
    }

    private void redisplay() {
        if (isGroupedByCategory) {
            displayWithCategories();
        } else {
            displayOnlyChildren();
        }
        itemChangeListener.notifyDataSetChanged();
    }

    private void displayWithCategories() {
        displayedRows.clear();
        Collections.sort(categories);

        for (DemoCategory category : categories) {
            List<DemoItem> items = new ArrayList<>();
            for (DemoItem item : itemsPerCategory.get(category.getId())) {
                if (matchesFilter(item)) {
                    items.add(item);
                }
            }
            if (!items.isEmpty()) {
                if (isExpanded.get(category.getId())) {
                    Collections.sort(items);
                    displayedRows.add(category);
                    displayedRows.addAll(items);
                } else {
                    displayedRows.add(category);
                }
            }
        }
    }

    private boolean matchesFilter(DemoItem item) {
        return filter == null || item.getName().contains(filter);
    }

    private void displayOnlyChildren() {
        displayedRows.clear();

        Collections.sort(items);
        for (DemoItem item : items) {
            if (matchesFilter(item)) {
                displayedRows.add(item);
            }
        }
    }

    public int getRowCount() {
        return displayedRows.size();
    }

    public Object getRow(int position) {
        return displayedRows.get(position);
    }

    public boolean isExpanded(DemoCategory category) {
        return isExpanded.get(category.getId());
    }

    public void collapseParent(DemoCategory category) {
        if (!isExpanded.get(category.getId())) {
            return;
        }
        isExpanded.put(category.getId(), false);

        int position = displayedRows.indexOf(category);
        int childPosition = position + 1;
        while (childPosition < displayedRows.size() && getRow(childPosition) instanceof DemoItem) {
            displayedRows.remove(childPosition);
        }

        itemChangeListener.notifyItemChanged(position);
        itemChangeListener.notifyItemRangeRemoved(childPosition, this.itemsPerCategory.get(category.getId()).size());
    }

    public void expandParent(DemoCategory category) {
        if (isExpanded.get(category.getId())) {
            return;
        }
        isExpanded.put(category.getId(), true);

        int position = displayedRows.indexOf(category);
        List<DemoItem> children = new ArrayList<>(this.itemsPerCategory.get(category.getId()));
        Collections.sort(children);
        displayedRows.addAll(position + 1, children);
        itemChangeListener.notifyItemChanged(position);
        itemChangeListener.notifyItemRangeInserted(position + 1, children.size());
    }

    public void remove(int position) {
        Object o = displayedRows.remove(position);
        if (o instanceof DemoCategory) {
            categories.remove(o);
            DemoCategory category = (DemoCategory) o;
            List<DemoItem> itemsToRemove = itemsPerCategory.remove(category.getId());

            if (isExpanded.get(category.getId())) {
                for (int i = 0; i < itemsToRemove.size(); i++) {
                    displayedRows.remove(position);
                }
                itemChangeListener.notifyItemRangeRemoved(position, 1 + itemsToRemove.size());
            } else {
                itemChangeListener.notifyItemRemoved(position);
            }
        } else if (o instanceof DemoItem) {
            DemoItem item = (DemoItem) o;
            itemsPerCategory.get(item.getCategoryId()).remove(item);
            items.remove(item);
            itemChangeListener.notifyItemRemoved(position);

            if (itemsPerCategory.get(item.getCategoryId()).isEmpty()) {
                displayedRows.remove(position - 1);
                itemChangeListener.notifyItemRemoved(position - 1);
            }
        }
    }

    public int indexOf(Object o) {
        return displayedRows.indexOf(o);
    }

    public void toggleCollapsed(DemoCategory category) {
        if (isExpanded(category)) {
            collapseParent(category);
        } else {
            expandParent(category);
        }
    }

    public void setItemChangeListener(ItemChangeListener itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
    }

    public List<Object> getDisplayedRows() {
        return displayedRows;
    }

    public void setFilter(String filter) {
        this.filter = filter;
        redisplay();
    }

    public void update(DemoItem item) {
        int position = indexOf(item);
        if (!matchesFilter(item)) {
            if (position != -1) {
                displayedRows.remove(position);

                if (isGroupedByCategory && !categoryHasVisibleItems(item.getCategoryId())) {
                    displayedRows.remove(position - 1);
                    itemChangeListener.notifyItemRangeRemoved(position - 1, 2);
                } else {
                    itemChangeListener.notifyItemRemoved(position);
                }
            }
        } else if (position == -1) {
            redisplay();
        } else {
            itemChangeListener.notifyItemChanged(position);
        }
    }

    private boolean categoryHasVisibleItems(UUID categoryId) {
        for (DemoItem otherItem : this.itemsPerCategory.get(categoryId)) {
            if (matchesFilter(otherItem)) {
                return true;
            }
        }
        return false;
    }

    public interface ItemChangeListener {
        void notifyItemChanged(int position);

        void notifyItemRangeInserted(int offset, int insertedItemCount);

        void notifyItemRangeRemoved(int offset, int removedItemCount);

        void notifyItemRemoved(int position);

        void notifyDataSetChanged();

        void notifyItemInserted(int position);
    }

    private static class NullItemChangeListener implements ItemChangeListener {
        @Override
        public void notifyItemChanged(int position) {
        }

        @Override
        public void notifyItemRangeInserted(int offset, int insertedItemCount) {
        }

        @Override
        public void notifyItemRangeRemoved(int offset, int removedItemCount) {
        }

        @Override
        public void notifyItemRemoved(int position) {
        }

        @Override
        public void notifyDataSetChanged() {
        }

        @Override
        public void notifyItemInserted(int position) {

        }
    }
}
