package com.ktm_technologies.markovapp;


import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ktm_technologies.nlcmd.DotWriter;
import com.ktm_technologies.nlcmd.MarkovChain;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button but;
    private ArrayList<MarkovChain> markovChain;
    Log log;
    static int k;
    List<String> phrase;
    StringBuilder[] Doers;
    TextView tvPhrase;
    TextView tvProb;
    TextView tvErk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        k=0;
        setContentView(R.layout.mainview);
        but=findViewById(R.id.button2);
        markovChain=new ArrayList<>();
        markovChain.add(new MarkovChain(1));
        markovChain.add(new MarkovChain(1));
        learnIt();
        DotWriter writer = new DotWriter("FooBarBaz", new PrintStream(System.out));
        try {
            for(int l=0;l<markovChain.size();l++) {
                markovChain.get(l).traverse(writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeechInput();
            }
        });
        tvPhrase=findViewById(R.id.phrase);
        tvProb=findViewById(R.id.prob);
        tvErk=findViewById(R.id.erkannt);
    }
    protected void onResume() {
        super.onResume();
    }

    public void learnIt(){
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("starte das Dashboard Menü und zeige die Motorkontrollleuchte an")).split( " ")));
        markovChain.get(0).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("zeige die Motorkontrollleuchte im Dashboard an")).split( " ")));
        markovChain.get(0).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("öffne das Dashboard Menü um die Motorkontrollleuchte anzuzeigen")).split( " ")));
        markovChain.get(0).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("öffne das Fenster des Dashboards damit ich die Motorkontrollleuchte sehen kann")).split( " ")));
        markovChain.get(0).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("öffne das Fenster im Dashboards sodass ich die Motorkontrollleuchte sehen kann")).split( " ")));
        markovChain.get(0).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("öffne das Dashboard um die Motorkontrollleuchte anzuzeigen")).split( " ")));
        markovChain.get(0).train(phrase);

        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("starte die Navigation im Simulationsmodus")).split( " ")));
        markovChain.get(1).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("simuliere die Navigation")).split( " ")));
        markovChain.get(1).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("starte eine Navigationssimulation")).split( " ")));
        markovChain.get(1).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("simuliere das Navigieren")).split( " ")));
        markovChain.get(1).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("Simulation einschalten")).split( " ")));
        markovChain.get(1).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("starte die Simulation")).split( " ")));
        markovChain.get(1).train(phrase);
        phrase = new LinkedList<>(Arrays.asList(replaceMissSpelling(killFillWords("starte Simulation")).split( " ")));
        markovChain.get(1).train(phrase);
    }

    public String killFillWords(String input){
        ArrayList<String> killWords=new ArrayList<>();
        killWords.add(" und");
        //killWords.add("der");
        //killWords.add("die");
        //killWords.add("das");

        killWords.add(" einer");
        killWords.add(" eine");
        killWords.add(" ein");
        killWords.add(" einem");
        killWords.add(" eines");
        killWords.add(" um");
        killWords.add(" auch");
        killWords.add(" kann");
        killWords.add(" im");
        killWords.add(" in");
        killWords.add(" des");
        killWords.add(" damit");
        //killWords.add("an");
        //killWords.add("am");
        killWords.add(" ähm");
        for(int o=0;o<killWords.size();o++){
            input=input.replace(killWords.get(o),"");
        }
        return input;
    }
    public String replaceMissSpelling(String input){
        input=input.replace("Menü","menu");
        return input;
    }

    /**
     * Debugging
     *
     * starte die Navigation und zeige im Dashboard menü zeige die Motorkontrollleuchte an
     */
    public void SpeechInput(){
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
                tvErk.setText(result.get(0));
                String reso=killFillWords(result.get(0));

                ArrayList<Integer> indezes=new ArrayList<>();
                int n=0;
                while(reso.indexOf("die",n)!=-1||reso.indexOf("der",n)!=-1||reso.indexOf("das",n)!=-1){
                    int indDie=reso.indexOf("die",n);
                    int indDer=reso.indexOf("der",n);
                    int indDas=reso.indexOf("das",n);
                    int lowest=9999;
                    if(indDie>=0)
                        lowest=indDie;
                    if(lowest>indDer&&indDer>=0)
                        lowest=indDer;
                    if(lowest>indDas&&indDas>=0)
                        lowest=indDas;
                    indezes.add(lowest);
                    n=lowest+1;
                }
                Doers=new StringBuilder[(int)Math.pow(3,indezes.size())];
                for(int i=0;i<(int)Math.pow(3,indezes.size());i++){
                    Doers[i]=new StringBuilder(result.get(0));
                }
                String[] fins=new String[(int)Math.pow(3,indezes.size())];

                changeDerDieDas(0,indezes.size(),0,indezes);

                double highest=0;
                String phrases="";
                int chain=9999;
/* TODO move to new API
                for(int p=0;p<markovChain.size();p++){
                    log.i("MarkovChain","Start with MarkovChain "+ p);
                    for(int x=0;x<(int)Math.pow(3,indezes.size());x++){
                        log.i("phrasis",Doers[x].toString());
                        String[] resulte = Doers[x].toString().split(" ");
                        List<String> phrase = new LinkedList<>(Arrays.asList(resulte));
                        Result ressi=new Result();
                        double result1 = markovChain.get(p).scan(phrase,ressi);
                        if(result1>highest){
                            try {
                                phrases=Arrays.toString(ressi.getPhrase().toArray());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            highest=result1*phrases.length();
                            chain=p;
                    }
                    log.i("HitWahrscheinlichkeit",String.valueOf(result1));
                    try{
                  log.i("passendePhrase",Arrays.toString(ressi.getPhrase().toArray()));
                    }
                    catch (Exception e){

                    }

                }}
 */
                tvPhrase.setText(phrases);
                tvProb.setText(highest*100+" %\nMatched on Chain " + chain);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void changeDerDieDas(int start, int lengthOfRestList,int posInIndexList, ArrayList<Integer> posis){
        if(lengthOfRestList==0)
        {
            return;
        }
        for(k=start;k<Math.pow(3,(lengthOfRestList-1))+start;k++){
            Doers[k].replace(posis.get(posInIndexList),posis.get(posInIndexList) + 3, "der");
                }
        changeDerDieDas(start,lengthOfRestList-1,posInIndexList+1, posis);
        start=k;
        for(k=start;k<Math.pow(3,(lengthOfRestList-1))+start;k++){
            Doers[k].replace(posis.get(posInIndexList),posis.get(posInIndexList) + 3, "die");
                }
        changeDerDieDas(start,lengthOfRestList-1,posInIndexList+1, posis);
        start=k;
        for(k=start;k<Math.pow(3,(lengthOfRestList-1))+start;k++){
            Doers[k].replace(posis.get(posInIndexList),posis.get(posInIndexList) + 3, "das");
                }
        changeDerDieDas(start,lengthOfRestList-1,posInIndexList+1, posis);
    }}
