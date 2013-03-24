package net.chuyang.apptracer.ui;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.thehecklers.dialogfx.DialogFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import net.chuyang.apptracer.Constants;
import net.chuyang.apptracer.TaskProcessService;
import net.chuyang.apptracer.Utils;
import net.chuyang.apptracer.codegen.ClassVO;

public class ApptracerController implements Initializable {

	@FXML
	private Button refreshBtn;
	@FXML
	private ListView<ProcessVO> processListView;
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
	@FXML
	private Button startBtn;
	@FXML
	private Button stopBtn;
	
	AssistenceService assistService = new AssistenceService();
	TaskProcessService taskProcessService = null;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		stopBtn.setDisable(true);
		handleRefreshJpsAction(null);
		processListView.setCellFactory(new Callback<ListView<ProcessVO>, ListCell<ProcessVO>>() {
			@Override
			public ListCell<ProcessVO> call(ListView<ProcessVO> list) {
				return new ProcessVOCell();
			}
		});
	}
	
	@FXML
	private void handleStartBtnAction(ActionEvent event){
		if(validate() == false)
			return;
		
		ProcessVO process = processListView.getSelectionModel().getSelectedItem();
		ClassVO vo = new ClassVO();
		vo.setClazz(classCombo.getSelectionModel().getSelectedItem().getClazz().getName());
		vo.setMethod(methodCombo.getSelectionModel().getSelectedItem().getMethod().getName());
		vo.setReturnType(methodCombo.getSelectionModel().getSelectedItem().getMethod().getReturnType().toString());
		
		StringBuilder classPath = new StringBuilder();
		for(String path : classPathListView.getItems()){
			classPath = classPath.append(path).append(";");
		}
		
		taskProcessService = new TaskProcessService();
		taskProcessService.setClassVO(vo);
		taskProcessService.setClassPath(classPath.toString());
		taskProcessService.setPort(process.pid);
		taskProcessService.start();
		
		taskProcessService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            	startBtn.setDisable(false);
                stopBtn.setDisable(true);
                enableUI();
                startBtn.requestFocus();
            }
        });
		
		taskProcessService.setOnRunning(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            	startBtn.setDisable(true);
                stopBtn.setDisable(false);
                disableUI();
            }
        });
	}
	
	@FXML
	private void handleStopBtnAction(ActionEvent event) {
		if(taskProcessService != null){
			taskProcessService.cancel();
		}
	}
	
	@FXML
	private void handleAddClassFolderAction(ActionEvent event){
		DirectoryChooser chooser = new DirectoryChooser();
		String title = Utils.getlocalizedString("ApptracerController.select.folder.title.txt");
		chooser.setTitle(title);
		File selectedDirectory = chooser.showDialog(addClassFolderBtn.getScene().getWindow());
		if(selectedDirectory != null)
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
		if(selectedFile != null)
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
		
		List<String> paths = new ArrayList<String>();
		for(String path : classPathListView.getItems()){
			paths.add(path);
		}
		List<Class> classes = assistService.getClassesFromJar(selectedFile.getAbsolutePath(), paths);
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
	
	@FXML
	private void handleRefreshJpsAction(ActionEvent event) {
		ObservableList<ProcessVO> items = FXCollections.observableArrayList(assistService.getJavaProcess());
		processListView.setItems(items);
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

	private void disableUI(){
		addClassFolderBtn.setDisable(true);
		addJarBtn.setDisable(true);
		processListView.setDisable(true);
		addTargetJarBtn.setDisable(true);
		classCombo.setDisable(true);
		methodCombo.setDisable(true);
		targetJarTextfield.setDisable(true);
		classPathListView.setDisable(true);
		refreshBtn.setDisable(true);
	}
	
	private void enableUI(){
		addClassFolderBtn.setDisable(false);
		addJarBtn.setDisable(false);
		processListView.setDisable(false);
		addTargetJarBtn.setDisable(false);
		classCombo.setDisable(false);
		methodCombo.setDisable(false);
		targetJarTextfield.setDisable(false);
		classPathListView.setDisable(false);
		refreshBtn.setDisable(false);
	}

	private boolean validate(){
		StringBuilder sb = new StringBuilder();
		if(processListView.getSelectionModel().getSelectedItem() == null)
			sb.append(Utils.getlocalizedString("ApptracerController.validation.process.txt")).append(Constants.LINE_SEPARATOR);
		
		if(classPathListView.getItems().size() < 1)
			sb.append(Utils.getlocalizedString("ApptracerController.validation.classpath.txt")).append(Constants.LINE_SEPARATOR);
		
		if("".equals(targetJarTextfield.getText())){
			sb.append(Utils.getlocalizedString("ApptracerController.validation.target.jar.txt")).append(Constants.LINE_SEPARATOR);
		}else if(classCombo.getSelectionModel().getSelectedItem() == null){
			sb.append(Utils.getlocalizedString("ApptracerController.validation.clazz.txt")).append(Constants.LINE_SEPARATOR);
		}else if(methodCombo.getSelectionModel().getSelectedItem() == null){
			sb.append(Utils.getlocalizedString("ApptracerController.validation.method.txt")).append(Constants.LINE_SEPARATOR);
		}
		
		if("".equals(sb.toString())){
			return true;
		}else{
			DialogFX dialog = new DialogFX();
	        dialog.setTitleText(Utils.getlocalizedString("Apptracer.title.txt"));
	        dialog.setMessage(sb.toString());
	        dialog.showDialog();
	        return false;
		}
	}
}
