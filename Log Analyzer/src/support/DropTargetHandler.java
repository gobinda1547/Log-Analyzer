package support;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import log.analyser.LogAnalyser;
import log.analyser.LogManager;

public class DropTargetHandler extends DropTarget {

	private static final long serialVersionUID = 1L;

	public synchronized void drop(DropTargetDropEvent evt) {

		try {
			evt.acceptDrop(DnDConstants.ACTION_COPY);

			@SuppressWarnings("unchecked")
			List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

			File[] files = new File[droppedFiles.size()];
			for (int i = 0; i < files.length; i++) {
				files[i] = droppedFiles.get(i);
			}

			LogManager.addLogFile(files);

		} catch (Exception ex) {
			LogAnalyser.showMessageInConsolePanel("problem while scanning files!\n", true);
			ex.printStackTrace();
		}
	}
}