package com.johannesbrodwall.recyclerviewdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ShowDemoItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_category);

        Bundle extras = getIntent().getExtras();
        final int position = extras.getInt("position");

        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("position", position);
                setResult(1, data);
                finish();
            }
        });
    }
}
