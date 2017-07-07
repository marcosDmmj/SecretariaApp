package net.simplifiedcoding.secretariaApp.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.simplifiedcoding.insertintomysql.R;
import net.simplifiedcoding.secretariaApp.calendario.Util;
import net.simplifiedcoding.secretariaApp.classesBasicas.OnTaskComplete;
import net.simplifiedcoding.secretariaApp.webservice.UploadNewEvent;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class AddEventActivity extends AppCompatActivity {
    private TextView currentDate, currentStartTime, currentEndTime;
    private EditText nome, email, titulo;
    private Button addEvento;
    private int countTitulo, countNome, countEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Intent intent = getIntent();
        String date = intent.getStringExtra("Date");
        titulo = (EditText)findViewById(R.id.editTxtTitulo);
        nome = (EditText)findViewById(R.id.editTxtNome);
        email = (EditText)findViewById(R.id.editTxtEmail);
        currentStartTime = (TextView)findViewById(R.id.txtHoraInicio);
        currentEndTime = (TextView)findViewById(R.id.txtHoraFim);
        currentDate = (TextView)findViewById(R.id.display_current_date);
        currentDate.setText(Util.stringToStringDayWeek(date));
        addEvento = (Button) findViewById(R.id.btnAddEvento);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countEmail = count;
                if ((countTitulo > 0)&&(countEmail > 0)&&(countNome > 0)){
                    addEvento.setEnabled(true);
                } else {
                    addEvento.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countNome = count;
                if ((countTitulo > 0)&&(countEmail > 0)&&(countNome > 0)){
                    addEvento.setEnabled(true);
                } else {
                    addEvento.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        titulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countTitulo = count;
                if ((countTitulo > 0)&&(countEmail > 0)&&(countNome > 0)){
                    addEvento.setEnabled(true);
                } else {
                    addEvento.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void startHourClicked(View view) {
        DialogFragment startTimePicker = new TimePickerFragmentStart(currentStartTime.getText());
        startTimePicker.show(getFragmentManager(),"TimePicker");
    }

    public void endHourClicked(View view) {
        DialogFragment endTimePicker = new TimePickerFragmentEnd(currentEndTime.getText());
        endTimePicker.show(getFragmentManager(),"TimePicker");
    }

    public void addNewEvent(View view) {
        Date startHourComplete = Util.stringHourToDate(currentStartTime.getText().toString());
        Date endHourComplete = Util.stringHourToDate(currentEndTime.getText().toString());
        boolean ok = Util.checkHour(Util.dateToHour(startHourComplete), Util.dateToMinute(startHourComplete),
                Util.dateToHour(endHourComplete), Util.dateToMinute(endHourComplete));
        if(ok) {
                new UploadNewEvent(this, new OnTaskComplete() {
                    @Override
                    public void onTaskComplete(Integer status) {
                        if (status == 1) {
                            Toast.makeText(getBaseContext(), "Evento enviado para o professor! Aguarde email de confirmação de agendamento", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Erro ao enviar evento!", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).execute(Util.replaceSpaceWS(titulo.getText().toString()),
                        Util.stringToStringWSComplete(currentDate.getText().toString(), currentStartTime.getText().toString()),
                        Util.stringToStringWSComplete(currentDate.getText().toString(), currentEndTime.getText().toString()),
                        Util.replaceSpaceWS(email.getText().toString()),
                        Util.replaceSpaceWS(nome.getText().toString()));
        } else {
            Toast.makeText(this, "Favor informar horário fim maior que horário início!", Toast.LENGTH_SHORT).show();
        }
    }


}
