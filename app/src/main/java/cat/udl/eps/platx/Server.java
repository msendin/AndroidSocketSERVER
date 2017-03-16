package cat.udl.eps.platx;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Activity {

    private ServerSocket serverSocket;

    Handler updateConversationHandler;

    Thread serverThread = null;

    private TextView text;



    public static final int SERVERPORT = 6000;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        text = (TextView) findViewById(R.id.text2);

        updateConversationHandler = new Handler();

        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerThread implements Runnable {

        private BufferedReader input;
        private BufferedWriter output;

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            String read = input.readLine();
                            updateConversationHandler.post(new updateUIThread(read));
                            output.write(read + "\n", 0, read.length() + 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class updateUIThread implements Runnable {
        private String msg;

        private updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            text.append("ECHOING the message in Server" + "\n");
            text.append(text.getText().toString()+"Client Says: "+ msg + "\n");
        }

    }

}
