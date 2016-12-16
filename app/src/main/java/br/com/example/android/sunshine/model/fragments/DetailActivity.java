package br.com.example.android.sunshine.model.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import br.com.example.android.sunshine.app.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView textView = (TextView) findViewById(R.id.detail_text);
        Intent intent = this.getIntent();
        //to avoid nullpointers and to check with extra_text exist =]
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT))
            textView.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_setting,menu);
        return true;
    }
}
