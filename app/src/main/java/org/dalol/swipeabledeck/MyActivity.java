package org.dalol.swipeabledeck;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Filippo on 8/20/2016.
 */
public class MyActivity extends AppCompatActivity {

    private SwipeableDeckView deckView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        deckView = (SwipeableDeckView) findViewById(R.id.swipeableDeck);
        View viewById = findViewById(R.id.handle);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deckView.changeImage();
            }
        });
    }
}
