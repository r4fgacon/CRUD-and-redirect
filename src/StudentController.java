
import java.io.*;
import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class StudentController implements HttpHandler {
    private List<Student> studentList;

    // private HttpExchange httpExchange;
    public StudentController() {
        studentList = new ArrayList<>();
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        int id;
        try {
            //this.httpExchange = httpExchange;
            // System.out.println(httpExchange.getRequestURI());

            if (httpExchange.getRequestURI().toString().equals("/students/add")) {
                add(httpExchange);
            } else if (httpExchange.getRequestURI().toString().equals("/students/delete/id")) {
                id = 0;
                delete(id);
            } else if (httpExchange.getRequestURI().toString().equals("/students/edit/id")) {
                id = 0;
                edit(id);
            } else {
                index(httpExchange);
            }
        } catch (IOException e) {
            System.out.println("IOException in StudentController handle()");
        }

    }

    private void index(HttpExchange httpExchange) throws IOException {

        String response = "";
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("<html><body> \n " +
                "<form method=\"POST\"> \n" +
                "<a href=\"/students/add\">add</a>" +
                " <table style=\"width:100%\"> " +
                "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Age</th><th>Actions</th></tr>");
        for (Student student : studentList) {
            responseBuilder.append("<tr>" +
                    "<th>" + student.getId() + "</th>" +
                    "<th>" + student.getFirstName() + "</th>" +
                    "<th>" + student.getLastName() + "</th>" +
                    "<th>" + student.getAge() + "</th>" +
                    "<th>" +
                    "<a href = \"/students/delete/" + student.getId() + "\">delete</a> " +
                    "<a href = \"/students/edit/" + student.getId() + "\">edit</a>" +
                    "</th>");
        }

        responseBuilder.append("</table>" + "</body></html>");
        response = responseBuilder.toString();

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }

    private void add(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            response = "<html><body>" +
                    "<form method=\"POST\">\n" +
                    "  First name:<br>\n" +
                    "  <input type=\"text\" name=\"firstname\" >\n" +
                    "  <br>\n" +
                    "  Last name:<br>\n" +
                    "  <input type=\"text\" name=\"lastname\" >\n" +
                    "  <br>\n" +
                    "  Age:<br>\n" +
                    "  <input type=\"text\" name=\"age\" >\n" +
                    "  <br><br> \n" +
                    "  <input type=\"submit\" value=\"Submit\">\n" +
                    "</form> " +
                    "</body></html>";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
        }

        if (method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();

            Map inputs = parseFormData(formData);
            int index = studentList.size();
            String firstName = inputs.get("firstname").toString();
            String lastName = inputs.get("lastname").toString();
            int age = Integer.parseInt(inputs.get("age").toString());
            Student student = new Student(index, firstName, lastName, age);
            studentList.add(student);
            br.close();
            isr.close();


            System.out.println(httpExchange.getAttribute("Location"));
            String url = "/students";
            httpExchange.getResponseHeaders().set("Location", url);
            httpExchange.sendResponseHeaders(303, -1);
            System.out.println(httpExchange.getResponseBody().toString());


        }

    }

    private void edit(int id) {
    }

    private void delete(int id) {
    }

    /**
     * Form data is sent as a urlencoded string. Thus we have to parse this string to get data that we want.
     * See: https://en.wikipedia.org/wiki/POST_(HTTP)
     */
    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            // We have to decode the value because it's urlencoded. see: https://en.wikipedia.org/wiki/POST_(HTTP)#Use_for_submitting_web_forms
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }

}
