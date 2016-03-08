package com.johannesbrodwall.recyclerviewdemo;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class DemoCategorizedListModelTest {

    private final DemoCategory category1 = new DemoCategory("Category 1");
    private final DemoCategory category2 = new DemoCategory("Category 2");
    private final DemoItem item1_1 = new DemoItem("Item 1.1", category1.getId());
    private final DemoItem item2_1 = new DemoItem("Item 2 A", category2.getId());
    private final DemoItem item2_2 = new DemoItem("Item 2 B", category2.getId());

    private DemoCategorizedListModel.ItemChangeListener mockChangeListener =
            Mockito.mock(DemoCategorizedListModel.ItemChangeListener.class);

    @Test
    public void shouldCategorizeAndSortItems() {
        DemoCategorizedListModel model = new DemoCategorizedListModel(
                Arrays.asList(category2, category1),
                Arrays.asList(item2_2, item1_1, item2_1));

        assertThat(model.getRows()).containsExactly(category1, item1_1, category2, item2_1, item2_2);
        assertThat(model.getRowCount()).isEqualTo(5);
        assertThat(model.getRow(2)).isEqualTo(category2);
    }

    @Test
    public void shouldDisplayOnlyItems() {
        DemoCategorizedListModel model = new DemoCategorizedListModel(
                Arrays.asList(category1, category2),
                Arrays.asList(item1_1, item2_1, item2_2));

        model.toggleCategories();
        assertThat(model.getRows()).containsExactly(item1_1, item2_1, item2_2);

        model.toggleCategories();
        assertThat(model.getRows()).containsExactly(category1, item1_1, category2, item2_1, item2_2);
    }

    @Test
    public void shouldRefreshOnToggleCategories() {
        DemoCategorizedListModel model = new DemoCategorizedListModel(
                Collections.singletonList(category1),
                Collections.singletonList(item1_1));
        model.setItemChangeListener(mockChangeListener);

        model.toggleCategories();
        verify(mockChangeListener).notifyDataSetChanged();
    }


    @Test
    public void shouldCollapseCategory() {
        DemoCategorizedListModel model = new DemoCategorizedListModel(
                Arrays.asList(category1, category2),
                Arrays.asList(item1_1, item2_1, item2_2));

        model.toggleCollapsed(category2);
        assertThat(model.getRows()).containsExactly(category1, item1_1, category2);

        model.toggleCollapsed(category2);
        assertThat(model.getRows()).containsExactly(category1, item1_1, category2, item2_1, item2_2);
    }

    @Test
    public void shouldNotifyOnCollapseCategory() {
        DemoCategorizedListModel model = new DemoCategorizedListModel(
                Arrays.asList(category1, category2),
                Arrays.asList(item1_1, item2_1, item2_2));
        model.setItemChangeListener(mockChangeListener);

        model.toggleCollapsed(category2);
        verify(mockChangeListener).notifyItemChanged(2);
        verify(mockChangeListener).notifyItemRangeRemoved(3, 2);
        reset(mockChangeListener);

        model.toggleCollapsed(category2);
        verify(mockChangeListener).notifyItemChanged(2);
        verify(mockChangeListener).notifyItemRangeInserted(3, 2);
    }

    @Test
    public void shouldRemoveItem() {
        DemoCategorizedListModel model = new DemoCategorizedListModel(
                Arrays.asList(category1, category2),
                Arrays.asList(item1_1, item2_1, item2_2));

        model.remove(model.indexOf(item1_1));
        assertThat(model.getRows()).containsExactly(category1, category2, item2_1, item2_2);

        model.remove(model.indexOf(category2));
        assertThat(model.getRows()).containsExactly(category1);
    }

    @Test
    public void shouldNotifyOnRemovedItem() {
        DemoCategorizedListModel model = new DemoCategorizedListModel(
                Arrays.asList(category1, category2),
                Arrays.asList(item1_1, item2_1, item2_2));
        model.setItemChangeListener(mockChangeListener);

        model.remove(model.indexOf(item1_1));
        verify(mockChangeListener).notifyItemRemoved(1);
        reset(mockChangeListener);
    }

    @Test
    public void shouldNotifyOnRemovedCategory() {
        DemoCategorizedListModel model = new DemoCategorizedListModel(
                Arrays.asList(category1, category2),
                Arrays.asList(item2_1, item2_2));
        model.setItemChangeListener(mockChangeListener);

        model.remove(model.indexOf(category2));
        verify(mockChangeListener).notifyItemRangeRemoved(1, 3);
    }

}