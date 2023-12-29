package com.example.myapp;

import static android.content.ContentValues.TAG;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatClient {

    public static final int CONNECTED = 1;//Connection completely established
    public static final int NOT_AGAIN_CONNECTED = 2;//Connection process is ongoing
    public static final int DES_CONNECTED_FROM_OTHER = 3;//Error, no more internet connection, etc.
    public static final int DES_CONNECTED_FROM_APP = 4;//application explicitly ask for shut down the connection
    private static final String COMMAND_CONNECT = "CONNECT";
    private static final String COMMAND_DISCONNECT = "DISCONNECT";

    private static final String COMMAND_SEND = "SEND";

    private static final String ACCEPT_VERSION_NAME = "accept-version";
    private static final String ACCEPT_VERSION = "1.1,1.0";
    private static final String COMMAND_SUBSCRIBE = "SUBSCRIBE";
    private static final String COMMAND_UNSUBSCRIBE = "UNSUBSCRIBE";
    private static final String SUBSCRIPTION_ID = "id";
    private static final String SUBSCRIPTION_DESTINATION = "destination";
    private final ChatClientListener listener;
    private int connection;
    private int maxWebSocketSize;
    WebSocket webSocketClient;
    private String id;

    public interface ChatClientListener {
        void onMessageReceived(String message);
    }

    public ChatClient(ChatClientListener listener) {
       this.listener = listener;
    }

    public void connectEstablish(String uuid) {
        id = uuid;
        connection = NOT_AGAIN_CONNECTED;
        maxWebSocketSize = 16 * 1024;;
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();
        String baseURL = "ws://64.227.145.37:8090/websocket/websocket";
        Request request = new Request.Builder()
                .url(baseURL)
                .build();
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, ">>> Connection has been closed ");
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, ">>> Connection getting termination  ");
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.d(TAG, ">>> onFailure ");
                connection = DES_CONNECTED_FROM_OTHER;
                super.onFailure(webSocket, t, response);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, ">>> Message received from server"+text);
                super.onMessage(webSocket, text);
                listener.onMessageReceived(text);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                Log.d(TAG, ">>> Message received from server " );
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d(TAG, ">>> Connection established successfully" );
                super.onOpen(webSocket, response);
                connection = CONNECTED;
                webSocketClient=webSocket;
                Map<String,String> headers = new HashMap<>();
                headers.put(ACCEPT_VERSION_NAME, ACCEPT_VERSION);
                transmit(COMMAND_CONNECT, headers, null);
                subscribe(id);
            }
        });
    }

    public void disConnect(String id){
        unsubscribe(id);
        if(this.connection == CONNECTED){
            this.connection = DES_CONNECTED_FROM_APP;
            transmit(COMMAND_DISCONNECT, null, null);
        }
    }

    public void accept(String body){
        if(this.connection == CONNECTED){
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(SUBSCRIPTION_DESTINATION, "/app/establishConnectionChat");
            transmit(COMMAND_SEND, headers, body);
        }
    }

    public  void reject(String body){
        if(this.connection == CONNECTED){
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(SUBSCRIPTION_DESTINATION, "/app/establishConnectionChat");
            transmit(COMMAND_SEND, headers, body);
        }
    }

    /**
     *
     */
    private void subscribe(String id){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(SUBSCRIPTION_ID, id);
        headers.put(SUBSCRIPTION_DESTINATION, "/user/"+id+"/queue/messages");
        transmit(COMMAND_SUBSCRIBE, headers, null);
    }

    public void unsubscribe(String id){
        if(this.connection == CONNECTED){
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(SUBSCRIPTION_ID, id);
            transmit(COMMAND_UNSUBSCRIBE, headers, null);
        }
    }

    /**
     *
     * @param command
     * @param headers
     * @param body
     */
    private void transmit(String command, Map<String, String> headers, String body){
        String message = generateCommand(command, headers, body);
        Log.d(TAG, ">>> " + message);
        while (true) {
            if (message.length() > this.maxWebSocketSize) {
                webSocketClient.send(message.substring(0, this.maxWebSocketSize));
                message = message.substring(this.maxWebSocketSize);
            } else {
                webSocketClient.send(message);
                break;
            }
        }
    }


    /**
     *
     * @param command
     * @param headers
     * @param body
     * @return
     */
    private String generateCommand(String command, Map<String, String> headers, String body){
        StringBuilder result  = new StringBuilder();
        result.append(command);
        result.append("\n");
        for(String key : headers.keySet()){
            result.append(key);
            result.append(":");
            result.append(headers.get(key));
            result.append("\n");
        }
        result.append("\n");
        if(body == null) {
            result.append("");
        }else {
            result.append(body);
        }
        result.append("\0");
        return result.toString();
    }
}
