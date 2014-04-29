package de.zeami.ennosuke.falloutterminalhacker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ActionBarActivity implements View.OnKeyListener {


    public final static String EXTRA_WORDLIST = "de.zeami.ennosuke.falloutterminalhacker.WORDLIST";
    public final static String EXTRA_WORDLENGTH = "de.zeami.ennosuke.falloutterminalhacker.WORDLENGTH";

    public ArrayList<String> Words = new ArrayList<String>();
    public int WordLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void clearWords(View view)
    {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_all_words_title)
                .setMessage(R.string.clear_all_words_message)
                .setPositiveButton(R.string.clear_all_words_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Words = new ArrayList<String>();
                        WordLength = 0;
                        ((TextView)findViewById(R.id.word_list)).setText("");

                    }
                })
                .setNegativeButton(R.string.clear_all_words_nok, null)
                .show();
    }

    public void beginHack(View view)
    {
        if(WordLength == 0 || Words.size() < 2)
        {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.not_enough_words)
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return;
        }
        Intent intent = new Intent(this, Hacking.class);
        intent.putStringArrayListExtra(EXTRA_WORDLIST, Words);
        intent.putExtra(EXTRA_WORDLENGTH, WordLength);
        startActivity(intent);

    }

    public void addWord(View view)
    {
        EditText editWord = (EditText)findViewById(R.id.edit_word);
        String word = editWord.getText().toString().toUpperCase();

        if(WordLength > 0 && word.length() != WordLength)
        {
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.wordlength_not_matching, word.length(),WordLength))
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return;
        }
        if(WordLength == 0)
        {
            WordLength = word.length();
        }
        if(Words.contains(word))
        {
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.word_already_entered, word))
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return;
        }
        Words.add(word);
        String newTextList = "";
        for(String item: Words)
        {
            newTextList = newTextList+item+"\n";
        }
        ((TextView)findViewById(R.id.word_list)).setText(newTextList);
        editWord.setText("");
    }

    public void clearWord(View view)
    {
        EditText editWord = (EditText)findViewById(R.id.edit_word);
        editWord.setText("");
        Toast.makeText(this, getResources().getString(R.string.word_cleared), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
        {
            addWord(view);
        }
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
