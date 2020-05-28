package com.exemple.chatapp;

import android.util.Log;
import android.util.Pair;

import androidx.core.util.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    Map<Long, String> names = new ConcurrentHashMap<>();
    WebSocketClient client;
    private Consumer<Pair<String, String>> onMessageReceived;

    public Server(Consumer<Pair<String, String>> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void connect() {
        URI address;
        try {
            address = new URI("ws://35.214.3.133:8881");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        client = new WebSocketClient(address) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("SERVER", "Connection to server is open");

            }

            @Override
            public void onMessage(String message) {
                Log.i("SERVER", "Get message from server: " + message);
                int type = Protocol.getType(message);
                if (type == Protocol.USER_STATUS) {
                    userStatusChanged(message);
                }
                if (type == Protocol.MESSAGE) {
                    displayIncomingMessage(message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("SERVER", "Connection closed: ");
            }

            @Override
            public void onError(Exception ex) {
                Log.i("SERVER", "ERROR occurred " + ex.getMessage());
            }
        };
        client.connect();
    }

    private void displayIncomingMessage(String json) {
        Protocol.Message m = Protocol.unpackMessage(json);
        String name = names.get(m.getSender());
        if (name == null) {
            name = "No name";
        }
        String text = m.getEncodedText();
        try {
            text = Crypto.decrypt(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onMessageReceived.accept(
                new Pair<String, String>(name, text)
        );
    }

    private void userStatusChanged(String json) {
        Protocol.UserStatus s = Protocol.unpackStatus(json);
        if (s.isConnected()) {
            names.put(s.getUser().getId(), s.getUser().getName());
        } else {
            names.remove(s.getUser().getId());
        }
    }

    public void sendMessage(String message) {
        if (client != null || !client.isOpen()) {
            return;
        }
        try {
            message = Crypto.encrypt(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Protocol.Message m = new Protocol.Message("Привет!");
        m.setReceiver(Protocol.GROUP_CHAT);
        String pM = Protocol.packMessage(m);
        Log.i("SERVER", "Send message: " + pM);
        client.send(pM);
    }

    public void sendUserName(String name) {
        String myName = Protocol.packName(new Protocol.UserName("Ванек"));
        Log.i("SERVER", "Sending my name to server: " + myName);
        client.send(myName);
    }


}
