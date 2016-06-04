package fyp.iems;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView currentTemperatureField, weatherIcon, cityField, updatedField;
    Typeface weatherFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().hide();

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "Fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);


        Functions.placeIdTask asyncTask =new Functions.placeIdTask(new Functions.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                currentTemperatureField.setText(weather_temperature);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });
        asyncTask.execute("33.7139", "73.0247"); //  asyncTask.execute("Latitude", "Longitude")



        /*RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        Button preButton = (Button) findViewById(R.id.preButton);
        Button postButton = (Button) findViewById(R.id.postButton);

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPrePost = "0";
                Intent intent = new Intent(MainActivity.this, OperateACActivity.class);
                intent.putExtra("IntraActivity",CheckPrePost);
                startActivity(intent);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                Intent intent = new Intent(MainActivity.this, OperateACActivity.class);
                intent.putExtra("IntraActivity", CheckPrePost);
                startActivity(intent);
            }
        });*/
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioAC:
                if (checked) {
                    Intent intentAC = new Intent(MainActivity.this, OperateACActivity.class);
                    startActivity(intentAC);
                    break;
                }
            case R.id.radioHeater:
                if (checked) {
                    Intent intentHeater = new Intent(MainActivity.this, OperateHeaterActivity.class);
                    startActivity(intentHeater);
                    break;
                }
            case R.id.radioShutdown:
                if (checked) {
                    Intent intentShutdown = new Intent(MainActivity.this, ShutdownActivity.class);
                    startActivity(intentShutdown);
                    break;
                }
        }
    }
}
