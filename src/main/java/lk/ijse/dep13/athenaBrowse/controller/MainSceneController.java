package lk.ijse.dep13.athenaBrowse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.net.URL;

public class MainSceneController {
    public AnchorPane root;
    public WebView wbDisplay;
    public TextField txtAddress;

    public void initialize() {
        txtAddress.setText("www.google.com");
        loadWebPage(txtAddress.getText());
        txtAddress.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) Platform.runLater(txtAddress::selectAll);
        });
    }

    public void txtAddressOnAction(ActionEvent actionEvent) {
        loadWebPage(txtAddress.getText());

    }

    public void loadWebPage(String url) {
        System.out.println(url);
        int i=-1;
        String protocol = null;
        String host = null;
        String port = null;
        String path = null;

        if((i=url.indexOf("://"))!=-1){
            protocol = url.substring(0,i);
        }else{
            protocol = "http";
            url = protocol+"://"+url;
        }

        int beginIndexOfHost = url.indexOf("/",protocol.length()+2);
        int beginIndexOfPort = url.indexOf(":",protocol.length()+3);
        int beginIndexOfPath  = url.indexOf("/",protocol.length()+3);

        if(beginIndexOfPort==-1 && beginIndexOfPath==-1){
            host = url.substring(beginIndexOfHost+1);
            port = (protocol.equals("http"))?"80":"443";
            path = "/";
        } else if (beginIndexOfPort==-1) {
            host = url.substring(beginIndexOfHost+1,beginIndexOfPath);
            port = (protocol.equals("http"))?"80":"443";
            path = url.substring(beginIndexOfPath);

        } else if (beginIndexOfPath==-1) {
            host = url.substring(beginIndexOfHost+1,beginIndexOfPort);
            port = url.substring(beginIndexOfPort);
            path = "/";

        }else{
            host = url.substring(beginIndexOfHost+1,beginIndexOfPort);
            port = url.substring(beginIndexOfPort,beginIndexOfPath);
            path = url.substring(beginIndexOfPath);
        }

        if(!protocol.equals("http") && !protocol.equals("https") && beginIndexOfPort==-1){
            System.out.println("Invalid Url");
        }else{
            System.out.println("protocol: "+protocol);
            System.out.println("host: "+host);
            System.out.println("port: "+port);
            System.out.println("path: "+path);
        }



    }
}
