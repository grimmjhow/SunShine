package br.com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.com.example.android.sunshine.model.fragments.ForecastFragment;

/**
 * Classe Main activity!
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //calling the main activity
        setContentView(R.layout.activity_main_blank);

        if(savedInstanceState == null) {//Why???
            getSupportFragmentManager().beginTransaction() //Why begin a transaction? This isn't a database
                    .add(R.id.container,new ForecastFragment())
                    .commit(); // commting the changes?? Maybe?
        }
    }

    /**
     * Menu Item for main activity
     * @param menu
     * @return
     */
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.forecastfragment,menu);

        return true;
    }*/
}
