package edu.spbpu.logic;

import edu.spbpu.logic.actions.data.DownloadAction;
import edu.spbpu.logic.actions.data.GetFileAction;
import edu.spbpu.logic.actions.data.GetFormAction;
import edu.spbpu.logic.actions.inverters.InverterInfo;
import edu.spbpu.logic.actions.modules.ModuleInfo;
import edu.spbpu.logic.actions.modules.ModuleList;
import edu.spbpu.logic.actions.project.*;
import edu.spbpu.logic.actions.project.local_data.LocalDataInfo;
import edu.spbpu.logic.actions.project.module.OperationsInfo;
import edu.spbpu.logic.actions.project.string.CableEditGET;
import edu.spbpu.logic.actions.project.string.CableEditPOST;
import edu.spbpu.logic.actions.project.string.CableInfo;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vasiliy Bobkov on 19.01.2017.
 */
@Log4j
public class ActionFactory {
    private Map<String, Action> actions = new ConcurrentHashMap<>();
    public static final ActionFactory INSTANCE = new ActionFactory();

    public ActionFactory() {
        actions.put("GET/", (r, s) -> "/index.jsp");
        actions.put("GET/data/my", (r, s) -> "/WEB-INF/jsp/data/my.jsp");
        actions.put("GET/data/get", new GetFormAction());
        actions.put("GET/data/get_file", new GetFileAction());
        actions.put("POST/data/download", new DownloadAction());
        actions.put("GET/projects/list", new ListOfPorjects());
        actions.put("GET/projects/add", (r, s) -> "/WEB-INF/jsp/projects/add.jsp");
        actions.put("POST/projects/add", new NewProjectPost());
        actions.put("GET/modules/list", new ModuleList());
        actions.put("GET/projects/edit", new EditProjectGet());
        actions.put("POST/projects/edit", new EditProjectPost());
        actions.put("GET/projects/info", new Info());
        actions.put("POST/projects/calculate", new Calculator());
        actions.put("GET/projects/module/operations", new OperationsInfo());
        actions.put("GET/inverters/info", new InverterInfo());
        actions.put("GET/modules/info", new ModuleInfo());
        actions.put("GET/projects/string/cable_edit", new CableEditGET());
        actions.put("GET/projects/cable/info", new CableInfo());
        actions.put("POST/projects/string/cable_edit", new CableEditPOST());
        actions.put("GET/projects/annual_info", new AnnualInfo());
        actions.put("GET/projects/local_data/info", new LocalDataInfo());
    }

    public Action getAction(HttpServletRequest request) {
        StringBuilder actionKey = new StringBuilder(request.getMethod());
        actionKey.append(request.getServletPath());
        if (request.getPathInfo() != null) actionKey.append(request.getPathInfo());

        log.info("New request: " + actionKey.toString());
        Action action = actions.get(actionKey.toString());
        return action != null ? action : (r, s) -> "/WEB-INF/jsp/404.jsp";
    }
}