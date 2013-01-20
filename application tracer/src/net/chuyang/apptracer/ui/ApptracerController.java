package net.chuyang.apptracer.ui;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import net.chuyang.apptracer.AssistenceService;
import net.chuyang.apptracer.AssistenceService.ProcessVO;
import net.chuyang.apptracer.Configuration;
import net.chuyang.apptracer.TaskProcessor;
import net.chuyang.apptracer.Utils;
import net.chuyang.apptracer.codegen.ReturnValueVO;

public class ApptracerController implements Initializable {

	@FXML
	private ListView<ProcessVO> processListView;
	@FXML
	private Button startBtn;
	@FXML
	private Button stopBtn;
	@FXML
	private Button addClassFolderBtn;
	@FXML
	private Button addJarBtn;
	@FXML
	private ListView<String> classPathListView;
	@FXML
	private TextField targetJarTextfield;
	@FXML
	private Button addTargetJarBtn;
	@FXML
	private ComboBox<ClassWrapper> classCombo;
	@FXML
	private ComboBox<MethodWrapper> methodCombo;
	
	AssistenceService service = new AssistenceService();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ObservableList<ProcessVO> items = FXCollections.observableArrayList(service.getJavaProcess());
		processListView.setItems(items);
		processListView.setCellFactory(new Callback<ListView<ProcessVO>, ListCell<ProcessVO>>() {
			@Override
			public ListCell<ProcessVO> call(ListView<ProcessVO> list) {
				return new ProcessVOCell();
			}
		});
	}
	
	@FXML
	private void handleStartBtnAction(ActionEvent event){
		ProcessVO process = processListView.getSelectionModel().getSelectedItem();
		Configuration.INSTANCE.setTargetPort(process.pid);
		ReturnValueVO vo = new ReturnValueVO();
		vo.setClazz(classCombo.getSelectionModel().getSelectedItem().getClazz().getName());
		vo.setMethod(methodCombo.getSelectionModel().getSelectedItem().getMethod().getName());
		vo.setReturnType(methodCombo.getSelectionModel().getSelectedItem().getMethod().getReturnType().toString());
		TaskProcessor.INSTANCE.handleReturnValueTask(vo);
	}
	
	@FXML
	private void handleAddClassFolderAction(ActionEvent event){
		DirectoryChooser chooser = new DirectoryChooser();
		String title = Utils.getlocalizedString("ApptracerController.select.folder.title.txt");
		chooser.setTitle(title);
		File selectedDirectory = chooser.showDialog(addClassFolderBtn.getScene().getWindow());
		classPathListView.getItems().add(selectedDirectory.getAbsolutePath());
	}
	
	@FXML
	private void handleAddJarAction(ActionEvent event){
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ApptracerController.jar.filechooser.extention.txt", "*.jar");
		chooser.getExtensionFilters().add(extFilter);
		String title = Utils.getlocalizedString("ApptracerController.select.jar.titile.txt");
		chooser.setTitle(title);
		File selectedFile = chooser.showOpenDialog(addJarBtn.getScene().getWindow());
		classPathListView.getItems().add(selectedFile.getAbsolutePath());
	}
	
	@FXML
	private void handleSelectTargetJar(ActionEvent event){
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ApptracerController.jar.filechooser.extention.txt", "*.jar");
		chooser.getExtensionFilters().add(extFilter);
		String title = Utils.getlocalizedString("ApptracerController.select.jar.titile.txt");
		chooser.setTitle(title);
		File selectedFile = chooser.showOpenDialog(addTargetJarBtn.getScene().getWindow());
		targetJarTextfield.setText(selectedFile.getAbsolutePath());
		
		List<Class> classes = service.getClassesFromJar(selectedFile.getAbsolutePath());
		List<ClassWrapper> classWrappers = new ArrayList<ClassWrapper>();
		for(Class clazz : classes){
			ClassWrapper wrapper = new ClassWrapper(clazz);
			classWrappers.add(wrapper);
		}
		classCombo.setItems(FXCollections.observableList(classWrappers));
	}
	
	@FXML
	private void handleSelectClassAction(ActionEvent event){
		ClassWrapper classWrapper = classCombo.getSelectionModel().getSelectedItem();
		Method[] methods = classWrapper.getClazz().getDeclaredMethods();
		List<MethodWrapper> methodWrappers = new ArrayList<MethodWrapper>();
		for(Method method : methods){
			MethodWrapper wrapper = new MethodWrapper(method);
			methodWrappers.add(wrapper);
		}
		methodCombo.setItems(FXCollections.observableArrayList(methodWrappers));
	}
	
	static class ProcessVOCell extends ListCell<ProcessVO>{
		@Override
        public void updateItem(ProcessVO item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                this.setText(item.pid + " " + item.processName);
            }
        }
	}
	
	static class ClassWrapper{
		private Class clazz;
		public ClassWrapper(Class clazz){
			this.clazz = clazz;
		}
		
		public Class getClazz(){
			return clazz;
		}
		
		@Override
		public String toString(){
			return clazz.getName();
		}
	}
	
	static class MethodWrapper{
		private Method method;
		public MethodWrapper(Method method){
			this.method = method;
		}
		
		public Method getMethod(){
			return method;
		}
		
		@Override
		public String toString(){
			return method.getName();
		}
	}
}
