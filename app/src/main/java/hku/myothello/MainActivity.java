package hku.myothello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    EditText txt_User1Name,txt_User2Name;
    Button btn_Start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_Start = (Button)findViewById(R.id.btn_Start);
        txt_User1Name = (EditText)findViewById(R.id.txt_User1Name);
        txt_User2Name = (EditText)findViewById(R.id.txt_User2Name);
        // Whenever the button is clicked, onClick is called
        btn_Start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
// TODO Auto-generated method stub
        if (v.getId() == R.id.btn_Start) {
            String uname1 = txt_User1Name.getText().toString();
            String uname2 = txt_User2Name.getText().toString();

            Intent intent = new Intent(getBaseContext(), Game_othello.class);
            ArrayList<String> cname = new ArrayList<String>();
            cname.add(uname1);
            cname.add(uname2);
            intent.putStringArrayListExtra("Player_name", cname);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
