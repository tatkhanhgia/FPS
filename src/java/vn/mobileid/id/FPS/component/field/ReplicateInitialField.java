/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import fps_core.objects.InitialsFieldAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.InternalResponse.InternalData;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.utils.TaskV2;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ReplicateInitialField {

    //<editor-fold defaultstate="collapsed" desc="Replicate Field Version1">
    public static InternalResponse replicateField(
            InitialsFieldAttribute initParent,
            Document document,
            User user,
            String transactionId
    ) throws Exception {
        boolean enabled = false;

        //<editor-fold defaultstate="collapsed" desc="Create list child field">
        List<InitialsFieldAttribute> childs = new ArrayList<>();
        if (initParent.isApplyToAll()) {
            long root = System.currentTimeMillis();
            for (int i = 1; i <= document.getDocumentPages(); i++) {
                InitialsFieldAttribute child = new InitialsFieldAttribute();
                child.setApplyToAll(false);
                child.setPages(Arrays.asList(i));
                child.setPage(i);
                child.setDimension(initParent.getDimension());
                child.setType(initParent.getType());
                child.setVisibleEnabled(true);
                try {
                    child.setFieldName(
                            initParent.getFieldName()
                                    .substring(0,
                                            initParent.getFieldName().lastIndexOf("_") + 1)
                            + Utils.hashAndExtractMiddleSixChars("SIGNATURE-" + String.valueOf(root)));
                    root++;
                } catch (Exception ex) {
                    System.err.println("Invalid hash algorithm!!");
                    child.setFieldName(initParent.getFieldName()
                            .substring(0,
                                    initParent.getFieldName().lastIndexOf("_") + 1)
                            + String.valueOf(root));
                }
                childs.add(child);
            }
            enabled = true;
        } else if (!Utils.isNullOrEmpty(initParent.getReplicatePages())) {
            final long rootTimestamp = System.currentTimeMillis();
            Iterator<Integer> temp = initParent.getReplicatePages().iterator();
            while (temp.hasNext()) {
                int page = temp.next();
                InitialsFieldAttribute child = new InitialsFieldAttribute();
                child.setApplyToAll(false);
                child.setPages(Arrays.asList(page));
                child.setPage(page);
                child.setDimension(initParent.getDimension());
                child.setType(initParent.getType());
                child.setVisibleEnabled(true);
                int position = initParent.getFieldName().lastIndexOf("_") == -1
                        ? initParent.getFieldName().length() - 1
                        : initParent.getFieldName().lastIndexOf("_");
                String buffer = initParent.getFieldName().lastIndexOf("_") == -1
                        ? "_"
                        : "";
                try {
                    child.setFieldName(
                            initParent.getFieldName()
                                    .substring(0,
                                            position + 1)
                            + buffer
                            + Utils.hashAndExtractMiddleSixChars("SIGNATURE-" + String.valueOf(
                                    rootTimestamp + page
                            )));
                } catch (Exception ex) {
                    System.err.println("Invalid hash algorithm!!");
                    child.setFieldName(initParent.getFieldName()
                            .substring(0,
                                    position + 1)
                            + buffer
                            + String.valueOf(rootTimestamp + page));
                }
                childs.add(child);
            }
            enabled = true;
        }
        //</editor-fold>

        if (Utils.isNullOrEmpty(childs)) {
            if (enabled) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_INITIAL,
                        A_FPSConstant.SUBCODE_ERROR_WHILE_PROCESSING_MULTI_THREAD
                );
            }
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        }

        //<editor-fold defaultstate="collapsed" desc="Create Initial Child Field">
        ExecutorService executors = Executors.newFixedThreadPool(2);
        final List<InternalResponse.InternalData> problemOccur = new ArrayList<>();

        //Split into 2 thread => increase performance
        int median = childs.size() % 2 == 0 ? childs.size() / 2 : (childs.size() + 1) / 2;

        //<editor-fold defaultstate="collapsed" desc="Thread 1 (0 to median)">
        Future<Object> thread1 = executors.submit(new TaskV2(
                new Object[]{
                    document.getId(),
                    childs.subList(0, median),
                    user
                },
                null) {
            @Override
            public Object call() {
                try {
                    User user = (User) this.get()[2];
                    List<InitialsFieldAttribute> group_a = (List<InitialsFieldAttribute>) this.get()[1];

                    for (InitialsFieldAttribute child : group_a) {
                        try {
                            //<editor-fold defaultstate="collapsed" desc="Add Field">
                            InternalResponse response = AddField.addField(
                                    (long) this.get()[0],
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);

                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(child.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                                continue;
                            }
                            int documentFieldId = (int) response.getData();
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Add Field Details">
                            response = AddField.addDetailField(
                                    documentFieldId,
                                    child.getType().getTypeId(),
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);
                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(child.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                            }
                            //</editor-fold>
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            synchronized (this) {
                                InternalData error = new InternalResponse.InternalData();
                                error.setName(child.getFieldName());
                                error.setValue(new InternalResponse(
                                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                        ""
                                ).setException(ex));
                                problemOccur.add(error);
                            }
                        }
                    }
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_SUCCESS,
                            ""
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                            ""
                    ).setException(ex);
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Thread 2 (median to end)">
        Future<Object> thread2 = executors.submit(new TaskV2(
                new Object[]{
                    document.getId(),
                    childs.subList(median, childs.size()),
                    user
                },
                null) {
            @Override
            public Object call() {
                try {
                    User user = (User) this.get()[2];
                    List<InitialsFieldAttribute> group_a = (List<InitialsFieldAttribute>) this.get()[1];

                    for (InitialsFieldAttribute child : group_a) {
                        try {
                            //<editor-fold defaultstate="collapsed" desc="Add Field">
                            InternalResponse response = AddField.addField(
                                    (long) this.get()[0],
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);

                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(child.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                                continue;
                            }
                            int documentFieldId = (int) response.getData();
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Add Field Details">
                            response = AddField.addDetailField(
                                    documentFieldId,
                                    child.getType().getTypeId(),
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);
                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(child.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                            }
                            //</editor-fold>
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            synchronized (this) {
                                InternalData error = new InternalResponse.InternalData();
                                error.setName(child.getFieldName());
                                error.setValue(new InternalResponse(
                                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                        ""
                                ).setException(ex));
                                problemOccur.add(error);
                            }
                        }
                    }
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_SUCCESS,
                            ""
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                            ""
                    ).setException(ex);
                }
            }
        });
        //</editor-fold>

        executors.shutdown();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check response">
        InternalResponse response_thread1 = (InternalResponse) thread1.get();
        System.out.println("response1:" + response_thread1.getStatus());
        InternalResponse response_thread2 = (InternalResponse) thread2.get();
        System.out.println("response2:" + response_thread2.getStatus());

        if (response_thread1.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response_thread1.setInternalData(new InternalData().setValue(problemOccur));
        }
        if (response_thread2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response_thread2.setInternalData(new InternalData().setValue(problemOccur));
        }
        if (!Utils.isNullOrEmpty(problemOccur)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    ""
            ).setInternalData(new InternalData().setValue(problemOccur));
        }
        //</editor-fold>

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Replicate Field Version2">
    public static InternalResponse replicateFieldV2(
            InitialsFieldAttribute initParent,
            List<InitialsFieldAttribute> listInit,
            Document document,
            User user,
            String transactionId
    ) throws Exception {
        boolean enabled = false;

        //<editor-fold defaultstate="collapsed" desc="Create list child field">
        List<InitialsFieldAttribute> childs = new ArrayList<>();
        if (initParent.isApplyToAll()) {
            long root = System.currentTimeMillis();
            for (int i = 1; i <= document.getDocumentPages(); i++) {
                InitialsFieldAttribute child = new InitialsFieldAttribute();
                child.setApplyToAll(false);
                child.setPages(Arrays.asList(i));
                child.setPage(i);
                child.setDimension(initParent.getDimension());
                child.setType(initParent.getType());
                child.setVisibleEnabled(true);
                try {
                    child.setFieldName(
                            initParent.getFieldName()
                                    .substring(0,
                                            initParent.getFieldName().lastIndexOf("_") + 1)
                            + Utils.hashAndExtractMiddleSixChars("SIGNATURE-" + String.valueOf(root)));
                    root++;
                } catch (Exception ex) {
                    System.err.println("Invalid hash algorithm!!");
                    child.setFieldName(initParent.getFieldName()
                            .substring(0,
                                    initParent.getFieldName().lastIndexOf("_") + 1)
                            + String.valueOf(root));
                }
                childs.add(child);
            }
            enabled = true;
        } else if (!Utils.isNullOrEmpty(initParent.getReplicatePages())) {
            final long rootTimestamp = System.currentTimeMillis();
            Iterator<Integer> temp = initParent.getReplicatePages().iterator();
            while (temp.hasNext()) {
                int page = temp.next();
                InitialsFieldAttribute child = new InitialsFieldAttribute();
                child.setApplyToAll(false);
                child.setPages(Arrays.asList(page));
                child.setPage(page);
                child.setDimension(initParent.getDimension());
                child.setType(initParent.getType());
                child.setVisibleEnabled(true);
                int position = initParent.getFieldName().lastIndexOf("_") == -1
                        ? initParent.getFieldName().length() - 1
                        : initParent.getFieldName().lastIndexOf("_");
                String buffer = initParent.getFieldName().lastIndexOf("_") == -1
                        ? "_"
                        : "";
                try {
                    child.setFieldName(
                            initParent.getFieldName()
                                    .substring(0,
                                            position + 1)
                            + buffer
                            + Utils.hashAndExtractMiddleSixChars("SIGNATURE-" + String.valueOf(
                                    rootTimestamp + page
                            )));
                } catch (Exception ex) {
                    System.err.println("Invalid hash algorithm!!");
                    child.setFieldName(initParent.getFieldName()
                            .substring(0,
                                    position + 1)
                            + buffer
                            + String.valueOf(rootTimestamp + page));
                }
                childs.add(child);
            }
            enabled = true;
        }
        //</editor-fold>

        if (Utils.isNullOrEmpty(childs)) {
            if (enabled) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_INITIAL,
                        A_FPSConstant.SUBCODE_ERROR_WHILE_PROCESSING_MULTI_THREAD
                );
            }
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        }

        //<editor-fold defaultstate="collapsed" desc="Create Initial Child Field">
        ExecutorService executors = Executors.newFixedThreadPool(2);
        final List<InternalResponse.InternalData> problemOccur = new ArrayList<>();

        //Split into 2 thread => increase performance
        int median = childs.size() % 2 == 0 ? childs.size() / 2 : (childs.size() + 1) / 2;

        //<editor-fold defaultstate="collapsed" desc="Thread 1 (0 to median)">
        Future<Object> thread1 = executors.submit(new TaskV2(
                new Object[]{
                    document.getId(),
                    childs.subList(0, median),
                    user
                },
                null) {
            @Override
            public Object call() {
                try {
                    User user = (User) this.get()[2];
                    List<InitialsFieldAttribute> group_a = (List<InitialsFieldAttribute>) this.get()[1];

                    for (InitialsFieldAttribute child : group_a) {
                        try {
                            //<editor-fold defaultstate="collapsed" desc="Add Field">
                            InternalResponse response = AddField.addField(
                                    (long) this.get()[0],
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);

                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(child.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                                continue;
                            }
                            int documentFieldId = (int) response.getData();
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Add Field Details">
                            response = AddField.addDetailField(
                                    documentFieldId,
                                    child.getType().getTypeId(),
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);
                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(child.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                            }
                            //</editor-fold>
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            synchronized (this) {
                                InternalData error = new InternalResponse.InternalData();
                                error.setName(child.getFieldName());
                                error.setValue(new InternalResponse(
                                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                        ""
                                ).setException(ex));
                                problemOccur.add(error);
                            }
                        }
                    }
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_SUCCESS,
                            ""
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                            ""
                    ).setException(ex);
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Thread 2 (median to end)">
        Future<Object> thread2 = executors.submit(new TaskV2(
                new Object[]{
                    document.getId(),
                    childs.subList(median, childs.size()),
                    user
                },
                null) {
            @Override
            public Object call() {
                try {
                    User user = (User) this.get()[2];
                    List<InitialsFieldAttribute> group_a = (List<InitialsFieldAttribute>) this.get()[1];

                    for (InitialsFieldAttribute child : group_a) {
                        try {
                            //<editor-fold defaultstate="collapsed" desc="Add Field">
                            InternalResponse response = AddField.addField(
                                    (long) this.get()[0],
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);

                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(child.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                                continue;
                            }
                            int documentFieldId = (int) response.getData();
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Add Field Details">
                            response = AddField.addDetailField(
                                    documentFieldId,
                                    child.getType().getTypeId(),
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);
                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(child.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                            }
                            //</editor-fold>
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            synchronized (this) {
                                InternalData error = new InternalResponse.InternalData();
                                error.setName(child.getFieldName());
                                error.setValue(new InternalResponse(
                                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                        ""
                                ).setException(ex));
                                problemOccur.add(error);
                            }
                        }
                    }
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_SUCCESS,
                            ""
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                            ""
                    ).setException(ex);
                }
            }
        });
        //</editor-fold>

        executors.shutdown();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check response">
        InternalResponse response_thread1 = (InternalResponse) thread1.get();
        System.out.println("response1:" + response_thread1.getStatus());
        InternalResponse response_thread2 = (InternalResponse) thread2.get();
        System.out.println("response2:" + response_thread2.getStatus());

        if (response_thread1.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response_thread1.setInternalData(new InternalData().setValue(problemOccur));
        }
        if (response_thread2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response_thread2.setInternalData(new InternalData().setValue(problemOccur));
        }
        if (!Utils.isNullOrEmpty(problemOccur)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    ""
            ).setInternalData(new InternalData().setValue(problemOccur));
        }
        //</editor-fold>

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    public static void main(String[] args) {
        for (int i = 0; i <= 10; i++) {
            System.out.println("First:" + i);
        }
        ExecutorService executors = Executors.newFixedThreadPool(1);
        executors.submit(new TaskV2(null, null) {
            @Override
            public Object call() {
                for (int i = 0; i <= 100; i++) {
                    System.out.println("Thread:" + i);
                }
                return null;
            }
        });
        executors.shutdown();
        for (int i = 0; i <= 100; i++) {
            System.out.println("After:" + i);
        }
    }
}
