package lk.ijse.dep13.athenaBrowse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.io.*;
import java.net.Socket;

public class MainSceneController {
    public AnchorPane root;
    public WebView wbDisplay;
    public TextField txtAddress;

    public void initialize() throws Exception {
        txtAddress.setText("http://ikman.lk/");
        loadWebPage(txtAddress.getText());
        txtAddress.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) Platform.runLater(txtAddress::selectAll);
        });
    }

    public void txtAddressOnAction(ActionEvent actionEvent) throws Exception {
        String url = txtAddress.getText();
        if (url.isBlank()) return;
        loadWebPage(url);

    }

    public void loadWebPage(String url) throws Exception {
        System.out.println(url);
        int i = -1;
        String protocol = null;
        String host = null;
        String port = null;
        String path = null;

        if ((i = url.indexOf("://")) != -1) {
            protocol = url.substring(0, i);
        } else {
            protocol = "http";
            url = protocol + "://" + url;
        }

        int beginIndexOfHost = url.indexOf("/", protocol.length() + 2);
        int beginIndexOfPort = url.indexOf(":", protocol.length() + 3);
        int beginIndexOfPath = url.indexOf("/", protocol.length() + 3);

        if (beginIndexOfPort == -1 && beginIndexOfPath == -1) {
            host = url.substring(beginIndexOfHost + 1);
            port = (protocol.equals("http")) ? "80" : "443";
            path = "/";
        } else if (beginIndexOfPort == -1) {
            host = url.substring(beginIndexOfHost + 1, beginIndexOfPath);
            port = (protocol.equals("http")) ? "80" : "443";
            path = url.substring(beginIndexOfPath);

        } else if (beginIndexOfPath == -1) {
            host = url.substring(beginIndexOfHost + 1, beginIndexOfPort);
            port = url.substring(beginIndexOfPort);
            path = "/";

        } else {
            host = url.substring(beginIndexOfHost + 1, beginIndexOfPort);
            port = url.substring(beginIndexOfPort, beginIndexOfPath);
            path = url.substring(beginIndexOfPath);
        }

        if (!protocol.equals("http") && !protocol.equals("https") && beginIndexOfPort == -1) {
            System.out.println("Invalid Url");
        } else {
            System.out.println("protocol: " + protocol);
            System.out.println("host: " + host);
            System.out.println("port: " + port);
            System.out.println("path: " + path);
        }

        try {
            Socket socket = new Socket(host, Integer.parseInt(port));
            System.out.println("Connected to " + socket.getRemoteSocketAddress());
            new Thread(() -> {
                try {
                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    String statusLine = br.readLine();
                    String[] s = statusLine.split(" ");
                    int statusCode = Integer.parseInt(s[1]);
                    System.out.println(statusCode);

                    String line;
                    String newUrl = null;
                    String header;
                    String value = null;
                    String contentType = "";

                    while ((line = br.readLine()) != null && !line.isBlank()) {
                        String[] split = line.split(":");
                        header = split[0].strip();
                        value = line.substring(header.length() + 1).strip();
                        if (statusCode >= 300 && statusCode <= 400) {
                            if (header.contains("Location")) {
                                loadWebPage(value.strip());
                                break;
                            }
                        }
                        if (header.equalsIgnoreCase("Content-Type")) {
                            contentType = value.strip();
                            System.out.println(contentType);

                        }
                    }
                    if (contentType.toLowerCase().contains("text/html".toLowerCase())) {
                        String body = "";
                        if ((line = br.readLine()) != null && line.contains("<")) {
                            body = line;
                        }
                        while ((line = br.readLine()) != null && !line.isBlank()) {
                            body += line;
                        }
                        String finalBody = body;
                        Platform.runLater(() -> {
                            wbDisplay.getEngine().loadContent(finalBody, "text/html");
                        });
                    } else {
                        System.out.println("We accept only text/html");
                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();


            String httpProtocol = """
                    GET %s HTTP/1.1
                    Host: %s
                    User-Agent: dep-browser
                    Connection: close
                    Accept: text/html;
                    
                    """.formatted(path, host);

            OutputStream os = socket.getOutputStream();
            os.write(httpProtocol.getBytes());
            os.flush();


        } catch (Exception e) {
            System.out.println("Connection failed");
        }


    }


}
