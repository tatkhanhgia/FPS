///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.object;
//
//import java.util.List;
//
///**
// *
// * @author GiaTK
// */
//public class FileManagement {
//    private String name;
//    private int page;
//    private float width;
//    private float height;
//    private long size;
//    private int rotate;
//    private String digest;
//    private HashAlgorithm algorithm = HashAlgorithm.HASH_MD5;
//    private DocumentType documentType = DocumentType.PDF;
//    private List<CustomPageSize> documentCustom;
//
//    public FileManagement() {
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getPage() {
//        return page;
//    }
//
//    public void setPages(int page) {
//        this.page = page;
//    }
//
//    public float getWidth() {
//        return width;
//    }
//
//    public void setWidth(float width) {
//        this.width = width;
//    }
//
//    public float getHeight() {
//        return height;
//    }
//
//    public void setHeight(float height) {
//        this.height = height;
//    }
//
//    public long getSize() {
//        return size;
//    }
//
//    public void setSize(long size) {
//        this.size = size;
//    }        
//
//    public String getDigest() {
//        return digest;
//    }
//
//    public void setDigest(String digest) {
//        this.digest = digest;
//    }
//
//    public HashAlgorithm getAlgorithm() {
//        return algorithm;
//    }
//
//    public void setAlgorithm(HashAlgorithm algorithm) {
//        this.algorithm = algorithm;
//    }
//
//    public DocumentType getDocumentType() {
//        return documentType;
//    }
//
//    public void setDocumentType(DocumentType documentType) {
//        this.documentType = documentType;
//    }
//
//    public List<CustomPageSize> getDocumentCustom() {
//        return documentCustom;
//    }
//
//    public void setDocumentCustom(List<CustomPageSize> documentCustom) {
//        this.documentCustom = documentCustom;
//    }
//
//    public int getRotate() {
//        return rotate;
//    }
//
//    public void setRotate(int rotate) {
//        this.rotate = rotate;
//    }
//    
//    public enum HashAlgorithm{
//        HASH_SHA1("SHA-1"),
//        HASH_MD5("MD5"),
//        HASH_SHA256("SHA-256"),
//        HASH_SHA384("SHA-384"),
//        HASH_SHA512("SHA-512");
//        private String name;
//
//        private HashAlgorithm(String name) {
//            this.name = name;
//        }
//
//        public String getName() {
//            return name;
//        }                
//    }
//}
