package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Product;
import model.services.ProductService;

public class ProductListController implements Initializable, DataChangeListener {
	

	
	private ProductService service;
	
	@FXML
	private TableView<Product> tableViewProduct;
	
	@FXML
	private TableColumn<Product, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Product, Integer> tableColumnCode;
	
	@FXML
	private TableColumn<Product, String> tableColumName;
	
	@FXML
	private TableColumn<Product, String> tableColumnDescription;
	
	@FXML
	private TableColumn<Product, Double> tableColumnPrice;
	
	@FXML
	private Button btNeW;
	
	

	@FXML
	private TableColumn<Product, Product> tableColumnEDIT;
	
	@FXML
	private TableColumn<Product, Product> tableColumnREMOVE;
	
	
	
	private ObservableList<Product> obsList;
	
	@FXML
	public void onBtNewProdAction(ActionEvent event) {

		Stage parentStage = Utils.currentStage(event);
		Product obj = new Product();
		createDialogForm(obj, "/gui/ProductForm.fxml", parentStage);
	}
	
	public void setProductService(ProductService service) {
		this.service = service;
	}
	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	


	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		tableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();

		// TableView acompanhar a janela
		tableViewProduct.prefHeightProperty().bind(stage.heightProperty());
	}

	private void createDialogForm(Product obj, String string, Stage parentStage) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void onDataChanget() {
		// TODO Auto-generated method stub
		
	}
	
	public void upadeteTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		List<Product> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewProduct.setItems(obsList);
				
		//botao EDIT na tabela
		initEditButtons();
		//botrao remover tabela
		initRemoveButtons();

	}

	private void initRemoveButtons() {
		
		
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Product obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
		
	}

	protected void removeEntity(Product obj) {
	Optional <ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
			service.remove(obj);
			upadeteTableView();
			
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
				
			}
		}
	}

	

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Product obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/ProductList.fxml", Utils.currentStage(event)));
			}
		});
		
	}


}
