package com.exemple.chatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    Button sendButton;
    EditText userInput;
    RecyclerView chatWindow;
    MessageController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.sendButton);
        userInput = findViewById(R.id.userInput);
        chatWindow = findViewById(R.id.chatWindow);

        controller = new MessageController();
        controller.setIncomingLayout(R.layout.incoming_message);
        controller.setOutgoingLayout(R.layout.outgoing_message);
        controller.setMessageTextId(R.id.messageText);
        controller.setUserNameId(R.id.userName);
        controller.setMessageTimeId(R.id.messageDate);
        controller.appendTo(chatWindow, this);

        controller.addMessage(
                new MessageController.Message("Всем привет. Добро пожаловать в приватный чат", "Васян", false)
        );
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String text =  userInput.getText().toString();
                controller.addMessage(
                        new MessageController.Message(text, "Иван", true)
                );
                userInput.setText("");
            }
        });
    }
}
