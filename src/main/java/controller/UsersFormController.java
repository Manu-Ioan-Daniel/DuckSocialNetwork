package controller;

import domain.Duck;
import domain.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.FriendshipModel;
import models.UserModel;
import utils.FXMLUtil;
import utils.Models;
import utils.StageManager;
import utils.Tuple;
import utils.observer.Observer;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class UsersFormController implements Observer, Initializable {
    @FXML
    private BorderPane root;

    @FXML
    private Button friendsBtn;

    @FXML
    private Button chatBtn;

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

    private final StageManager addUserStageManager = new StageManager();
    private final StageManager signoutAlertManager = new StageManager();
    private final StageManager deleteErrorAlertManager = new StageManager();

    private final static int USERS_PER_PAGE = 3;


    public void initData(String username) {
        usernameLabel.setText(username);
        initUsersTable();
        loadLabelInfo();
        initPagination();
    }

    private void initPagination() {
        loadPaginationPageCount();
        pagination.setPageFactory((index)->{
            refreshUsersTable();
            return new VBox();
        });
    }

    private void initUsersTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        friendsColumn.setCellValueFactory(cell-> new SimpleStringProperty( friendshipModel.findFriendsOf(cell.getValue().getId()).stream()
                .map(id->userModel.findOne(id))
                .filter(Optional::isPresent)
                .map(user->user.get().getUsername())
                .collect(Collectors.joining(","))));
        typeColumn.setCellValueFactory(cell->
                new SimpleStringProperty(cell.getValue() instanceof Duck ? "duck" : "person"));
    }

    public void loadLabelInfo(){
        userCount.setText(Integer.toString(userModel.getTotalUsers()));
        duckCount.setText(Integer.toString(userModel.getTotalDucks()));
        peopleCount.setText(Integer.toString(userModel.getTotalPeople()));
    }

    public void loadPaginationPageCount(){
        int totalUsers = userModel.getTotalUsers();
        int pageCount = totalUsers/USERS_PER_PAGE + (totalUsers % USERS_PER_PAGE == 0 ? 0 : 1);
        pagination.setPageCount(pageCount);
    }

    public void signout(){

        userModel.removeObserver(this);
        Stage stage = (Stage) root.getScene().getWindow();
        StageManager.showSignoutWindow(stage);

    }

    @Override
    public void update(){
        loadLabelInfo();
        loadPaginationPageCount();
        refreshUsersTable();
    }
    private void refreshUsersTable(){
        int pageIndex = pagination.getCurrentPageIndex();
        usersTableView.setItems(userModel.findUsersFromPage(pageIndex, USERS_PER_PAGE));
        if(!usersTableView.getSortOrder().contains(usernameColumn))
            usersTableView.getSortOrder().add(usernameColumn);
        usersTableView.sort();
    }

    @FXML
    public void handleSignout(){
        signoutAlertManager.showConfirmationAlert(this::signout);
    }

    @FXML
    public void handleAdd(){
        Tuple<Scene,AddUserFormController> tuple = FXMLUtil.load("/view/addUserForm.fxml");

        AddUserFormController controller = tuple.getSecond();
        controller.setUserModel(userModel);

        Scene scene = tuple.getFirst();

        addUserStageManager.showStage(scene,false);
    }

    public void handleDelete(){
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if(selectedUser == null){
            deleteErrorAlertManager.showErrorAlert("You did not select a user!");
            return;
        }
        if(selectedUser.getUsername().equals(usernameLabel.getText())){
            deleteErrorAlertManager.showErrorAlert("You cannot delete yourself!");
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
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userModel = Models.getUserModel();
        userModel.addObserver(this);
        this.friendshipModel = Models.getFriendshipModel();
    }

}
