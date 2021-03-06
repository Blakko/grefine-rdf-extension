
package org.deri.grefine.rdf.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import com.google.refine.ProjectManager;
import com.google.refine.commands.Command;
import com.google.refine.util.ParsingUtilities;

public class LoadLanguageCommand extends Command {

    public LoadLanguageCommand() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String rawDirectoryFile = request.getSession().getServletContext()
                .getRealPath("extensions/rdf-extension/module/langs/");
        String cleanedDirectory = rawDirectoryFile.replace("main" + File.separator + "webapp" + File.separator, "");

        BufferedReader reader = null;String param = null;
        try {
            param = (String) ProjectManager.singleton.getPreferenceStore().get("userLang");
        } catch (NullPointerException e) {
        }
        if (param == null) param = request.getParameter("lng");

        String[] langs = param.split(" ");
        try {
            String file = cleanedDirectory + File.separator + "translation-" + langs[0] + ".json";
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        } catch (FileNotFoundException e1) {
            try {
                String file = cleanedDirectory + File.separator + "translation-default.json";
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            } catch (FileNotFoundException e3) {
                e3.printStackTrace();
            }
        }

        String line = null;
        String message = new String();
        if (reader != null) {
            while ((line = reader.readLine()) != null) {
                // buffer.append(line);
                message += line + System.getProperty("line.separator");
            }
        }
        
        try {
        //String tmp = ParsingUtilities.encode(message);
        ParsingUtilities.evaluateJsonStringToObject(message);
        }
        catch(JSONException ex) {
                System.out.println("JSON error: " + ex.getMessage());
        }

        
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(message);
        response.getWriter().flush();
        response.getWriter().close();
    }

}