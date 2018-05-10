package group1.tcss450.uw.edu.a450groupone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);
        if(savedInstanceState == null) {
            if (findViewById(R.id.chatContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.chatContainer, new ChatFragment())
                        .commit();
            }
        }
    }

}
