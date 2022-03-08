package com.example.map_proiect_extins.controller;

import com.example.map_proiect_extins.FriendshipsWindow;
import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.domain.UserFriendsDTO;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.*;
import java.time.LocalDate;
import java.util.List;



public class RaportsController {

    UserDashboard userDashboard = new UserDashboard();
    ObservableList<UserFriendsDTO> modelListFriends = FXCollections.observableArrayList();

    @FXML
    private ListView<UserFriendsDTO> friendsList;

    @FXML
    private Button backBtn;

    @FXML
    private TextArea raportResultTextArea;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker finishDate;

    @FXML
    private Label errorFriend;

    @FXML
    private Label errorStartDate;

    @FXML
    private Label errorFinishDate;

    @FXML
    public void initialize() {
        friendsList.setItems(modelListFriends);
        raportResultTextArea.setPromptText("Raport result...");

        errorFriend.setText("");
        errorStartDate.setText("");
        errorFinishDate.setText("");

        friendsList.getSelectionModel().selectedItemProperty().addListener(o -> errorFriend.setText(""));
        startDate.valueProperty().addListener(o -> errorStartDate.setText(""));
        finishDate.valueProperty().addListener(o -> errorFinishDate.setText(""));
    }

    private List<UserFriendsDTO> getFriendshipsDTOList() {

        return userDashboard.getUserFriendships();
//                .stream()
//                .sorted((f1,f2) -> getLastMsgDate(f2).compareTo(getLastMsgDate(f1)))
//                .toList();
    }

    public void setServices(UserService usrService, FriendshipService frService, User loginUser, MessageService msgService) {
        userDashboard.setFriendshipService(frService);
        userDashboard.setCurrentUser(loginUser);
        userDashboard.setUserService(usrService);
        userDashboard.setMessageService(msgService);

        modelListFriends.setAll(getFriendshipsDTOList());

    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        try {
            Stage stage;
            Scene scene;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FriendshipsWindow.class.getResource("views/friendships-view.fxml"));
            AnchorPane root = loader.load();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            FrienshipsController controller = loader.getController();
            controller.setService(userDashboard.getFriendshipService(), userDashboard.getUserService(), userDashboard.getCurrentUser(), userDashboard.getMessageService());
            stage.setScene(scene);
            stage.setMinWidth(750);
            stage.setMinHeight(500);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onActionRaport1(ActionEvent event) {
        Boolean raport = true;
        raportResultTextArea.setText("");
        String raportResult = "";

        LocalDate dateS = LocalDate.now(), dateF = LocalDate.now();

        if (startDate.getValue() == null) {
            raport = false;
            raportResultTextArea.setPromptText("Raport result...");
            errorStartDate.setText("Select a starting date!");
        }
        if (finishDate.getValue() == null) {
            raport = false;
            raportResultTextArea.setPromptText("Raport result...");
            errorFinishDate.setText("Select a finishing date!");
        }

        if (raport == true) {
            try {
                String raportFr = "", raportMsg = "";

                dateS = startDate.getValue();
                dateF = finishDate.getValue();

                for (String r : userDashboard.raportFriendshipsInAPeriod(dateS,dateF)) {
                    raportFr += (r + "\n");
                }

                for (String r : userDashboard.raportMessagesInAPeriodForAFriend(dateS,dateF)) {
                    raportMsg += (r + "\n");
                }

                raportResult += "YOUR ACTIVITY FOR " + dateS + " AND " + dateF + ":\n\n\n";

                raportResult += "\nNEW FRIENDSHIPS:" + "\n\n";
                if (raportFr.equals("")) {
                    raportResult += "No new friends." + "\n\n";
                } else {
                    raportResult += raportFr + "\n";
                }

                raportResult += "\nNEW MESSAGES:" + "\n\n";
                if (raportMsg.equals("")) {
                    raportResult += "No new messages." + "\n\n";
                } else {
                    raportResult += raportMsg + "\n";
                }

            } catch (Exception e) {
                errorStartDate.setText(e.getMessage());
                errorFinishDate.setText(e.getMessage());
            }

        }

        if (raportResult.equals(""))
            raportResultTextArea.setPromptText("Raport result...");
        else
            raportResultTextArea.setText(raportResult);
    }

    @FXML
    public void onActionRaport2(ActionEvent event) {
        Boolean raport = true;
        UserFriendsDTO friend = friendsList.getSelectionModel().getSelectedItem();
        raportResultTextArea.setText("");
        String raportResult = "";

        LocalDate dateS = LocalDate.now(), dateF = LocalDate.now();

        if (friend == null) {
            raport = false;
            raportResultTextArea.setPromptText("Raport result...");
            errorFriend.setText("Select a friend!");
        }
        if (startDate.getValue() == null) {
            raport = false;
            raportResultTextArea.setPromptText("Raport result...");
            errorStartDate.setText("Select a starting date!");
        }
        if (finishDate.getValue() == null) {
            raport = false;
            raportResultTextArea.setPromptText("Raport result...");
            errorFinishDate.setText("Select a finishing date!");
        }


        if (raport == true) {
            try {
                String raportFr = "", raportMsg = "";

                Long idFr;
                if (friend.getId().getSecond().equals(userDashboard.getCurrentUserId()))
                    idFr = friend.getId().getFirst();
                else
                    idFr = friend.getId().getSecond();

                dateS = startDate.getValue();
                dateF = finishDate.getValue();

                for (String r : userDashboard.raportMessagesInAPeriodForAFriendMSG(idFr,dateS,dateF)) {
                    raportMsg += (r + "\n");
                }

                raportResult += "RECIVED MESSAGES FOR " + dateS + " AND " + dateF + ":\n\n\n";

                raportResult += "\nNEW MESSAGES FROM " + friend.getFirstName() + " " + friend.getLastName() + ":" + "\n\n";
                if (raportMsg.equals("")) {
                    raportResult += "No new messages." + "\n\n";
                } else {
                    raportResult += raportMsg + "\n";
                }

            } catch (Exception e) {
                errorStartDate.setText(e.getMessage());
                errorFinishDate.setText(e.getMessage());
            }

        }

        if (raportResult.equals(""))
            raportResultTextArea.setPromptText("Raport result...");
        else
            raportResultTextArea.setText(raportResult);
    }

    @FXML
    public void onActionGeneratePdfBtn(ActionEvent event) throws IOException {
        Stage secondStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", ".pdf"));
        File location = fileChooser.showSaveDialog(secondStage);
        if(location != null && !raportResultTextArea.getText().equals("")){
            PDDocument pdfdoc= new PDDocument();
            PDPage page = new PDPage();
            pdfdoc.addPage(page);

            PDFont font = PDType1Font.HELVETICA_BOLD;

            // Start a new content stream which will "hold" the to be created content
            PDPageContentStream contentStream = new PDPageContentStream(pdfdoc, page);

            // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
            contentStream.beginText();

            contentStream.setFont( font, 10 );
            //Setting the leading
            contentStream.setLeading(14.5f);

            //Setting the position for the line
            contentStream.newLineAtOffset(25, 725);

            String raportResult = raportResultTextArea.getText();
            String[] textResult = raportResult.split("\n");
            for(String t:textResult){
                contentStream.newLine();
                contentStream.showText(t);
            }
            contentStream.endText();

            // Make sure that the content stream is closed:
            contentStream.close();


            //path where the PDF file will be store
            pdfdoc.save(location);

            //prints the message if the PDF is created successfully
            System.out.println("PDF created");

            //closes the document
            pdfdoc.close();
        }else {

        }
    }


}
