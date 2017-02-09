package servlet;

import model.Path;
import model.Form;
import model.Generation;
import model.Generator;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Servlet implementation class UploadServlet
 * ファイルアップロードサーブレット
 */
@WebServlet("/GenerateServlet")
public class GenerateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GenerateServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("index.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * POST送信された中身を確認し、ER図(クラス図)と画面遷移図をサーバ上に設置
	 * 重複しないようidを割り振る
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("<h3>500 Internal Server Error</h3>");
		
		//アプリケーションスコープからPathインスタンスを取得
		ServletContext application = this.getServletContext();
		Path path = (Path) application.getAttribute("path");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String id = RandomStringUtils.randomAlphabetic(5) + sdf.format(new Date()); //ランダムなidを割り振る
		Form form = new Form(id);
		String stName = "", dataName = "";
		File file = new File(path.getModels() + "/" + id);
		file.mkdir();
		
		//フォームの中身をチェック
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory); 
		try{
			List<FileItem> items = upload.parseRequest(request); 
			for(FileItem item : items) {//フォームの中身を1つずつ確認
				if(!item.isFormField()) {
					switch(item.getFieldName()) {
						case "stDiagram":
							if(!item.getName().equals("") && item.getName() != null) {
								stName = item.getName();
								form.setSTName(stName);
								item.write(new File(path.getModels() + "/" + id + "/" + stName));
							}
							break;
						case "dataDiagram":
							if(!item.getName().equals("") && item.getName() != null) {
								dataName = item.getName();
								form.setDataName(dataName);
								item.write(new File(path.getModels() + "/" + id + "/" + dataName));
							}
							break;
						case "diagram":
							if(!item.getName().equals("") && item.getName() != null) {
								stName = item.getName();
								dataName = item.getName();
								form.setSTName(stName);
								form.setDataName(dataName);
								item.write(new File(path.getModels() + "/" + id + "/" + stName));
							}
							break;
					}
				} else {
					switch(item.getFieldName()) {
					case "geneWay":
						form.setWay(item.getString());
						break;
					case "secuSalt":
						form.setSecuSalt(item.getString());
						break;
					case "ciphSeed":
						form.setCiphSeed(item.getString());
						break;
					case "apiKey":
						form.setApiKey(item.getString());
						break;
					}
				}
			}
		} catch (Exception e) {
			response.getWriter().write("フォームの読み込みに失敗");
			return;
		}
		
		Generator generator = new Generator(path, form);
		Generation generation = new Generation(id);
		if(generator.generate()) { //自動生成に成功
			generation.setWarnings(generator.getWarnings());
			request.setAttribute("generation", generation);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/result.jsp");
			dispatcher.forward(request, response);
		} else { //自動生成に失敗
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/error.jsp");
			generation.setErrorMessage(generator.getErrorMessage());
			request.setAttribute("generation", generation);
			dispatcher.forward(request, response);
		}
	}

}
