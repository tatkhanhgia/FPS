/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.systemManagement;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.AuthorizeController;
import vn.mobileid.id.FPS.controller.DocumentController;
import vn.mobileid.id.FPS.controller.FMSController;
import vn.mobileid.id.FPS.controller.FieldController;
import vn.mobileid.id.FPS.controller.UtilsController;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ManagementController extends HttpServlet {

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        
        String documentPattern = getInitParameter("documentPattern");
        String fieldPattern = getInitParameter("fieldPattern");
        String authorizePattern = getInitParameter("authorizePattern");
        String FMSPattern = getInitParameter("FMSPattern");
        String uri = req.getRequestURI();
        if (uri.matches(documentPattern)) {
            DocumentController.service_(req, res);
            return;
        } else if (uri.matches(fieldPattern)) {
            FieldController.service_(req, res);
            return;
        } else if (uri.matches(authorizePattern)) {
            AuthorizeController.service_(req, res);
            return;
        } else if (uri.matches("^/fps/v1/documents/fields")) {
            UtilsController.service_(req, res);
            return;
        } else if (uri.matches(FMSPattern)){
            FMSController.service_(req, res);
            return;
        }
        Utils.sendMessage(
                res,
                A_FPSConstant.HTTP_CODE_NOT_FOUND,
                "application/json",
                null,
                "none");
    }
}
