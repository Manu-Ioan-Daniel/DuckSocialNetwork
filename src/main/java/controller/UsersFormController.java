package controller;
import domain.Duck;
import domain.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.FriendshipModel;
import models.UserModel;
import utils.Models;
import utils.StageManager;
import utils.observer.Observer;
import java.net.URL;
import java.util.ResourceBundle;



public class UsersFormController implements Observer, Initializable {
    @FXML
    private BorderPane root;

    @FXML
    private Label duckCount;

    @FXML
    private Label peopleCount;

    @FXML
    private Label userCount;

    @FXML
    private Label usernameLabel;

    @FXML
    private TableView<User> usersTableView;

    @FXML
    private TableColumn<User,String> usernameColumn;

    @FXML
    private TableColumn<User,String> emailColumn;

    @FXML
    private TableColumn<User,String> friendsColumn;

    @FXML
    private TableColumn<User,String> typeColumn;

    @FXML
    private Pagination pagination;

    private UserModel userModel;
    private FriendshipModel friendshipModel;

    private final static int USERS_PER_PAGE = 3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userModel = Models.getUserModel();
        userModel.addObserver(this);
        this.friendshipModel = Models.getFriendshipModel();
    }

    public void initData(String username) {
        usernameLabel.setText(username);
        initUsersTable();
        loadLabelInfo();
        initPagination();
    }

    private void initPagination() {
        loadPaginationPageCount();
        pagination.setPageFactory((index)->{
            refreshUsersTable(index);
            return new VBox();
        });
    }

    private void initUsersTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        friendsColumn.setCellValueFactory(cell-> new SimpleStringProperty(
                userModel.mapIdsToUsernamesString(friendshipModel.findFriendsOf(cell.getValue().getId()))));
        typeColumn.setCellValueFactory(cell->
                new SimpleStringProperty(cell.getValue() instanceof Duck ? "duck" : "person"));
    }

    public void loadLabelInfo(){
        userCount.setText(Integer.toString(userModel.getTotalUsers()));
        duckCount.setText(Integer.toString(userModel.getTotalDucks()));
        peopleCount.setText(Integer.toString(userModel.getTotalPeople()));
    }

    public void loadPaginationPageCount(){
        pagination.setPageCount(userModel.getPageCount(USERS_PER_PAGE));
    }

    private void refreshUsersTable(int pageIndex){
        usersTableView.setItems(userModel.findUsersFromPage(pageIndex, USERS_PER_PAGE));
        if(!usersTableView.getSortOrder().contains(usernameColumn))
            usersTableView.getSortOrder().add(usernameColumn);
        usersTableView.sort();
    }

    @FXML
    public void handleSignout(){
        StageManager.showConfirmationAlert(this::signout);
    }

    public void signout(){

        userModel.removeObserver(this);
        Stage stage = (Stage) root.getScene().getWindow();
        StageManager.showLoginWindow(stage);

    }

    @FXML
    public void handleAdd(){
        StageManager.showAddUserWindow();
    }

    public void handleDelete(){
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if(selectedUser == null){
            StageManager.showErrorAlert("Please select a user");
            return;
        }
        if(selectedUser.getUsername().equals(usernameLabel.getText())){
            StageManager.showErrorAlert("You cannot delete yourself!");
            return;
        }
        userModel.delete(selectedUser.getId());
    }

    public void handleFriends(){
        userModel.removeObserver(this);
        Stage  stage = (Stage) root.getScene().getWindow();
        StageManager.showFriendsWindow(stage,usernameLabel.getText());

    }

    @Override
    public void update(){
        loadLabelInfo();
        loadPaginationPageCount();
        refreshUsersTable(pagination.getCurrentPageIndex());
    }

}
