package vn.mobileid.id.FPS.controller.document.summary.processingImpl;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.component.document.process;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import fps_core.enumration.DocumentStatus;
//import fps_core.enumration.FieldTypeName;
//import fps_core.enumration.ProcessStatus;
//import fps_core.module.DocumentUtils_itext7;
//import fps_core.objects.core.CheckBoxFieldAttribute;
//import fps_core.objects.core.ExtendedFieldAttribute;
//import fps_core.objects.FileManagement;
//import fps_core.objects.core.InitialsFieldAttribute;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import org.apache.commons.codec.binary.Hex;
//import vn.mobileid.id.FMS;
//import vn.mobileid.id.FPS.component.document.UploadDocument;
//import vn.mobileid.id.FPS.component.enterprise.ProcessModuleForEnterprise;
//import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
//import vn.mobileid.id.FPS.component.field.GetField;
//import vn.mobileid.id.FPS.controller.A_FPSConstant;
//import vn.mobileid.id.FPS.object.Document;
//import vn.mobileid.id.FPS.object.InternalResponse;
//import vn.mobileid.id.FPS.object.User;
//import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
//import vn.mobileid.id.general.LogHandler;
//import vn.mobileid.id.utils.Crypto;
//import vn.mobileid.id.utils.TaskV2;
//import vn.mobileid.id.FPS.component.document.process.interfaces.IDocumentProcessing;
//import vn.mobileid.id.FPS.component.document.process.interfaces.IModuleProcessing;
//import vn.mobileid.id.FPS.component.document.process.interfaces.IVersion;
//import static vn.mobileid.id.FPS.component.document.process.interfaces.IVersion.Version.V1;
//import static vn.mobileid.id.FPS.component.document.process.interfaces.IVersion.Version.V2;
//
///**
// *
// * @author GiaTK
// */
//class InitialsProcessing_Test extends AbstractReplicateProcessing<InitialsFieldAttribute> {
//
//    InitialsProcessing_Test(
//            InitialsFieldAttribute field,
//            Version version,
//            FieldTypeName fieldTypeName,
//            BiFunction<InitialsFieldAttribute, ExtendedFieldAttribute,Boolean> logicAddField,
//            Function<InitialsFieldAttribute, byte[]> logicProcessField,
//            Function<InitialsFieldAttribute, byte[]> logicProcessMultipleField,
//            Function<InitialsFieldAttribute, Boolean> logicUploadFMS,
//            String transactionId) {
//        super(field, version, fieldTypeName, logicAddField, logicProcessField, logicProcessMultipleField, logicUploadFMS, transactionId);
//    }
//
//    public InitialsProcessing_Test(Version version) {
//        init();
//    }
//
//    private Function<InitialsFieldAttribute, Boolean> logicAddField() {
//        return (fieldChild) -> {
//            if (fieldChild.getImage() == null
//                    || fieldChild.getImage().isEmpty()
//                    || fieldChild.getImage().length() <= 32) {
//                fieldChild.setImage(field.getImage());
//            }
//            return true;
//        };
//    }
//}
