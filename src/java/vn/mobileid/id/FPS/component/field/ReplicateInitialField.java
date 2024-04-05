/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.interfaces.AbstractReplicateField;
import java.util.ArrayList;
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
    /**
     * Replicate the InitialField if that Object existed param replicate
     * <p>
     * @param <T>
     * @param parent
     * @param document
     * @param user
     * @param transactionId
     * @return
     * @throws Exception 
     */
    public static <T extends AbstractReplicateField> InternalResponse replicateField(
            T parent,
            Document document,
            User user,
            String transactionId
    ) throws Exception {
        boolean enabled = false;

        //<editor-fold defaultstate="collapsed" desc="Delete duplicate page existed in field.replicatePages">
        List<Integer> replicatePages = new ArrayList<>();
        if (!Utils.isNullOrEmpty(parent.getReplicatePages())) {
            for (int i = 0; i < parent.getReplicatePages().size(); i++) {
                int p = parent.getPage();
                int pp = parent.getReplicatePages().get(i);
                if (!replicatePages.contains(parent.getReplicatePages().get(i))
                        && (parent.getReplicatePages().get(i) != parent.getPage())) {
                    replicatePages.add(parent.getReplicatePages().get(i));
                }
            }
        }
        if (replicatePages.isEmpty()) {
            replicatePages = parent.getReplicatePages();
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create list child field">
        List<T> childs = new ArrayList<>();
        if (parent.isReplicateAllPages()) {
            long root = System.currentTimeMillis();
            for (int i = 1; i <= document.getDocumentPages(); i++) {
                if (i == parent.getPage()) {
                    continue;
                }
                T child = (T) parent.getClass().newInstance();
                child.setApplyToAll(false);
                child.setPage(i);
                child.setDimension(parent.getDimension());
                child.setType(parent.getType());
                child.setVisibleEnabled(true);
                child.setRemark(parent.getRemark());
                child.setRequired(true);

                int position = parent.getFieldName().lastIndexOf("_") == -1
                        ? parent.getFieldName().length() - 1
                        : parent.getFieldName().lastIndexOf("_");
                String buffer = parent.getFieldName().lastIndexOf("_") == -1
                        ? "_"
                        : "";
                try {
                    String suffix = Utils.hashAndExtractMiddleSixChars("SIGNATURE-" + String.valueOf(root));
                    child.setSuffix(suffix);
                    child.setFieldName(
                            parent.getFieldName()
                                    .substring(0,
                                            position + 1)
                            + buffer
                            + suffix);
                    root++;
                } catch (Exception ex) {
                    System.err.println("Invalid hash algorithm!!");
                    child.setFieldName(parent.getFieldName()
                            .substring(0,
                                    position + 1)
                            + buffer
                            + String.valueOf(root));
                }
                childs.add(child);
            }
            enabled = true;
        } else if (!Utils.isNullOrEmpty(replicatePages)) {
            final long rootTimestamp = System.currentTimeMillis();
            Iterator<Integer> temp = replicatePages.iterator();
            while (temp.hasNext()) {
                int page = temp.next();
                T child = (T)parent.getClass().newInstance();
                child.setApplyToAll(false);
                child.setPage(page);
                child.setDimension(parent.getDimension());
                child.setType(parent.getType());
                child.setVisibleEnabled(true);
                child.setRemark(parent.getRemark());
                child.setRequired(true);
                int position = parent.getFieldName().lastIndexOf("_") == -1
                        ? parent.getFieldName().length() - 1
                        : parent.getFieldName().lastIndexOf("_");
                String buffer = parent.getFieldName().lastIndexOf("_") == -1
                        ? "_"
                        : "";
                try {
                    String suffix = Utils.hashAndExtractMiddleSixChars("SIGNATURE-" + String.valueOf(
                            rootTimestamp + page
                    ));
                    child.setSuffix(suffix);
                    child.setFieldName(
                            parent.getFieldName()
                                    .substring(0,
                                            position + 1)
                            + buffer
                            + suffix);
                } catch (Exception ex) {
                    System.err.println("Invalid hash algorithm!!");
                    child.setFieldName(parent.getFieldName()
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

        //<editor-fold defaultstate="collapsed" desc="Create Child Field">
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
                    List<Object> group_a = (List<Object>) this.get()[1];

                    for (Object child : group_a) {
                        BasicFieldAttribute temp = (BasicFieldAttribute) child;
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
                                    error.setName(temp.getFieldName());
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
                                    temp.getType().getTypeId(),
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);
                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(temp.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                            }
                            //</editor-fold>
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            synchronized (this) {
                                InternalData error = new InternalResponse.InternalData();
                                error.setName(temp.getFieldName());
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
                    List<Object> group_a = (List<Object>) this.get()[1];

                    for (Object child : group_a) {
                        BasicFieldAttribute temp = (BasicFieldAttribute) child;
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
                                    error.setName(temp.getFieldName());
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
                                    temp.getType().getTypeId(),
                                    child,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);
                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                synchronized (this) {
                                    InternalData error = new InternalResponse.InternalData();
                                    error.setName(temp.getFieldName());
                                    error.setValue(response);
                                    problemOccur.add(error);
                                }
                            }
                            //</editor-fold>
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            synchronized (this) {
                                InternalData error = new InternalResponse.InternalData();
                                error.setName(temp.getFieldName());
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
