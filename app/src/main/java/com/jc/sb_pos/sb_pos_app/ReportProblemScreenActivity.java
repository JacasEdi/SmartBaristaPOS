package com.jc.sb_pos.sb_pos_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class ReportProblemScreenActivity extends AppCompatActivity
{
    private Button submitBtn;
    private MultiAutoCompleteTextView txtDescription;
    private AutoCompleteTextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem_screen);

        submitBtn = (Button) findViewById(R.id.btn_submit);
        txtDescription = (MultiAutoCompleteTextView) findViewById(R.id.tv_problem_description);
        txtTitle = (AutoCompleteTextView) findViewById(R.id.tv_problem_title);


        submitBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submitReport();
            }
        });

    }

    private void submitReport()
    {
        String description = txtDescription.getText().toString();
        String title = txtTitle.getText().toString();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"jacek.budzynski.92@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, title);
        email.putExtra(Intent.EXTRA_TEXT, description);
        try
        {
            startActivity(Intent.createChooser(email, "Send e-mail using"));
        }
        catch (android.content.ActivityNotFoundException e)
        {
            Toast.makeText(ReportProblemScreenActivity.this, "No suitable e-mail client" +
                    "could be found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(ReportProblemScreenActivity.this, MainPosScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
