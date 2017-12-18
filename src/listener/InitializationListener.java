package listener;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import model.Path;

/*
 * Webアプリケーションの開始、終了を検知するリスナー
 */

@WebListener
public class InitializationListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent arg0) {
		//アプリケーションスコープに各パスをセット
		ServletContext application = arg0.getServletContext();
		Path path = (Path) application.getAttribute("path");
		String p = arg0.getServletContext().getRealPath("");
		path = new Path(p+"/WEB-INF/models", p+"/workspace", p+"/generated", 
				p+"/WEB-INF/cakephp.zip", p+"/WEB-INF/database.php");
		application.setAttribute("path", path);
		File models = new File(path.getModels());
		if(!models.exists()) models.mkdir();
		File workspace = new File(path.getWorkspace());
		if(!workspace.exists()) workspace.mkdir();
		File generated = new File(path.getGenerated());
		if(!generated.exists()) generated.mkdir();
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
	
}
