package com.company;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class MyObject {
    private String command; // append, reduce, multiply, divide, power
    private long number;

    public MyObject(String command, long number) {
        this.command = command;
        this.number = number;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

public class testTask {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        long operationNumber = 0;
        JSONParser jsonParser = new JSONParser();
        List<MyObject> inputArray = (List) new ArrayList<MyObject>();
        try (FileReader reader = new FileReader("input.json")) {
            //Read JSON file
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("input_array");
            operationNumber = (Long) jsonObject.get("operation_number");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                String command = (String) object.get("command");
                Long number = (Long) object.get("number");
                inputArray.add(new MyObject(command, number));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // part from example, can be improved
        long result = 0;
        for (MyObject object : inputArray) {
            if (object.getCommand().equals("append")) {
                result = result + object.getNumber();
            } else if (object.getCommand().equals("reduce")) {
                result = result - object.getNumber();
            } else if (object.getCommand().equals("multiply")) {
                result = result * object.getNumber();
            } else if (object.getCommand().equals("divide")) {
                result = result / object.getNumber();
            } else if (object.getCommand().equals("power")) {
                result = (long) Math.pow(result, object.getNumber());
            }
        }

        //create result object
        JSONObject resultObject = new JSONObject();
        resultObject.put("result", result);
        //Write JSON file
        try (FileWriter file = new FileWriter("result.json")) {
            file.write(resultObject.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        URL url = new URL("https://dummy.com/api/" + operationNumber + "/result");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-Language", "en-US");
        con.setDoOutput(true);
        String jsonInputString = resultObject.toJSONString();

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
    }
}
