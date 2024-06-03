package vn.mobileid.id.FPS.controller.document.summary.module;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.component.document.module;
//
//import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
//import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
//import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
//import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
//import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;
//import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
//import java.util.Collection;
//import java.util.Iterator;
//import org.bouncycastle.asn1.ASN1Encodable;
//import org.bouncycastle.asn1.cms.Attribute;
//import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
//import org.bouncycastle.cms.CMSSignedData;
//import org.bouncycastle.cms.SignerInformation;
//import org.bouncycastle.tsp.TimeStampToken;
//
///**
// *
// * @author GiaTK
// */
//public class ReadTimestampToken {
//
//    //<editor-fold defaultstate="collapsed" desc="Read Timestamp Token">
//    public static ITimeStampToken getDocumentTimeStamp(byte[] contentBytes) {
//        try {
//            IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
//            IASN1Primitive asn1Object = BOUNCY_CASTLE_FACTORY.createASN1InputStream(contentBytes).readObject();
//            IASN1Sequence tokenSequence = BOUNCY_CASTLE_FACTORY.createASN1Sequence(asn1Object);
//            IContentInfo contentInfo = BOUNCY_CASTLE_FACTORY.createContentInfo(tokenSequence);
//            return BOUNCY_CASTLE_FACTORY.createTimeStampToken(contentInfo);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Read Signature TimeStamp">
//    public static TimeStampToken getSignatureTimeStamp(byte[] contentBytes) {
//        try {
//            CMSSignedData cmsSignedData = new CMSSignedData(contentBytes);
//            Collection<SignerInformation> signerInfos = cmsSignedData.getSignerInfos().getSigners();
//            for (Iterator it = signerInfos.iterator(); it.hasNext();) {
//                SignerInformation signer = (SignerInformation) it.next();
//                Attribute attribute = signer.getUnsignedAttributes().get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
//                if (attribute != null) {
//                    for (ASN1Encodable asn1Encodable : attribute.getAttrValues()) {
//                        CMSSignedData tstSignedData = new CMSSignedData(asn1Encodable.toASN1Primitive().getEncoded());
//                        return new TimeStampToken(tstSignedData);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//    //</editor-fold>
//}
