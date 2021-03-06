package english.grammar.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import english.grammar.app.Customization.AutoResizeTextView;
import english.grammar.app.Manager.Conversation_DBManager;
import english.grammar.app.Models.ExampleModel;
import english.grammar.app.SharedData.SharedClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PresentContinuousTense extends AppCompatActivity {

    private AdView mAdView;
    private TextToSpeech textToSpeech;
    List<ExampleModel> exampleData = new ArrayList<>();
    ImageButton backBtn, defbtn, methodbtn;
    Conversation_DBManager dbManager;
    TextView engDeftxt,  methodEnglish;
    ListView exampleLV;
    ExampleAdapter exampleAdapter;
    Typeface alvi_Nastaleeq_Lahori, montserrat_reg, montserrat_semibold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_continuous_tense);

        MobileAds.initialize(this, getResources().getString(R.string.admob__app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        defbtn=findViewById(R.id.defbtn);
        methodbtn=findViewById(R.id.methodbtn);

        initExample();
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Log.d("msg", "failed");
                }
            }
        });

        montserrat_semibold = Typeface.createFromAsset(this.getAssets(), "fonts/Montserrat-SemiBold.ttf");
        montserrat_reg = Typeface.createFromAsset(this.getAssets(), "fonts/Montserrat-Regular.ttf");
        alvi_Nastaleeq_Lahori = Typeface.createFromAsset(this.getAssets(), "fonts/alvi_Nastaleeq_Lahori.ttf");
        dbManager = new Conversation_DBManager(this);
        dbManager.open();


        methodEnglish = findViewById(R.id.methodEnglish);
        engDeftxt = findViewById(R.id.engDeftxt);

        backBtn = findViewById(R.id.backBtn);
        exampleLV = findViewById(R.id.exampleLV);
        exampleAdapter = new ExampleAdapter();
        exampleLV.setAdapter(exampleAdapter);

        engDeftxt.setText(dbManager.getTenses(SharedClass.tense_id).get(0).getDef_eng().replace("\\n", "\n"));

        methodEnglish.setText(dbManager.getTenses(SharedClass.tense_id).get(0).getMethod_eng().replace("\\n", "\n"));

        methodEnglish.setTypeface(montserrat_reg);
        engDeftxt.setTypeface(montserrat_reg);

        defbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(engDeftxt.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        methodbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(methodEnglish.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PresentTense.class));
                finish();
            }
        });

    }

    private void initExample() {
        ExampleModel exampleModel = new ExampleModel("Example");
        exampleData.add(exampleModel);
        ExampleModel exampleModel1 = new ExampleModel("Example");
        exampleData.add(exampleModel1);
        ExampleModel exampleModel2 = new ExampleModel("Example");
        exampleData.add(exampleModel2);
        ExampleModel exampleModel3 = new ExampleModel("Example");
        exampleData.add(exampleModel3);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), PresentTense.class));
        finish();
    }

    //  WHEN ACTIVITY IS PAUSED
    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
    }

    //  WHEN ACTIVITY IS DESTROYED
    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public class ExampleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dbManager.getTenseExample(SharedClass.tense_id).size();
        }

        @Override
        public Object getItem(int i) {
            return dbManager.getTenseExample(SharedClass.tense_id).get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View rowView = View.inflate(PresentContinuousTense.this, R.layout.example_cell, null);

            AutoResizeTextView exampleTxt = rowView.findViewById(R.id.exampleTxt);
            AutoResizeTextView meaningTxt = rowView.findViewById(R.id.meaningTxt);
            ImageButton volumeBtn = rowView.findViewById(R.id.volumeBtn);

            exampleTxt.setText(dbManager.getTenseExample(SharedClass.tense_id).get(i).getEnglishexample());


            volumeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textToSpeech.speak(dbManager.getTenseExample(SharedClass.tense_id).get(i).getEnglishexample(), TextToSpeech.QUEUE_FLUSH, null);
                }
            });

            return rowView;
        }
    }

    public void favbtn(View view){
        startActivity(new Intent(getApplicationContext(), FavouriteMessages.class));
    }

}
