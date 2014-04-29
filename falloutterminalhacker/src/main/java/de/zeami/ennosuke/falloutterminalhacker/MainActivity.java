/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Dennis Hillmann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.zeami.ennosuke.falloutterminalhacker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {


    public final static String EXTRA_WORDLIST = "de.zeami.ennosuke.falloutterminalhacker.WORDLIST";
    public final static String EXTRA_WORDLENGTH = "de.zeami.ennosuke.falloutterminalhacker.WORDLENGTH";

    public ArrayList<String> Words = new ArrayList<String>();
    public int WordLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        if(id == R.id.action_help) {
            Intent intent = new Intent(this, MainHelp.class);
            startActivity(intent);
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
        if(editWord == null) return;
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
        refreshWordListView();
        editWord.setText("");
    }

    protected void refreshWordListView()
    {
        String stringToSet = "";
        int start = 0;
        for(String word: Words)
        {
            stringToSet += word+", ";
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(stringToSet);
        for(String word: Words)
        {
           final String clickedString = word;
           ssb.setSpan(new ClickableSpan() {
               @Override
               public void onClick(View view) {
                   Words.remove(clickedString);
                   refreshWordListView();
               }
           }, start, (start+word.length()),0);
           start += word.length()+2;
        }
        TextView view = (TextView)findViewById(R.id.word_list);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    public void clearWord(View view)
    {
        EditText editWord = (EditText)findViewById(R.id.edit_word);
        editWord.setText("");
        Toast.makeText(this, getResources().getString(R.string.word_cleared), Toast.LENGTH_SHORT).show();
    }


}
