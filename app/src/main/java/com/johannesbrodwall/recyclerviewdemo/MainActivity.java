package com.johannesbrodwall.recyclerviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final int SHOW_ITEM = 1423;
    private MenuItem toggleCategoriesMenuItem;
    private DemoCategorizedListModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView categorizedList = (RecyclerView) findViewById(R.id.categorizedList);

        categorizedList.setLayoutManager(new LinearLayoutManager(this));
        categorizedList.addItemDecoration(new DividerItemDecoration(this));
        model = getModel();
        CategorizedListAdapter adapter = new CategorizedListAdapter(model, this);
        categorizedList.setAdapter(adapter);

        adapter.setOnItemClickListener(new CategorizedListAdapter.OnClickListener() {
            @Override
            public void onClick(Object item) {
                if (item instanceof DemoItem) {
                    Intent intent = new Intent(MainActivity.this, ShowDemoItem.class);
                    intent.putExtra("position", model.indexOf(item));
                    startActivityForResult(intent, SHOW_ITEM);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHOW_ITEM && resultCode == 1) {
            model.remove(data.getExtras().getInt("position"));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toggleCategoriesMenuItem = menu.add("Toggle category view");
        toggleCategoriesMenuItem.setIcon(R.drawable.ic_list_24dp);
        toggleCategoriesMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == toggleCategoriesMenuItem) {
            model.toggleCategories();
        }
        return true;
    }

    private DemoCategorizedListModel getModel() {
        return new DemoCategorizedListModel();
    }
}
