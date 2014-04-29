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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Hacking extends ActionBarActivity {


    private ArrayList<String> WordList;
    private int WordLength = 0;
    private String LastEnteredWord = "";
    private int LastCorrectChars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        WordList = intent.getStringArrayListExtra(MainActivity.EXTRA_WORDLIST);
        WordLength = intent.getIntExtra(MainActivity.EXTRA_WORDLENGTH,0);

        setContentView(R.layout.activity_hacking);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        refreshWordListView();
        LastEnteredWord = findFirstWord();
        ((TextView)findViewById(R.id.word_to_enter)).setText(LastEnteredWord);
        ((TextView)findViewById(R.id.wordlength)).setText(getResources().getString(R.string.wordlength, WordLength));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hacking, menu);
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

    public void continueHack(View view)
    {
        EditText correctChars = (EditText)findViewById(R.id.correct_chars);
        if(correctChars == null) return;
        LastCorrectChars = Integer.parseInt(correctChars.getText().toString());
        if(LastCorrectChars == WordLength)
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.correct_word_found)
                    .setMessage(getResources().getString(R.string.correct_word_found_message, LastEnteredWord))
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return;
        }
        WordList.remove(LastEnteredWord);
        removeNotPossible();
        refreshWordListView();
        if(WordList.size() > 1)
        {
            LastEnteredWord = WordList.get(0);
            ((TextView)findViewById(R.id.word_to_enter)).setText(LastEnteredWord);
        }
        else if(WordList.size() == 1)
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.correct_word_found)
                    .setMessage(getResources().getString(R.string.correct_word_found_message, WordList.get(0)))
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return;
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.something_went_wrong_title)
                    .setMessage(R.string.something_went_wrong_message)
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return;
        }
    }

    /**
     * Checks which word to use as a first try.
     *
     * The word in the first try should match as many words as possible on a character basis.
     * The positions of the characters do matter. If there are words with the same amount of matches
     * the first one found will be returned.
     *
     * @return String of the Word to use in the first try
     */
    public String findFirstWord()
    {
        String wordToUse = "";
        int maxMatchedWords = 0, minMatchedChars = (WordLength/2)+1;
        for(int i=0; i<WordList.size();i++)
        {
            String word = WordList.get(i);
            char[] word1Array = word.toCharArray();
            int matchedWords = 0;
            if((i+1)<WordList.size())
            {
                for(int j = i+1;j< WordList.size();j++)
                {
                    char[] word2Array = WordList.get(j).toCharArray();
                    int matches = 0;
                    for(int k = 0; k<WordLength;k++)
                    {
                        if(word1Array[k] == word2Array[k])
                            matches++;
                    }
                    if(matches >= minMatchedChars) {
                        matchedWords++;
                    }
                }
                if(matchedWords > maxMatchedWords)
                {
                    wordToUse = word;
                    maxMatchedWords = matchedWords;
                }
            }

        }
        return wordToUse;
    }

    /**
     * Removes the words that are not possible from the list.
     * A word is deemed not possible if the number of same characters with the word before
     * is more or less than the number of correctly matched characters from the word before
     *
     * Example:
     *
     * Possible words are:
     * Hello, Norma, Nello, Mallu
     *
     * Hello was put into the terminal and it had 4 correct characters
     *
     * Norma is not possible, because none of it characters matches Hello
     *
     * Nello is possible because 4 characters match Hello
     *
     * Mellu is not possible, because it matches less characters (3) with Hello than Hello with the
     * solution
     *
     */
    public void removeNotPossible()
    {
        char[] lastEnteredWordChars = LastEnteredWord.toCharArray();
        List<String> newList = new ArrayList<String>(WordList);
        for(String word: WordList)
        {
            int matches = 0;
            char[] wordChars = word.toCharArray();
            for(int i = 0; i<WordLength;i++)
            {
                if(lastEnteredWordChars[i] == wordChars[i])
                    matches++;
            }
            if(matches < LastCorrectChars || matches > LastCorrectChars)
            {
                newList.remove(word);
            }

        }
        WordList = new ArrayList<String>(newList);
    }

    /**
     * Refreshes the displayed list of possible words to match the actual list
     */
    private void refreshWordListView() {
        TextView possibleWords = (TextView)findViewById(R.id.word_list);
        String stringToSet = "";
        for(String word: WordList)
        {
            stringToSet += word+", ";
        }
        possibleWords.setText(stringToSet);
    }


}
