package com.cm.hybridmessagingsdk.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Message {

    public enum State {
        Cancelled(0), Sent(1), Rejected(2), Accepted(3), Failed(4), Delivered(5);
        private int value;

        public int getValue() {
            return value;
        }

        private State(int value) {
            this.value = value;
        }
    }

    private String id;
    private String updateId;
    private String recipient;
    private String sender;
    private String body;
    private Date dateTime;
    private State state;

    private LinkedHashMap<String, String> extras;

    private static ArrayList<String> messageKeys;
    private static String jsonId        = "ID";
    private static String jsonRecipient = "Recipient";
    private static String jsonSender    = "Sender";
    private static String jsonBody      = "Body";
    private static String jsonDate      = "DateTime";
    private static String jsonStatus    = "Status";

    public static Message getMessageFromJson(JSONObject json) {
        String id = null;
        String recipient = null;
        String sender = null;
        String body = null;
        String date = null;
        String state = null;
        LinkedHashMap<String, String> extras = null;

        try {

            if(json.has(jsonId)) {
                id = json.getString(jsonId);
            }

            if(json.has(jsonRecipient)) {
                recipient = json.getString(jsonRecipient);
            }

            if(json.has(jsonSender)) {
                sender = json.getString(jsonSender);
            }

            if(json.has(jsonBody)) {
                body = json.getString(jsonBody);
            }

            if(json.has(jsonDate)) {
                date = json.getString(jsonDate);
            }

            if(json.has(jsonStatus)) {
                state = json.getString(jsonStatus);
            }

            // Check if there are other values than the one parsed like above
            extras = new LinkedHashMap<String, String>();
            ArrayList<String> extraKeys = getExtras(json);
            for(String key : extraKeys) {
                extras.put(key, json.getString(key));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Message(id, recipient, sender, body, date, state).setExtras(extras);
    }

    private static ArrayList<String> getExtras(JSONObject json) {

        ArrayList<String> extraKeys = new ArrayList<String>();

        // Create a list with all the default keys for Message
        if(messageKeys == null) {
            messageKeys = new ArrayList<String>();
            messageKeys.add(jsonId);
            messageKeys.add(jsonRecipient);
            messageKeys.add(jsonSender);
            messageKeys.add(jsonBody);
            messageKeys.add(jsonDate);
            messageKeys.add(jsonStatus);
        }

        // Get keys from json
        Iterator<String> keys = json.keys();

        // Loop trough all keys from JSONObject
        while(keys.hasNext()) {

            String key = keys.next();
            boolean keyExists = false;
            for(int i = 0; i < messageKeys.size(); i++) {
                // The key should not match with any known key for message
                if(key.equals(messageKeys.get(i))) {
                    keyExists = true;
                }
            }

            // If the key is unique save it.
            if(!keyExists) extraKeys.add(key);
        }

        return extraKeys;
    }

    public Message(String id, String recipient, String sender, String body, String dateTime, String state) {
        this.id = id;
        this.recipient = recipient;
        this.sender = sender;
        this.body = body;
        this.dateTime = stringToDate(dateTime);
        this.state = stringToState(state);
    }

    public State stringToState(String value) {
        if(value == null) return null;

        if(value.equals("Cancelled")) {
            return State.Cancelled;
        } else if(value.equals("Sent")) {
            return State.Sent;
        } else if(value.equals("Rejected")) {
            return State.Rejected;
        } else if(value.equals("Accepted")) {
            return State.Accepted;
        } else if(value.equals("Failed")) {
            return State.Failed;
        } else if(value.equals("Delivered")) {
            return State.Delivered;
        }
        return null;
    }

    public String getDateAsString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = dateFormat.format(dateTime);
        return date;
    }

    public Date stringToDate(String textCreated) {
        if(textCreated == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS").parse(textCreated);
        }
        catch (ParseException e) { e.printStackTrace(); }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public LinkedHashMap<String, String> getExtras() {
        return extras;
    }

    public Message setExtras(LinkedHashMap<String, String> extras) {
        this.extras = extras;
        return this;
    }
}
