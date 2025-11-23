package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AssignTenantController {

    @FXML private ComboBox<String> propertyComboBox;
    @FXML private TextField tenantEmailField;
    @FXML private TextField contractIdField;

    private FacadeClass f;
    private ObservableList<Property> propertyList = FXCollections.observableArrayList();

    private final int CURRENT_OWNER_ID = FacadeClass.CURRENT_USER_ID;

    @FXML
    public void initialize() {
        f = FacadeClass.getInstance();
        loadPropertiesIntoComboBox();
    }

    /**
     * Loads properties owned by the logged-in owner into the ComboBox.
     */
    private void loadPropertiesIntoComboBox() {
        try {
            List<Property> properties = f.getPropertyService().getOwnerProperties(CURRENT_OWNER_ID);

            propertyList.clear();
            propertyList.addAll(properties);

            ObservableList<String> comboItems = FXCollections.observableArrayList();

            for (Property p : properties) {
                comboItems.add(p.getProperty_id() + " - " + p.getProperty_name() + " (" + p.getAddress() + ")");
            }

            propertyComboBox.setItems(comboItems);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load properties.");
        }
    }

    /**
     * Handles adding tenant to a selected property.
     */
    @FXML
    private void handleSendInvitationClick() {
        String selectedPropertyString = propertyComboBox.getValue();
        String tenantEmail = tenantEmailField.getText().trim();
        String contractIdText = contractIdField.getText().trim();
        if (selectedPropertyString == null || tenantEmail.isEmpty() || contractIdText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please select a property and enter tenant email.");
            return;
        }

        // Extract property_id from combo value
        int propertyId = Integer.parseInt(selectedPropertyString.split(" - ")[0]);


        //extract the contract id



        Integer manualContractId = contractIdText.isEmpty() ? null : Integer.parseInt(contractIdText);

        int tenantId = f.getTenantService().getTenantKey(tenantEmail);
        try {
            // Assign tenant via service
            boolean yesSucess = f.getContractService().createAssignment(FacadeClass.CURRENT_USER_ID,propertyId,tenantId,manualContractId);

            if (yesSucess) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Success",
                        "Tenant assigned successfully!");

                tenantEmailField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Failed",
                        "Could not assign tenant. Check if the Assignment already exists or the property is full.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error assigning tenant.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
