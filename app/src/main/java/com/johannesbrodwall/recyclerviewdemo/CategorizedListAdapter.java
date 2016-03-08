package com.johannesbrodwall.recyclerviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CategorizedListAdapter extends RecyclerView.Adapter<CategorizedListAdapter.DemoViewHolder> implements DemoCategorizedListModel.ItemChangeListener {

    public interface OnClickListener {
        void onClick(Object item);
    }

    private OnClickListener onItemClickListener;

    public void setOnItemClickListener(OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemClick(Object item) {
        if (onItemClickListener != null) {
            onItemClickListener.onClick(item);
        }
    }


    private static final int ROW_CATEGORY = 123;
    private static final int ROW_ITEM = 413;
    private final DemoCategorizedListModel model;

    private LayoutInflater inflater;

    public CategorizedListAdapter(DemoCategorizedListModel model, Context context) {
        this.model = model;
        model.setItemChangeListener(this);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        ItemTouchHelper.Callback listItemTouchListener = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            ColorDrawable background = new ColorDrawable(Color.RED);
            Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_highlight_remove_24dp);
            int xMarkMargin = (int) recyclerView.getContext().getResources().getDimension(R.dimen.ic_clear_margin);

            {
                icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CategorizedListAdapter.DemoCategoryViewHolder) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                remove(swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // HACK: it seems like we sometimes get onDraw for items that have been removed
                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                int iconWidth = icon.getIntrinsicWidth();
                int iconHeight = icon.getIntrinsicWidth();

                int rowHeight = itemView.getBottom() - itemView.getTop();

                int iconTop = itemView.getTop() + (rowHeight - iconHeight)/2;

                if (dX < 0.0) {
                    // Left swipe
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    background.draw(c);

                    int iconLeft = itemView.getRight() - xMarkMargin - iconWidth;
                    icon.setBounds(iconLeft, iconTop, iconLeft + iconWidth, iconTop + iconHeight);
                    icon.draw(c);
                } else {
                    // Right swipe
                    background.setBounds(itemView.getLeft(), itemView.getTop(), (int) (itemView.getLeft() + dX), itemView.getBottom());
                    background.draw(c);

                    int iconLeft = itemView.getLeft() + xMarkMargin;
                    icon.setBounds(iconLeft, iconTop, iconLeft + iconWidth, iconTop + iconHeight);
                    icon.draw(c);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(listItemTouchListener).attachToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof DemoCategory) return ROW_CATEGORY;
        if (getItem(position) instanceof DemoItem) return ROW_ITEM;
        throw new IllegalArgumentException("Unknown row type");
    }

    @Override
    public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ROW_CATEGORY) {
            return new DemoCategoryViewHolder(inflater.inflate(R.layout.parent_row, parent, false));
        } else if (viewType == ROW_ITEM) {
            return new DemoItemViewHolder(inflater.inflate(R.layout.item_row, parent, false));
        } else {
            throw new IllegalArgumentException("Unknown view type " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(DemoViewHolder holder, int position) {
        holder.onBindViewHolder(model.getRow(position));
    }

    @Override
    public int getItemCount() {
        return model.getRowCount();
    }

    private Object getItem(int position) {
        return model.getRow(position);
    }

    public void remove(int position) {
        model.remove(position);
    }

    public static abstract class DemoViewHolder extends RecyclerView.ViewHolder {
        public DemoViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(Object row);
    }


    class DemoCategoryViewHolder extends DemoViewHolder implements View.OnClickListener {

        private final TextView categoryText;
        private final Drawable drawableCollapse;
        private DemoCategory category;
        private final ImageView categoryToggleImage;
        private final Drawable drawableExpand;

        public DemoCategoryViewHolder(View parent) {
            super(parent);
            categoryText = (TextView) parent.findViewById(R.id.categoryText);
            categoryToggleImage = (ImageView) parent.findViewById(R.id.categoryToggleImage);
            drawableExpand = parent.getContext().getDrawable(R.drawable.ic_vertical_align_bottom_24dp);
            drawableCollapse = parent.getContext().getDrawable(R.drawable.ic_file_upload_24dp);
            parent.setOnClickListener(this);
        }

        @Override
        public void onBindViewHolder(Object row) {
            category = (DemoCategory)row;
            categoryText.setText(category.getName());
            categoryToggleImage.setImageDrawable(model.isExpanded(category) ? drawableCollapse : drawableExpand);

        }

        @Override
        public void onClick(View v) {
            model.toggleCollapsed(category);
            onItemClick(category);
        }
    }

    class DemoItemViewHolder extends DemoViewHolder implements View.OnClickListener {
        private final TextView itemText;
        private final TextView itemPriceText;
        private DemoItem item;

        public DemoItemViewHolder(View parent) {
            super(parent);
            itemText = (TextView) parent.findViewById(R.id.itemText);
            itemPriceText = (TextView)parent.findViewById(R.id.itemPriceText);
            parent.setOnClickListener(this);
        }

        @Override
        public void onBindViewHolder(Object row) {
            item = (DemoItem)row;
            itemText.setText(item.getName());
            itemPriceText.setText("11 kr");
        }

        @Override
        public void onClick(View v) {
            onItemClick(item);
        }
    }
}
