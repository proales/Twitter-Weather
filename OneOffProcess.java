import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import java.util.List;
import twitter4j.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.*;

public class OneOffProcess
{
  public static void main(String args[]){
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer("key_goes_here", "key_goes_here");
        AccessToken accessToken = new AccessToken("key_goes_here", "key_goes_here");
        twitter.setOAuthAccessToken(accessToken);
        System.out.println("System Started");
        
        File a = new File("1.txt"); 
        saveUrlToFile(a,"http://www.google.com/ig/api?weather=60615");
        String XMLString = fileToString("1.txt");
        setStatus(getWeather(XMLString), twitter);
        System.exit(0);
  }
    
  public static String getWeather(String XMLString){
      //
      String currentA = "<current_conditions>";
      String currentB = "</current_conditions>";
      String conditionA = "<condition data=";  //condition data "clear"
      String conditionB = "/>";
      String tempA = "<temp_f data=";     //temp data "79"
      String tempB = "/>";
      String forecastA = "<forecast_conditions>";
      String forecastB = "</forecast_conditions>";
      String lowA = "<low data=";
      String lowB = "/>";
      String highA = "<high data=";
      String highB = "/>";
      
      String currentString = "";
      String forecastString = "";
      
      currentString = XMLString.substring(XMLString.indexOf(currentA)+currentA.length(),XMLString.indexOf(currentB));
      System.out.println(currentString);
      
      forecastString = XMLString.substring(XMLString.indexOf(forecastA)+forecastA.length()+1,XMLString.indexOf(forecastB));
      
      String condition = currentString.substring(currentString.indexOf(conditionA)+conditionA.length()+1,currentString.indexOf(conditionB)-1);
      String temp = currentString.substring(currentString.indexOf(tempA)+tempA.length()+1,currentString.indexOf(tempB,currentString.indexOf(tempA)+tempA.length())-1);
      
      String low = forecastString.substring(forecastString.indexOf(lowA)+lowA.length()+1,forecastString.indexOf(lowB,forecastString.indexOf(lowA)+lowA.length())-1);
      String high = forecastString.substring(forecastString.indexOf(highA)+highA.length()+1,forecastString.indexOf(highB, forecastString.indexOf(highA)+highA.length())-1);
      String forecastCondition = forecastString.substring(forecastString.indexOf(conditionA)+conditionA.length()+1,forecastString.indexOf(conditionB,forecastString.indexOf(conditionA)+conditionA.length())-1);
      
      return "At "+getTime()+" in Hyde Park: Condition is "+condition+" and "+temp+" degrees. Forecast for today: "+forecastCondition+" High-"+high+" Low-"+low;
    }
  
  public static String getTime(){
      Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      Date date = new Date();
      utcCalendar.setTime(date);
      String timeString = "";
      
      int ESThour = utcCalendar.get(Calendar.HOUR) - 5;
      int ESTminute = utcCalendar.get(Calendar.MINUTE);
      if (ESThour == 0){         //5pm
          ESThour = 12;
      }
      if (ESThour == -1){        //4pm
          ESThour = 11;
      }
      if (ESThour == -2){        //3pm
          ESThour = 10;
      }
      if (ESThour == -3){        //2pm
          ESThour = 9;
      }
      if (ESThour == -4){        //1pm
          ESThour = 8;
      }
      
      if (ESTminute < 10){
        timeString = ESThour+":0"+ESTminute;
      } else {
        timeString = ESThour+":"+ESTminute;
      }  
      
      return timeString;
    }
    
  public static void saveUrlToFile(File saveFile,String location){
        URL url;
        try {
            url = new URL(location);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedWriter out = new BufferedWriter(new FileWriter(saveFile));
            char[] cbuf=new char[255];
            while ((in.read(cbuf)) != -1) {
                out.write(cbuf);
            }
            in.close();
            out.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
  }
  
  private static String fileToString(String filePath){
        try{
            StringBuffer fileData = new StringBuffer(1000);
            BufferedReader reader = new BufferedReader(
                    new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            return fileData.toString();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
    }  

  public static void setStatus(String statusString, Twitter twitter){
      try {
            Status status = twitter.updateStatus(statusString);
            System.out.println("Successfully updated the status to [" + status.getText() + "].");
      } catch (TwitterException ex) {
            ex.printStackTrace();
      }
    }
}
