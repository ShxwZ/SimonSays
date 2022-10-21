package com.example.simondice;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity  implements View.OnTouchListener{
    private TextView fallos;
    private List<String> colores = new ArrayList<>();
    private List<String> coloresTemp = new ArrayList<>();
    private int nivel = 1, nivelBlinked = 0;
    private List<Button> botones = new ArrayList<>();
    private List<Button> botonesTemp = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fallos = (TextView)findViewById(R.id.textView);
        fallos.setText("");
        findViewById(R.id.buttonRojo).setOnTouchListener(this);
        findViewById(R.id.buttonAmarillo).setOnTouchListener(this);
        findViewById(R.id.buttonVerde).setOnTouchListener(this);
        findViewById(R.id.buttonAzul).setOnTouchListener(this);
    }

    protected void reRunColors(View v,int delayBlink) {
        if (v == findViewById(R.id.buttonRojo))
            blinkButtonColor(R.id.buttonRojo, R.color.red, delayBlink, R.color.red_light);
        if (v == findViewById(R.id.buttonAmarillo))
            blinkButtonColor(R.id.buttonAmarillo, R.color.yellow, delayBlink, R.color.yellow_light);
        if (v == findViewById(R.id.buttonAzul))
            blinkButtonColor(R.id.buttonAzul, R.color.blue, delayBlink, R.color.blue_light);
        if (v == findViewById(R.id.buttonVerde))
            blinkButtonColor(R.id.buttonVerde, R.color.green, delayBlink, R.color.green_light);
    }

    private void blinkButtonColor(int button, int colorButton, int delayBlink, int colorLigthButton) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(button).setBackgroundColor(getResources().getColor(colorButton));
            }
        }, delayBlink);
        findViewById(button).setBackgroundColor(getResources().getColor(colorLigthButton));
    }

    protected void RandomColor(int delayBlink)  {
        Random random = new Random();
        int r = random.nextInt(5 - 1) + 1;
       switch (r) {
           case 1:
               addColorToListAndBlink(R.id.buttonRojo, R.color.red, delayBlink, R.color.red_light, "Rojo");
               break;
           case 2:
               addColorToListAndBlink(R.id.buttonAmarillo, R.color.yellow, delayBlink, R.color.yellow_light, "Amarillo");
               break;
           case 3:
               addColorToListAndBlink(R.id.buttonAzul, R.color.blue, delayBlink, R.color.blue_light, "Azul");
               break;
           case 4:
               addColorToListAndBlink(R.id.buttonVerde, R.color.green, delayBlink, R.color.green_light, "Verde");
               break;
       }
    }

    private void addColorToListAndBlink(int button, int colorNormal, int delayBlink, int colorClaro, String nombreColor) {
        blinkButtonColor(button, colorNormal, delayBlink, colorClaro);
        colores.add(nombreColor);
        botones.add(findViewById(button));
    }

    public void startColors(){
        buttonStatus(false);
        findViewById(R.id.buttonComenzar).setEnabled(false);
        for (int i = 1; i <= nivel; i++){
            if (i > botones.size()){
                new Handler().postDelayed(new Runnable() { @Override public void run() { RandomColor(nivelBlinked/2); } }, nivelBlinked*i);

            } else{
                int finalI = i;
                new Handler().postDelayed(new Runnable() { @Override public void run() { reRunColors(botones.get(finalI -1),nivelBlinked/2); } }, nivelBlinked*i);
            }
        }
        new Handler().postDelayed(new Runnable() { @Override public void run() {
            coloresTemp.addAll(colores);
            botonesTemp.addAll(botones);
            fallos.setText("Buena suerte :)");
            buttonStatus(true);
        } }, nivelBlinked*(nivel+1));
    }

    private void buttonStatus(boolean status) {
        findViewById(R.id.buttonRojo).setEnabled(status);
        findViewById(R.id.buttonRestart).setEnabled(status);
        findViewById(R.id.buttonAmarillo).setEnabled(status);
        findViewById(R.id.buttonVerde).setEnabled(status);
        findViewById(R.id.buttonAzul).setEnabled(status);
        findViewById(R.id.buttonUpLevel).setEnabled(status);
        findViewById(R.id.buttonDownLevel).setEnabled(status);
    }

    public void botonComenzar(View view){
        fallos.setText("");
        ((TextView)findViewById(R.id.textViewNivel)).setText("Nivel: " + nivel);
        nivelBlinked = (4000/nivel);
        startColors();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            v.setBackgroundColor(getResources().getColor(
                    v == findViewById(R.id.buttonAzul) ?
                            R.color.blue_light :
                            v == findViewById(R.id.buttonVerde) ?
                                    R.color.green_light :
                                    v == findViewById(R.id.buttonRojo) ?
                                            R.color.red_light :
                                            R.color.yellow_light
            ));
            checkWinOrLoose(v);
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            v.setBackgroundColor(getResources().getColor(
                    v == findViewById(R.id.buttonAzul) ?
                            R.color.blue :
                            v == findViewById(R.id.buttonVerde) ?
                                    R.color.green :
                                    v == findViewById(R.id.buttonRojo) ?
                                            R.color.red :
                                            R.color.yellow
            ));
        }
        return true;
    }

    private void checkWinOrLoose(View v) {
        if (!botonesTemp.isEmpty()){
            boolean loss = v != botonesTemp.get(0);
            if (v == botonesTemp.get(0)){
                coloresTemp.remove(0);
                botonesTemp.remove(0);
                fallos.setText(coloresTemp.isEmpty() ? "Has ganado" :"Buena suerte :)");
                findViewById(R.id.buttonComenzar).setEnabled(true);
                nivel += coloresTemp.isEmpty() ? 1 : 0;
                ((Button)findViewById(R.id.buttonComenzar)).setText(coloresTemp.isEmpty() ? "Continuar siguiente nivel" : "Reiniciar");
            }else if (loss){
                fallos.setText("Has perdido :(");
                resetAll();
            }
        }
    }

    public void resetAll(){
        coloresTemp.clear();
        botonesTemp.clear();
        colores.clear();
        botones.clear();
        ((Button)findViewById(R.id.buttonComenzar)).setText("Comenzar");
        buttonStatus(true);
        findViewById(R.id.buttonComenzar).setEnabled(true);
    }

    public void botonBajarNivel(View view) {
        nivel--;
        resetAll();
        ((TextView)findViewById(R.id.textViewNivel)).setText("Nivel: " + nivel);
    }

    public void botonSubirNivel(View view) {
        nivel++;
        resetAll();
        ((TextView)findViewById(R.id.textViewNivel)).setText("Nivel: " + nivel);
    }

    public void botonReiniciar(View view) {
        resetAll();
    }

    public void botonVerde(View view) {}
    public void botonRojo(View view) {}
    public void botonAzul(View view) {}
    public void botonAmarillo(View view) {}
}