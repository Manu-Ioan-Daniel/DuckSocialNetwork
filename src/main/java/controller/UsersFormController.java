package controller;
import domain.User;
import enums.ChangeEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.UsersService;
import utils.Services;
import utils.StageManager;
import utils.observer.Observer;
import java.net.URL;
import java.util.Objects;
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

    private UsersService usersService;

    private User currentUser;

    private final static int USERS_PER_PAGE = 3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.usersService = Services.getUsersService();
        usersService.addObserver(this);
        Services.getFriendsService().addObserver(this);
    }

    public void initData(String username) {
        usernameLabel.setText(username);
        usersService.getUser(usernameLabel.getText()).ifPresent(user -> currentUser = user);
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
        friendsColumn.setCellValueFactory(cell-> new SimpleStringProperty(usersService.getFriendsToString(cell.getValue().getId())));
        typeColumn.setCellValueFactory(cell-> new SimpleStringProperty(usersService.getType(cell.getValue())));
    }

    public void loadLabelInfo(){
        userCount.setText(Integer.toString(usersService.getTotalUsers()));
        duckCount.setText(Integer.toString(usersService.getTotalDucks()));
        peopleCount.setText(Integer.toString(usersService.getTotalPeople()));
    }

    private void loadPaginationPageCount(){
        pagination.setPageCount(usersService.getPageCount(USERS_PER_PAGE));
    }

    private void refreshUsersTable(int pageIndex){
        usersTableView.setItems(usersService.findUsersFromPage(pageIndex, USERS_PER_PAGE));
    }

    @FXML
    public void handleSignout(){
        StageManager.showConfirmationAlert(this::signout);
    }

    public void signout(){

        removeObservers();
        Stage stage = (Stage) root.getScene().getWindow();
        StageManager.showLoginWindow(stage);

    }

    @FXML
    public void handleAdd(){
        StageManager.showAddUserWindow();
    }

    @FXML
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
        usersService.delete(selectedUser.getId());
    }

    public void handleFriends(){
        removeObservers();
        Stage  stage = (Stage) root.getScene().getWindow();
        StageManager.showFriendsWindow(stage,usernameLabel.getText());

    }

    private void loadNotifications(){
        if(Objects.equals(usersService.getLastFriendRequest().getId().getSecond(), currentUser.getId()))
            StageManager.showInformationAlert("You have friend requests from: " + usersService.getFriendRequestsToString(currentUser.getId()));
    }

    private void removeObservers(){
        usersService.removeObserver(this);
        Services.getFriendsService().removeObserver(this);
    }

    @Override
    public void update(ChangeEvent event){
        if(ChangeEvent.SENT_FRIEND_REQUEST == event){
            loadNotifications();
        }
        else {
            loadLabelInfo();
            loadPaginationPageCount();
            refreshUsersTable(pagination.getCurrentPageIndex());
        }
    }

}
