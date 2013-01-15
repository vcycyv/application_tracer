package net.chuyang.apptracer.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import net.chuyang.apptracer.AssistenceService;
import net.chuyang.apptracer.AssistenceService.ProcessVO;

public class ApptracerController implements Initializable {

	@FXML
	private ListView<ProcessVO> processList;

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
