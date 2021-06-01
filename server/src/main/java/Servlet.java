import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import io.swagger.client.model.ErrMessage;
import io.swagger.client.model.ResultVal;
import io.swagger.client.model.TextLine;

@WebServlet(name = "Servlet", value = "/Servlet")
public class Servlet extends HttpServlet {
  Gson gson = new Gson();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws
          ServletException, IOException {
    res.setContentType("text/plain");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();
    PrintWriter out = res.getWriter();

    //Check URL
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      out.write("missing parameters");
      return;
    }
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      res.getWriter().write("GET works");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws
          ServletException, IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");

    String urlPath = req.getPathInfo();
    PrintWriter out = res.getWriter();

    ResultVal resultVal = new ResultVal();
    ErrMessage errMessage = new ErrMessage();
    TextLine textLine = new TextLine();
    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      out.write("url is empty or null");
      errMessage.setMessage("Null or empty URL");
      out.write(gson.toJson(errMessage));
      return;
    }
    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      errMessage.setMessage("invalid url");
      out.write(gson.toJson(errMessage));
      return;
    } else {
      //Send 200 response code.
      res.setStatus(HttpServletResponse.SC_OK);
      //Response body with a line's word count.
      String requestData = req.getReader().lines().collect(Collectors.joining());
      WordCount wordCount = new WordCount();
      int count = wordCount.count(requestData);
      resultVal.setMessage(count);
      out.write(gson.toJson(resultVal));
      out.close();
    }
    out.flush();
  }

  /**
   * Validate the url to see if it is valid.
   * Currently, only support the wordcount function, so only textboday/wordcount is valid.
   *
   * @param urlParts all the parts of the url
   * @return result of the validation
   */
  private boolean isUrlValid(String[] urlParts) {
    //Validate if word parts are accurate in URL
    ///textbody/{function}
    if (!urlParts[1].equals("textbody") || !urlParts[2].equals("wordcount")) {
      return false;
    }
    return true;
  }
}
