import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.apache.http.util.TextUtils;
import org.json.JSONObject;

import java.util.concurrent.Future;

public class OMDBController {

    @FXML
    private Button searchBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private ImageView posterImgVw;
    @FXML
    private TextField queryTxtFd;
    @FXML
    private Label titleData;
    @FXML
    private Label plotData;
    @FXML
    private Label actorsData;
    @FXML
    private Label writerData;
    @FXML
    private Label genreData;
    @FXML
    private Label typeData;
    @FXML
    private Label ratedData;
    @FXML
    private Label runTimeData;
    @FXML
    private Label statusLbl;

    @FXML
    protected void onSearch(MouseEvent event) {

        String queryString = queryTxtFd.getText();

        if (TextUtils.isEmpty(queryString) || queryString.length() < 3) {
            statusLbl.setText("Search text is too small!");
            return;
        }

        statusLbl.setText("Searching...");

        sendQueryReqest(queryString);
    }

    @FXML
    protected void onReset(MouseEvent event) {
        resetAll();
    }

    private void setData(String posterUrl, String title, String plot, String actors, String writer,
                         String genre, String type, String rated, String runTime) {
        posterImgVw.setImage(new Image(posterUrl));
        titleData.setText(title);
        plotData.setText(plot);
        actorsData.setText(actors);
        writerData.setText(writer);
        genreData.setText(genre);
        typeData.setText(type);
        ratedData.setText(rated);
        runTimeData.setText(runTime);
    }

    private void sendQueryReqest(String query) {

        String urlString = "http://www.omdbapi.com/";
        String apiKey = "b54e863";

        Callback<JsonNode> callback = new Callback<JsonNode>() {
            @Override
            public void completed(HttpResponse<JsonNode> httpResponse) {

                JSONObject object = httpResponse.getBody().getObject();

                String title = object.optString("Title");
                String rated = object.optString("Rated");
                String runTime = object.optString("Runtime");
                String genre = object.optString("Genre");
                String plot = object.optString("Plot");
                String actors = object.optString("Actors");
                String posterUrl = object.optString("Poster");
                String type = object.optString("Type");
                String writer = object.optString("Writer");

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        statusLbl.setText("");
                        setData(posterUrl, title, plot, actors, writer, genre, type, rated, runTime);
                    }
                });
            }

            @Override
            public void failed(UnirestException e) {

                e.printStackTrace();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        statusLbl.setText("An error has occurred");
                    }
                });
            }

            @Override
            public void cancelled() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        statusLbl.setText("An error has occurred");
                    }
                });
            }
        };

        Future<HttpResponse<JsonNode>> request = Unirest.get(urlString)
                .queryString("apikey", apiKey)
                .queryString("t", query)
                .asJsonAsync(callback);
    }

    private void resetAll() {

        String noData = "No Data";

        posterImgVw.setImage(new Image(getClass().getResourceAsStream("poster.jpg")));
        titleData.setText(noData);
        plotData.setText(noData);
        actorsData.setText(noData);
        writerData.setText(noData);
        genreData.setText(noData);
        typeData.setText(noData);
        ratedData.setText(noData);
        runTimeData.setText(noData);
        statusLbl.setText("");
        queryTxtFd.setText("");
    }
}
