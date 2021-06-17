package net.victorbetoni.tasker.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.victorbetoni.tasker.TaskTracker;
import net.victorbetoni.tasker.controller.OrganizationController;
import net.victorbetoni.tasker.database.Database;
import net.victorbetoni.tasker.enums.OrganizationRole;
import net.victorbetoni.tasker.model.Organization;
import net.victorbetoni.tasker.model.auth.OrganizationSession;
import net.victorbetoni.tasker.utils.HashUtils;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class CreateOrganizationFormView implements View {

    @Override
    public Stage getStage() {
        Stage stage = new Stage();

        stage.getIcons().add(TaskTracker.ICON);
        stage.setTitle(TaskTracker.SOFTWARE_NAME + " - User Panel");

        Font ubuntu = Font.loadFont(UserPanelView.class.getResourceAsStream("/font/Ubuntu.ttf"), 12);

        GridPane grid = new GridPane();

        Font UBUNTU = Font.loadFont(UserPanelView.class.getResourceAsStream("/font/Ubuntu-Medium.ttf"), 12);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);

        grid.getColumnConstraints().addAll(col1, col2);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text title = new Text("Criar organização:");
        title.setId("text");
        title.setFont(Font.loadFont(UserPanelView.class.getResourceAsStream("/font/Ubuntu-Medium.ttf"), 25));
        grid.add(title, 0, 0, 2, 1);

        Label name = new Label("Nome da organização:");
        name.setFont(UBUNTU);
        grid.add(name, 0, 1);
        TextField nameField = new TextField();
        nameField.setId("text-field");
        nameField.setMinWidth(50);
        grid.add(nameField, 1, 1);

        Label pw = new Label("Senha:");
        grid.add(pw, 0, 2);
        PasswordField passwordInput = new PasswordField();
        grid.add(passwordInput, 1, 2);

        Text errorText = new Text();
        grid.add(errorText, 1, 3);

        Button criar = new Button("Criar");
        criar.setOnMouseClicked((e) -> {
            try{
                String orgName = nameField.getText().trim();
                String password = passwordInput.getText().trim();
                if(orgName.length() > 20)
                    throw new IOException("O nome da organização deve ter no máximo 20 caracteres");
                if(password.length() < 5)
                    throw new IOException("A senha deve ter no mínimo 5 caracteres");

                OrganizationController controller = TaskTracker.getOrganizationController();

                Organization org = controller.createOrganization(orgName, HashUtils.sha256(password), UUID.randomUUID());
                controller.addMemberToOrganization(TaskTracker.getSession().getUser(), org, OrganizationRole.OWNER);
                TaskTracker.setOrganizationSession(new OrganizationSession(org, System.currentTimeMillis()));

                OrganizationView view = new OrganizationView(org);
                view.getStage().show();
                stage.close();

            }catch (IOException ex) {
                errorText.setText(ex.getMessage());
            }

        });
        criar.setMinWidth(40);
        grid.add(criar, 0, 4);

        Scene scene = new Scene(grid, 500, 250);
        stage.setScene(scene);

        return stage;
    }
}