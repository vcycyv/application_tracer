package net.chuyang.apptracer.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import net.chuyang.apptracer.AssistenceService;
import net.chuyang.apptracer.TaskProcessor;
import net.chuyang.apptracer.AssistenceService.ProcessVO;
import net.chuyang.apptracer.codegen.ReturnValueVO;
import net.chuyang.apptracer.Configuration;

public class ApptracerController implements Initializable {

	@FXML
	private ListView<ProcessVO> processList;
	@FXML
	private Button startBtn;
	@FXML
	private Button stopBtn;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		AssistenceService service = new AssistenceService();
		ObservableList<ProcessVO> items = FXCollections.observableArrayList(service.getJavaProcess());
		processList.setItems(items);
		processList.setCellFactory(new Callback<ListView<ProcessVO>, ListCell<ProcessVO>>() {
			@Override
			public ListCell<ProcessVO> call(ListView<ProcessVO> list) {
				return new ProcessVOCell();
			}
		});
		
		
	}
	
	@FXML
	private void handleStartBtnAction(ActionEvent event){
		ProcessVO process = processList.getSelectionModel().getSelectedItem();
		Configuration.INSTANCE.setTargetPort(process.pid);
		ReturnValueVO vo = new ReturnValueVO();
		vo.setClazz("CaseObject");
		vo.setMethod("execute");
		vo.setReturnType("boolean");
		TaskProcessor.INSTANCE.handleReturnValueTask(vo);
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
}
