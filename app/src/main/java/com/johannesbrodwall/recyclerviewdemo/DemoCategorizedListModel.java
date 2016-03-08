package com.johannesbrodwall.recyclerviewdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DemoCategorizedListModel {

    private static final List<DemoCategory> CATEGORIES = Arrays.asList(new DemoCategory("foo"), new DemoCategory("bar"), new DemoCategory("baz"));
    private final List<DemoItem> items;
    private final List<DemoCategory> categories;
    private final Map<UUID, List<DemoItem>> itemsPerCategory = new HashMap<>();
    private final Map<UUID, Boolean> isExpanded = new HashMap<>();

    private List<Object> rows = new ArrayList<>();
    private boolean isCategoriesDisplayed = true;
    private ItemChangeListener itemChangeListener = new NullItemChangeListener();

    public DemoCategorizedListModel() {
        this(getCategories(), getItems());
    }

    public DemoCategorizedListModel(List<DemoCategory> categories, List<DemoItem> items) {
        this.categories = categories;
        this.items = items;

        for (DemoItem item : items) {
            if (!itemsPerCategory.containsKey(item.getCategoryId())) {
                itemsPerCategory.put(item.getCategoryId(), new ArrayList<DemoItem>());
            }
            itemsPerCategory.get(item.getCategoryId()).add(item);
        }
        for (DemoCategory category : categories) {
            isExpanded.put(category.getId(), true);
        }

        expandAll();
    }

    private void expandAll() {
        rows.clear();
        Collections.sort(categories);

        for (DemoCategory category : categories) {
            rows.add(category);
            if (isExpanded.get(category.getId())) {
                rows.addAll(itemsPerCategory.get(category.getId()));
            }
        }
    }

    private void displayOnlyChildren() {
        rows.clear();

        Collections.sort(items);
        rows.addAll(items);
    }

    private static List<DemoItem> getItems() {
        ArrayList<DemoItem> result = new ArrayList<>();
        for (int i=0; i<20; i++) {
            result.add(new DemoItem("item " + i, pickRandom(CATEGORIES).getId()));
        }
        return result;
    }

    private static <T> T pickRandom(List<T> alternatives) {
        return alternatives.get(new Random().nextInt(alternatives.size()));
    }

    private static List<DemoCategory> getCategories() {
        return CATEGORIES;
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

    public int collapseParent(DemoCategory category) {
        isExpanded.put(category.getId(), false);

        int position = rows.indexOf(category) + 1;
        while (position < rows.size() && getRow(position) instanceof DemoItem) {
            rows.remove(position);
        }
        return this.itemsPerCategory.get(category.getId()).size();
    }

    public int expandParent(DemoCategory category) {
        isExpanded.put(category.getId(), true);

        int position = rows.indexOf(category) + 1;
        rows.addAll(position, this.itemsPerCategory.get(category.getId()));
        return this.itemsPerCategory.get(category.getId()).size();
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
        } else {
            DemoItem item = (DemoItem)o;
            itemsPerCategory.get(item.getCategoryId()).remove(item);
            items.remove(item);
        }
        itemChangeListener.notifyItemRemoved(position);
    }

    public int indexOf(Object o) {
        return rows.indexOf(o);
    }

    public void toggleCollapsed(DemoCategory category) {
        int position = indexOf(category);
        if (isExpanded(category)) {
            itemChangeListener.notifyItemRangeRemoved(position + 1, collapseParent(category));
        } else {
            itemChangeListener.notifyItemRangeInserted(position + 1, expandParent(category));
        }
        itemChangeListener.notifyItemChanged(position);
    }

    public void setItemChangeListener(ItemChangeListener itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
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
