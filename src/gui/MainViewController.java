package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.ProductService;
import model.services.SellerService;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemProduct;
	
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellertList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.upadeteTableView();
		});
	}
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.upadeteTableView();
		});
		
	}
	
	public void menuItemProduct() {
		loadView("/gui/ProductList.fxml", (ProductListController controller) ->{
			controller.setProductService(new ProductService());
			controller.upadeteTableView();
			
		});
	}
	
	public void munuItemTeste() {
		loadView("/gui/teste.fxml",x-> {});
	}
	
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
		
	}
	
	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		
	}
	
	private synchronized <T> void  loadView(String absoluteName, Consumer<T> initializingActionConsumer) {
		try {
			
			FXMLLoader loader= new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVebox = loader.load(); 
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVebox.getChildren());
			
			T controller = loader.getController();
			initializingActionConsumer.accept(controller);
		
		} catch (IOException e) {
			Alerts.showAlert("IO Exeption", "Error loading View", e.getMessage(), AlertType.ERROR);
		}
	}
	

}
