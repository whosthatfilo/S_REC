package com.example.speechrecognition;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    //views from activity
    TextView mTextTv;
    TextView mTextResponses;
    ImageButton mVoiceBtn;
    List<String> wordBank = new ArrayList<>();
    List<String> responses = new ArrayList<>();

    EditText Et1, Et3;
    Button Btn;
    TextView tv;

    boolean word = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextTv = (TextView) findViewById(R.id.textTv);
        mTextResponses = (TextView) findViewById(R.id.textTv);
        mVoiceBtn = (ImageButton) findViewById(R.id.voiceBtn);

        Et1 = (EditText)findViewById(R.id.et1);
        Et3 = (EditText)findViewById(R.id.et3);
        Btn = (Button)findViewById(R.id.btn);
        tv = (TextView)findViewById(R.id.text_view);

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        Python py = Python.getInstance();
        final PyObject pyobj = py.getModule("test_python"); //here we will give name of our python file

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //function name                           //first argument         //second argument
                PyObject obj = pyobj.callAttr("main",Et1.getText().toString(),Et3.getText().toString());

                //now obj will continue our result, so set its result to textView
                tv.setText(obj.toString());

            }
        });

        //create array list and connect it to xml file
        wordBank = Arrays.asList(getResources().getStringArray(R.array.Words));
        responses = Arrays.asList(getResources().getStringArray(R.array.responses));

        //button click to show speech to text dialog
        mVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }

    public void speak() {
        //intent to show speech to text dialog
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi say something");

        //start intent
        try {
            //in there was no error
            //show dialog
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            //if there was some error
            //get message of error and show
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //this is the counter for the responses loop
        int responseItem =0;
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            ArrayList<String> result = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mTextTv.setText(result.get(0).toUpperCase());
            //loops every word in a phrase until it keyword from wordBank
            for(String wordItem: wordBank) {
                if (result.get(0).contains(wordItem)) {
                    mTextResponses.setText(responses.get(responseItem).toString());
                    break;
                }
                //item +1
                //this moves to the next word, and it will check if there is key word in word item
                responseItem++;
            }
        }
    }
}