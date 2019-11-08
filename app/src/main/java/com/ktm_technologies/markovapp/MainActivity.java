package com.ktm_technologies.markovapp;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ktm_technologies.markov.DotWriter;
import com.ktm_technologies.markov.MarkovChain;
import com.ktm_technologies.markov.Result;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MarkovChain markovChain;
    Log log;
    List<String> phrase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        SpeechInput();
        markovChain=new MarkovChain(1);
        learnIt();
        DotWriter writer = new DotWriter("FooBarBaz", new PrintStream(System.out));
        try {
            markovChain.traverse(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void learnIt(){
        phrase = new LinkedList<>(Arrays.asList("starte das Dashboard Menü und zeige die Motorkontrollleuchte an".split( " ")));
        markovChain.train(phrase);
        phrase = new LinkedList<>(Arrays.asList("zeige die Motorkontrollleuchte im Dashboard an".split( " ")));
        markovChain.train(phrase);
        phrase = new LinkedList<>(Arrays.asList("öffne das Dashboard Menü".split( " ")));
        markovChain.train(phrase);
        phrase = new LinkedList<>(Arrays.asList("öffne das Fenster des Dashboards".split( " ")));
        markovChain.train(phrase);
        phrase = new LinkedList<>(Arrays.asList("starte die Navigation im Simulationsmodus".split( " ")));
        markovChain.train(phrase);
        phrase = new LinkedList<>(Arrays.asList("simuliere die Navigation".split( " ")));
        markovChain.train(phrase);

    }

    /**
     * Debugging
     *
     * starte die Navigation und zeige im Dashboard menü zeige die Motorkontrollleuchte an
     */
    public void SpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_RESULTS,RESULT_CANCELED);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE");
        //   intent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE,true);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, 10);

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                log.i("res",result.get(0));
                String[] resulte = result.get(0).split(" ");
                List<String> phrase = new LinkedList<>(Arrays.asList(resulte));
                double result1 = markovChain.scan(phrase,new Result());
                log.i("adsd",String.valueOf(result1));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}