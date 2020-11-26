package id.fajarpw.socketclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Thread thread = null;
    EditText etIP, etPort, etMessages;
    TextView tvMessages;
    Button btnSend, btnConnect;
    String SERVER_IP;
    int SERVER_PORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMessages = findViewById(R.id.txtStatus);
        etIP = findViewById(R.id.editTextIP);
        etPort = findViewById(R.id.editTextPort);
        etMessages = findViewById(R.id.editTextPesan);
        btnSend = findViewById(R.id.buttonKirim);
        btnConnect = findViewById(R.id.buttonConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvMessages.setText("");
                SERVER_IP = etIP.getText().toString().trim();
                SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
                thread = new Thread(new Thread1());
                thread.start();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessages.getText().toString().trim();
                if(!message.isEmpty()){
                    new Thread(new Thread3(message)).start();
                }
            }
        });

    }

    private PrintWriter output;
    private BufferedReader input;

    class Thread1 implements Runnable{
        Socket socket;
        @Override
        public void run() {
            try{
                socket = new Socket(SERVER_IP,SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Connected \n");
                    }
                });
                new Thread(new Thread2()).start();
            }catch (IOException exception){
                exception.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable{
        @Override
        public void run() {
            while (true){
                try {
                    final String message = input.readLine();
                    if(message!=null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvMessages.append("Server: " + message + "\n");
                            }
                        });
                    }else {
                        thread = new Thread(new Thread1());
                        thread.start();
                        return;
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    class Thread3 implements Runnable{
        private String message;
        Thread3(String message){
            this.message=message;
        }
        @Override
        public void run() {
            output.write(message);
            output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append("Client: " + message + "\n");
                    etMessages.setText("");
                }
            });
        }
    }

}