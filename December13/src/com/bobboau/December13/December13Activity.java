package com.bobboau.December13;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class December13Activity extends Activity {
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	
	Calculation calc = null;
	
	Intent speech_intent = null;
	
	int active_operand = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
		PackageManager packageManager = getPackageManager();
		speech_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		List<ResolveInfo> activities = packageManager.queryIntentActivities(speech_intent, 0);

		if(activities.size() < 1)
		{
			Toast.makeText(this, "no speech recognition available, please try this on a real phone", 4000).show();
			findViewById(R.id.output).postDelayed(new Runnable() {
				@Override
				public void run() {		
					December13Activity.this.finish();
				}
			}, 3000);
			speech_intent = null;
		}
		else
		{
			Toast.makeText(this, "select a calculation from the options menu", 3000).show();
		}

    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final boolean b = super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.calculate_menu, menu);
		return b;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(speech_intent == null)
		{
			return false;
		}
		switch (item.getItemId()) {
			case R.id.menu_addition :
				calc = new Calculation(){
					{
						init(new String[]{"first number", "second number"});
					}
					@Override
					public String getResult() {
						return operands[0].value + " + " + operands[1].value + "=\n" + 
								(operands[0].value + operands[1].value);
					}
				};
				Toast.makeText(this, "+", 3000).show();
			break;
			case R.id.menu_subtraction :
				calc = new Calculation(){
					{
						init(new String[]{"first number", "second number"});
					}
					@Override
					public String getResult() {
						return operands[0].value + " - " + operands[1].value + "=\n" + 
								(operands[0].value - operands[1].value);
					}
				};
				Toast.makeText(this, "-", 3000).show();
			break;
			case R.id.menu_division :
				calc = new Calculation(){
					{
						init(new String[]{"first number", "second number"});
					}
					@Override
					public String getResult() {
						return operands[0].value + " / " + operands[1].value + "=\n" + 
								(operands[0].value / operands[1].value);
					}
				};
				Toast.makeText(this, "/", 3000).show();
			break;
			case R.id.menu_multiplication :
				calc = new Calculation(){
					{
						init(new String[]{"first number", "second number"});
					}
					@Override
					public String getResult() {
						return operands[0].value + " * " + operands[1].value + "=\n" + 
								(operands[0].value * operands[1].value);
					}
				};
				Toast.makeText(this, "*", 3000).show();
			break;
			case R.id.menu_quadratic :
				calc = new Calculation(){
					{
						init(new String[]{"a", "b", "c"});
					}
					@Override
					public String getResult() {
						double a = operands[0].value;
						double b = operands[1].value;
						double c = operands[2].value;
										
						double first =  (-b-Math.sqrt((b*b)-(4.0 * a * c)))/(2.0f * a);
						double second = (-b+Math.sqrt((b*b)-(4.0 * a * c)))/(2.0f * a);
						return "(-b+-sqrt(b^2-4ac))/(2a) = \n" + 
							first+":"+second;
					}
				};
			break;
			case R.id.menu_Intrest :
				calc = new Calculation(){
					{
						init(new String[]{"principal", "annual interest rate", "years"});
					}
					@Override
					public String getResult() {
						double p = operands[0].value;
						double r = operands[1].value;
						double t = operands[2].value;
										
						return "PE^rt = \n" + 
							p*Math.exp(r*t);
					}
				};
			break;
			default:
				return super.onOptionsItemSelected(item);
		}
		
		//start the chain reaction
		startSpeechAction(0);
		
		return true;
	}
	
	private void startSpeechAction(int operand)
	{
		if(calc == null)
		{
			return;
		}
		
		if(calc.opperandCount() <= operand)
		{
			showResult();
			return;
		}

		active_operand = operand;
		speech_intent.putExtra(RecognizerIntent.EXTRA_PROMPT, calc.opperandDescription(operand));
		speech_intent.putExtra(
			RecognizerIntent.EXTRA_LANGUAGE_MODEL,
			RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
		);
		speech_intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		startActivityForResult(speech_intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> list = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			if(list.size() < 1)
			{
				Toast.makeText(this, "Sorry, I couldn't make that out, please try again", 3000).show();
				startSpeechAction(active_operand);
				return;
			}
			
			String return_string = list.get(0);
			
			Pattern pattern = Pattern.compile("(negative[\\s]*|minus[\\s]*)");
			Matcher matcher = pattern.matcher(return_string);
			return_string = matcher.replaceAll("-");
			
			pattern = Pattern.compile("([\\s]*point[\\s]*)");
			matcher = pattern.matcher(return_string);
			return_string = matcher.replaceAll(".");
			
			try
			{
				calc.setOpperandvalue(active_operand, Float.parseFloat(return_string));
			}
			catch(NumberFormatException e)
			{
				Toast.makeText(this, "Sorry, I couldn't make that out\nI heard '"+list.get(0)+"'\nplease try again", 3000).show();
				startSpeechAction(active_operand);
				return;
			}
			
			int next_operand = active_operand+1;
			if(calc.opperandCount() <= next_operand)
			{
				showResult();
				return;
			}
			else
			{
				startSpeechAction(next_operand);
			}
		}
	}
	
	private void showResult()
	{
		((TextView)findViewById(R.id.output)).setText(
				calc.getResult()
		);
	}
}