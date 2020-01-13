package proxyChecker.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.internal.http2.Header;
import proxyChecker.core.Checker;
import proxyChecker.core.ExtendedProxy;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class MainController implements Initializable, Checker.CheckingListener {

    private final Pattern ipPattern = Pattern.compile("^$|^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$");

    //General tab
    @FXML private TextArea inputTa;
    @FXML private TextField urlTf;
    @FXML private TextField timeoutTf;
    @FXML private Spinner<Integer> threadsSpn;
    @FXML private Spinner<Integer> checksSpn;

    //body
    @FXML  private ProgressBar progBar;
    @FXML private Label progLbl;

    @FXML private TableView<ExtendedProxy> outputTable;
    @FXML private TableView<Header> headersTable;
    @FXML private TableColumn<ExtendedProxy, String> ipCol;
    @FXML private TableColumn<ExtendedProxy, String> addressCol;
    @FXML private TableColumn<ExtendedProxy, Integer> portCol;
    @FXML private TableColumn<ExtendedProxy, Integer> checksCol;
    @FXML private TableColumn<ExtendedProxy, Long> avgTimeCol;
    @FXML private TableColumn<ExtendedProxy, Boolean> isAllOkCol;
    @FXML private TableColumn<Header, String> headerNameCol;
    @FXML private TableColumn<Header, String> headerValueCol;

    //console
    @FXML private TextArea consoleTa;

    private Checker checker;

    private ObservableList<ExtendedProxy> proxies = FXCollections.observableArrayList();;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //general tab
        urlTf.setText(Prefs.getString(Prefs.Key.URL));
        threadsSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, Prefs.getInt(Prefs.Key.THREADS)));
        checksSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, Prefs.getInt(Prefs.Key.CHECKS)));
        timeoutTf.setText(String.valueOf(Prefs.getInt(Prefs.Key.TIMEOUT)));

        //Headers tab
        headerNameCol.prefWidthProperty().bind(headersTable.widthProperty().multiply(0.3));
        headerValueCol.prefWidthProperty().bind(headersTable.widthProperty().multiply(0.7));

        //body
        ipCol.prefWidthProperty().bind(outputTable.widthProperty().multiply(0.1));
        addressCol.prefWidthProperty().bind(outputTable.widthProperty().multiply(0.4));
        portCol.prefWidthProperty().bind(outputTable.widthProperty().multiply(0.1));
        checksCol.prefWidthProperty().bind(outputTable.widthProperty().multiply(0.1));
        avgTimeCol.prefWidthProperty().bind(outputTable.widthProperty().multiply(0.2));
        isAllOkCol.prefWidthProperty().bind(outputTable.widthProperty().multiply(0.1));
        ipCol.setCellValueFactory(new PropertyValueFactory<>("ip"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        portCol.setCellValueFactory(new PropertyValueFactory<>("port"));
        checksCol.setCellValueFactory(new PropertyValueFactory<>("checksCount"));
        avgTimeCol.setCellValueFactory(new PropertyValueFactory<>("avgTime"));
        isAllOkCol.setCellValueFactory(new PropertyValueFactory<>("isAllOk"));
        TableContextMenu contextMenu = new TableContextMenu(outputTable);
        outputTable.setItems(proxies);


    }

    @FXML
    public void start() {
        proxies.clear();
        if (urlTf.getText() == null || urlTf.getText().length() == 0) {
            log("URL isn't specified");
            return;
        }
        if (inputTa.getText() == null || inputTa.getText().length() == 0) {
            log("Proxies aren't specified");
            return;
        }
        proxies.addAll(getProxies(inputTa.getText()));
        if (proxies.isEmpty()) {
            log("Correct proxies not found");
            return;
        }
        checker = new Checker(urlTf.getText(), proxies, this);
        checker.setChecksCount(checksSpn.getValue());
        checker.setMaxThreads(threadsSpn.getValue());
        Prefs.put(Prefs.Key.CHECKS, checksSpn.getValue());
        Prefs.put(Prefs.Key.THREADS, threadsSpn.getValue());
        //timeout
        try {
            int timeout = Integer.parseInt(timeoutTf.getText());
            checker.setTimeout(timeout);
            Prefs.put(Prefs.Key.TIMEOUT, timeout);
        } catch (Exception e) {
            log("Incorrect timeout specified. The default timeout is set (" + checker.getTimeout() + "ms)");
        }

        log("Checking started");
        checker.start();
    }

    @FXML
    public void stop() {
        if (checker != null) {
            checker.stop();
            onFinish();
        }
    }

    private List<ExtendedProxy> getProxies(String input) {
        List<ExtendedProxy> proxies = new ArrayList<>();
        String[] lines = input.split("\n");
        for (String line : lines) {
            try {
                ExtendedProxy extProxy;
                String[] tokens = line.split(",");
                String ip = tokens[0].trim();
                if (ipPattern.matcher(ip).find())
                    extProxy = new ExtendedProxy(ip.split(":")[0], Integer.parseInt(ip.split(":")[1]));
                else {
                    log(ip + " - not looks like IP\n");
                    continue;
                }
                if (tokens.length == 3) extProxy.setCredentials(tokens[1].trim(), tokens[2].trim());
                else if (tokens.length != 1) log(line + " - unable to read login and password\n");
                proxies.add(extProxy);
            } catch (Exception e) {
                e.printStackTrace();
                log(line + " - unable to parse line");
            }
        }
        return proxies;
    }

    @Override
    public void onCheckComplete(ExtendedProxy proxy) {
        outputTable.refresh();
        log(String.format("%-30s%-22s%s", proxy, "Response code: " + proxy.getLastCode(), "Response time: " + proxy.getLastResponseTime()));
        progBar.setProgress(checker.getProgress());
        Platform.runLater(() -> progLbl.setText(String.format("%.1f", checker.getProgress() * 100) + "%"));

    }

    @Override
    public void onFinish() {
        log("Checking complete");
    }



    private void log(String log) {
        String curTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        Platform.runLater(() -> {
            consoleTa.setText(consoleTa.getText() + curTime + ": " + log +"\n");
            consoleTa.positionCaret(consoleTa.getLength());
        });
    }
}
