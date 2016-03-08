package com.johannesbrodwall.recyclerviewdemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DemoCategorizedListModel {

    private final List<DemoItem> items = new ArrayList<>();
    private final List<DemoCategory> categories = new ArrayList<>();
    private final Map<UUID, List<DemoItem>> itemsPerCategory = new HashMap<>();
    private final Map<UUID, Boolean> isExpanded = new HashMap<>();

    private List<Object> rows = new ArrayList<>();
    private boolean isCategoriesDisplayed = true;
    private ItemChangeListener itemChangeListener = new NullItemChangeListener();

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

        expandAll();
    }

    private void expandAll() {
        rows.clear();
        Collections.sort(categories);

        for (DemoCategory category : categories) {
            rows.add(category);
            if (isExpanded.get(category.getId())) {
                List<DemoItem> items = new ArrayList<>(itemsPerCategory.get(category.getId()));
                Collections.sort(items);
                rows.addAll(items);
            }
        }
    }

    private void displayOnlyChildren() {
        rows.clear();

        Collections.sort(items);
        rows.addAll(items);
    }

    public int getRowCount() {
        return rows.size();
    }

    public Object getRow(int position) {
        return rows.get(position);
    }

    public boolean isExpanded(DemoCategory category) {
        return isExpanded.get(category.getId());
    }

    public void collapseParent(DemoCategory category) {
        isExpanded.put(category.getId(), false);

        int position = rows.indexOf(category);
        int childPosition = position + 1;
        while (childPosition < rows.size() && getRow(childPosition) instanceof DemoItem) {
            rows.remove(childPosition);
        }

        itemChangeListener.notifyItemChanged(position);
        itemChangeListener.notifyItemRangeRemoved(childPosition, this.itemsPerCategory.get(category.getId()).size());
    }

    public void expandParent(DemoCategory category) {
        isExpanded.put(category.getId(), true);

        int position = rows.indexOf(category);
        rows.addAll(position + 1, this.itemsPerCategory.get(category.getId()));
        itemChangeListener.notifyItemChanged(position);

        int count = this.itemsPerCategory.get(category.getId()).size();
        itemChangeListener.notifyItemRangeInserted(position + 1, count);
    }

    public void toggleCategories() {
        isCategoriesDisplayed = !isCategoriesDisplayed;
        if (isCategoriesDisplayed) {
            expandAll();
        } else {
            displayOnlyChildren();
        }
        itemChangeListener.notifyDataSetChanged();
    }

    public void remove(int position) {
        Object o = rows.remove(position);
        if (o instanceof DemoCategory) {
            categories.remove(o);
            List<DemoItem> itemsToRemove = itemsPerCategory.remove(((DemoCategory) o).getId());

            for (int i=0; i<itemsToRemove.size(); i++) {
                rows.remove(position);
            }
            itemChangeListener.notifyItemRangeRemoved(position, 1+itemsToRemove.size());
        } else {
            DemoItem item = (DemoItem)o;
            itemsPerCategory.get(item.getCategoryId()).remove(item);
            items.remove(item);
            itemChangeListener.notifyItemRemoved(position);
        }
    }

    public int indexOf(Object o) {
        return rows.indexOf(o);
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

    public List<Object> getRows() {
        return rows;
    }

    public interface ItemChangeListener {
        void notifyItemChanged(int position);

        void notifyItemRangeInserted(int offset, int insertedItemCount);

        void notifyItemRangeRemoved(int offset, int removedItemCount);

        void notifyItemRemoved(int position);

        void notifyDataSetChanged();
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
    }
}
