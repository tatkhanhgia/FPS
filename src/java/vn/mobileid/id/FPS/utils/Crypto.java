/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import sun.misc.BASE64Decoder;
import sun.security.provider.X509Factory;
import vn.mobileid.id.FPS.systemManagement.LogHandler;

/**
 *
 * @author VUDP
 */
public class Crypto {

    private static final Logger LOG = LogManager.getLogger(Crypto.class);

    final public static String HASH_MD5 = "MD5";
    final public static String HASH_SHA1 = "SHA-1";
    final public static String HASH_SHA256 = "SHA-256";
    final public static String HASH_SHA384 = "SHA-384";
    final public static String HASH_SHA512 = "SHA-512";

    final public static String HASH_SHA1_ = "SHA1";
    final public static String HASH_SHA256_ = "SHA256";
    final public static String HASH_SHA384_ = "SHA384";
    final public static String HASH_SHA512_ = "SHA512";

    final public static String SIG_ALO_SHA256_RSA = "SHA256withRSA";

    final public static int HASH_MD5_LEN = 16;
    final public static int HASH_MD5_LEN_PADDED = 34;

    final public static int HASH_SHA1_LEN = 20;
    final public static int HASH_SHA1_LEN_PADDED = 35;

    final public static int HASH_SHA256_LEN = 32;
    final public static int HASH_SHA256_LEN_PADDED = 51;

    final public static int HASH_SHA384_LEN = 48;
    final public static int HASH_SHA384_LEN_PADDED = 67;

    final public static int HASH_SHA512_LEN = 64;
    final public static int HASH_SHA512_LEN_PADDED = 83;

    final public static String KEY_ALGORITHM_RSA = "RSA";
    final public static String KEY_ALGORITHM_DSA = "DSA";

    final public static String CHARSET_UTF8 = "UTF-8";
    final public static String CHARSET_UTF16LE = "UTF-16LE";
    final public static String CHARSET_UTF16BE = "UTF-16BE";
    final public static String BASE64 = "BASE64";

    final public static String SECURE_BLACKBOX_LICENSE = "A6FF3228BE7138FECDEC31C2C99A5AA8F210D38478CD1C257489A48892330D033BF93983DC971DBB8F6665BCB6298984EE82265EE5C4416B7EB7396E33150675C69BF663B9EAE3D2A96D8C523BF1C5A2B4A09D16A8CD905C87A05EE80726DC0491382879DC4E23DF64888841704169E5CDD8157A7A9A782211A31EBA8531406FD3AF310E3AF618070CC280E98EDB522F57C9A8A5A3BE2A60E0B55486512A44B12B014E8B3C499D082D9F84FCD62FA560C29F54513F1B76DC7B92116CE741BD17080040C65F838E4DEE7744F5D7A6257740E8077EFF01C1B57A661AD51C83D94BA962707FFAE0C25EBFDBBDF7DC5A3A92DBD8C60FCED08AF7F874F3A02805C3D7";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static long crc32(String data) {
        byte bytes[] = data.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        long checksumValue = checksum.getValue();
        return checksumValue;
    }

    public static byte[] hashData(byte[] data, String algorithm) {
        byte[] result = null;
        try {
            if (algorithm.compareToIgnoreCase(HASH_MD5) == 0) {
                algorithm = HASH_MD5;
            } else if (algorithm.compareToIgnoreCase(HASH_SHA1) == 0
                    || algorithm.compareToIgnoreCase(HASH_SHA1_) == 0) {
                algorithm = HASH_SHA1;
            } else if (algorithm.compareToIgnoreCase(HASH_SHA256) == 0
                    || algorithm.compareToIgnoreCase(HASH_SHA256_) == 0) {
                algorithm = HASH_SHA256;
            } else if (algorithm.compareToIgnoreCase(HASH_SHA384) == 0
                    || algorithm.compareToIgnoreCase(HASH_SHA384_) == 0) {
                algorithm = HASH_SHA384;
            } else if (algorithm.compareToIgnoreCase(HASH_SHA512) == 0
                    || algorithm.compareToIgnoreCase(HASH_SHA512_) == 0) {
                algorithm = HASH_SHA512;
            } else {
                algorithm = HASH_SHA256;
            }
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            result = md.digest();
        } catch (NoSuchAlgorithmException e) {
            
            e.printStackTrace();
        }
        return result;
    }

    public static String hashPass(byte[] data) {
        return DatatypeConverter.printHexBinary(hashData(hashData(data, HASH_SHA384), HASH_SHA384));
    }

    public static PublicKey getPublicKeyInPemFormat(String data) {
        PublicKey pubKeyString = null;
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary(data));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            pubKeyString = kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubKeyString;
    }

    public static PublicKey getPublicKeyInHexFormat(String data) {
        PublicKey pubKeyString = null;
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(DatatypeConverter.parseHexBinary(data));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            pubKeyString = kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubKeyString;
    }

    public static X509Certificate getX509Object(String pem) {
        X509Certificate x509 = null;
        try {
            CertificateFactory certFactoryChild = CertificateFactory
                    .getInstance("X.509", "BC");
            InputStream inChild = new ByteArrayInputStream(
                    getX509Der(pem));
            x509 = (X509Certificate) certFactoryChild
                    .generateCertificate(inChild);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return x509;
    }

    public static byte[] getX509CertificateEncoded(X509Certificate x509) {
        byte[] data = null;
        try {
            data = x509.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
           
        }
        return data;
    }

    public static byte[] getPublicKeyEncoded(X509Certificate x509) {
        byte[] data = null;
        try {
            data = x509.getPublicKey().getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return data;
    }

    public static int checkCertificateValidity(X509Certificate x509) {
        int status;
        try {
            x509.checkValidity();
            status = 0;
        } catch (CertificateExpiredException e) {
            e.printStackTrace();
            status = 1;
        } catch (CertificateNotYetValidException e) {
            e.printStackTrace();
            status = -1;
        }
        return status;
    }

    private static byte[] getX509Der(String base64Str)
            throws Exception {
        byte[] binary = null;
        if (base64Str.indexOf("-----BEGIN CERTIFICATE-----") != -1) {
            binary = base64Str.getBytes();
        } else {
            binary = DatatypeConverter.parseBase64Binary(base64Str);
        }
        return binary;
    }

    public static SecretKey computeSecretKey(String keyType, byte[] rawSecretKey) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(rawSecretKey, keyType);
        return (SecretKey) secretKeySpec;
    }

    public static byte[] wrapSecrectKey(
            String algWrapping, //AES - DES - DSA - EC - GCM - PBE - RC2 - (RSASSA-PSS)
            SecretKey wrappingKey,
            byte[] wrappingIv,
            Key keyToBeWrapped
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidParameterSpecException, IllegalBlockSizeException, NoSuchProviderException {
        Cipher wrappingCipher = Cipher.getInstance(algWrapping);
        String[] list = algWrapping.split("/");
        AlgorithmParameters algParams = AlgorithmParameters.getInstance(list[0]);
        algParams.init(new IvParameterSpec(wrappingIv));
        wrappingCipher.init(Cipher.WRAP_MODE, wrappingKey, algParams);          
        return wrappingCipher.wrap(keyToBeWrapped);
    }

    public static Key unwrapSecrectKey(
            String algWrap,
            String wrappedKeyAlgorithm,
            SecretKey wrappingKey,
            byte[] wrappingIv,
            byte[] wrappedKey,
            int wrappedKeyType
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidParameterSpecException, IllegalBlockSizeException, NoSuchProviderException {
        Cipher wrappingCipher = Cipher.getInstance(algWrap);
        String[] list = algWrap.split("/");
        AlgorithmParameters algParams = AlgorithmParameters.getInstance(list[0]);
        algParams.init(new IvParameterSpec(wrappingIv));
        wrappingCipher.init(Cipher.UNWRAP_MODE, wrappingKey, algParams);
        return wrappingCipher.unwrap(wrappedKey, wrappedKeyAlgorithm, wrappedKeyType);
    }

    public static byte[] encrypt(String encryptType, SecretKey key, byte[] initVector, byte[] data)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        IvParameterSpec iv = new IvParameterSpec(initVector);
        Cipher cipher = Cipher.getInstance(encryptType);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(data);
        return encrypted;

    }

    public static byte[] decrypt(String encryptType, SecretKey key, byte[] initVector, byte[] encoded)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        IvParameterSpec iv = new IvParameterSpec(initVector);
        Cipher cipher = Cipher.getInstance(encryptType);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] data = cipher.doFinal(encoded);
        return data;
    }

//    public static List<Certificate> getCertificateChain(String caCert1, String caCert2, X509Certificate cert) {
//        X509Certificate endCert = null;
//        X509Certificate ca1 = null;
//        X509Certificate ca2 = null;
//        endCert = cert;
//        ca1 = getX509Object(caCert1);
//        try {
//            endCert.verify(ca1.getPublicKey());
//            Collection<Certificate> certChain = CertTools.getCertsFromPEM(new ByteArrayInputStream(caCert1.getBytes()));
//            certChain.add((Certificate) endCert);
//
//            List<Certificate> certificates = new ArrayList(certChain);
//            Collections.reverse(certificates);
//            return certificates;
//        } catch (Exception e) {
//            if (LogHandler.isShowWarnLog()) {
//                LOG.warn("First CA certificate isn't the one who issues end-user certificate. Try the second one");
//            }
//            ca2 = getX509Object(caCert2);
//            try {
//                endCert.verify(ca2.getPublicKey());
//                Collection<Certificate> certChain = CertTools.getCertsFromPEM(new ByteArrayInputStream(caCert2.getBytes()));
//                certChain.add((Certificate) endCert);
//
//                List<Certificate> certificates = new ArrayList(certChain);
//                Collections.reverse(certificates);
//                return certificates;
//            } catch (Exception exx) {
//                exx.printStackTrace();
//                return null;
//            }
//        }
//    }

    public static String signWithKeyStore(
            String data,
            String keystorePath,
            String keystorePassword,
            String keystoreType) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore keystore = KeyStore.getInstance(keystoreType);
        Signature sig;
        try ( InputStream is = new FileInputStream(keystorePath)) {
            keystore.load(is, keystorePassword.toCharArray());
            Enumeration<String> e = keystore.aliases();
            String aliasName;
            PrivateKey key = null;
            while (e.hasMoreElements()) {
                aliasName = e.nextElement();
                key = (PrivateKey) keystore.getKey(aliasName,
                        keystorePassword.toCharArray());
                if (key != null) {
                    break;
                }
            }
            sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(key);
            sig.update(data.getBytes());
        }
        return DatatypeConverter.printBase64Binary(sig.sign());
    }

    public static String sign(
            String data,
            String keystr,
            String mimeType,
            String alg) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        PrivateKey key = getPrivateKeyFromString(keystr, mimeType);
        Signature sig = Signature.getInstance(alg);
        sig.initSign(key);
        sig.update(data.getBytes());
//        return DatatypeConverter.printBase64Binary(sig.sign());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sig.sign());
    }

    public static PrivateKey getPrivateKeyFromString(String key, String mimeType) throws IOException, GeneralSecurityException {
        byte[] encoded = null;
        if (mimeType.toLowerCase().contains("base64")) {
            String privateKeyPEM = key;
            privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
            privateKeyPEM = privateKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");
            encoded = DatatypeConverter.parseBase64Binary(privateKeyPEM);
        } else {
            encoded = DatatypeConverter.parseHexBinary(key);
        }
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        PrivateKey privKey = (PrivateKey) kf.generatePrivate(keySpec);
        return privKey;
    }

    public static PrivateKey getPrivateKey(byte[] pk) throws IOException, GeneralSecurityException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pk);
        PrivateKey privKey = (PrivateKey) kf.generatePrivate(keySpec);
        return privKey;
    }

    public static boolean[] getKeyUsage(X509Certificate x509) {
        /*
         * digitalSignature        (0),
         nonRepudiation          (1),
         keyEncipherment         (2),
         dataEncipherment        (3),
         keyAgreement            (4),
         keyCertSign             (5),  --> true ONLY for CAs
         cRLSign                 (6),
         encipherOnly            (7),
         decipherOnly            (8)
         *
         **/
        return x509.getKeyUsage();
    }

    public static int getBasicConstraint(X509Certificate x509) {
        return x509.getBasicConstraints();
    }

    public static boolean checkCertificateRelation(String childCert,
            String parentCert) {
        boolean isOk = false;
        try {
            CertificateFactory certFactoryChild = CertificateFactory
                    .getInstance("X.509", "BC");
            InputStream inChild = new ByteArrayInputStream(
                    getX509Der(childCert));
            X509Certificate certChild = (X509Certificate) certFactoryChild
                    .generateCertificate(inChild);

            CertificateFactory certFactoryParent = CertificateFactory
                    .getInstance("X.509", "BC");
            InputStream inParent = new ByteArrayInputStream(
                    getX509Der(parentCert));
            X509Certificate certParent = (X509Certificate) certFactoryParent
                    .generateCertificate(inParent);

            certChild.verify(certParent.getPublicKey());

            isOk = true;
        } catch (SignatureException e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Invalid certficate. Signature exception.");
            }
        } catch (CertificateException e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Invalid certficate. Certificate exception.");
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Invalid certficate. Something wrong exception.");
            }
        }
        return isOk;
    }

    public static boolean checkCertificateRelation(X509Certificate childCert,
            X509Certificate parentCert) {
        boolean isOk = false;
        try {
            childCert.verify(parentCert.getPublicKey());
            isOk = true;
        } catch (SignatureException e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Invalid certficate. Signature exception.");
            }
            e.printStackTrace();
        } catch (CertificateException e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Invalid certficate. Certificate exception.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Invalid certficate. Something wrong exception.");
            }
            e.printStackTrace();
        }
        return isOk;
    }

    public static String encryptRSA(String message, PublicKey publicKey) {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            result = DatatypeConverter.printBase64Binary(cipher.doFinal(message.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String decryptRSA(String message, PrivateKey privateKey) {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            result = new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(message)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean validateHashData(String hash) {
        if ((hash.length() % 2) != 0) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Invalid HashData=" + hash + " modulus of 2 should be ZERO");
            }
            return false;
        }
        byte[] binraryHash = DatatypeConverter.parseHexBinary(hash);
        if (binraryHash.length > 83) { // 83 is SHA-512 padded
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Hash length is greater than 64 bytes. Wtf?");
            }
            return false;
        }
        return true;
    }

    public static String getHashAlgorithm(byte[] hashData) {
        int len = hashData.length;
        switch (len) {
            case HASH_MD5_LEN:
                return HASH_MD5;
            case HASH_MD5_LEN_PADDED:
                return HASH_MD5;
            case HASH_SHA1_LEN:
                return HASH_SHA1;
            case HASH_SHA1_LEN_PADDED:
                return HASH_SHA1;
            case HASH_SHA256_LEN:
                return HASH_SHA256;
            case HASH_SHA256_LEN_PADDED:
                return HASH_SHA256;
            case HASH_SHA384_LEN:
                return HASH_SHA384;
            case HASH_SHA384_LEN_PADDED:
                return HASH_SHA384;
            case HASH_SHA512_LEN:
                return HASH_SHA512;
            case HASH_SHA512_LEN_PADDED:
                return HASH_SHA512;
            default:
                return HASH_SHA1;
        }
    }

    public static byte[] getBytes(String data, String charset) {
        byte[] bytes;
        try {
            bytes = data.getBytes(charset);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Invalid charset " + charset + ". Using the default one. It maybe got the unicode issue.");
            }
            bytes = data.getBytes();
        }
        return bytes;
    }

    public static String generatePKCS1Signature(
            String data,
            String keyStorePath,
            String keyStorePassword,
            String keystoreType) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore keystore = KeyStore.getInstance(keystoreType);
        Signature sig;
        try ( InputStream is = new FileInputStream(keyStorePath)) {
            keystore.load(is, keyStorePassword.toCharArray());
            Enumeration<String> e = keystore.aliases();
            String aliasName;
            PrivateKey key = null;
            while (e.hasMoreElements()) {
                aliasName = e.nextElement();
                key = (PrivateKey) keystore.getKey(aliasName,
                        keyStorePassword.toCharArray());
                if (key != null) {
                    break;
                }
            }
            sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(key);
            sig.update(data.getBytes());
        }
        return DatatypeConverter.printBase64Binary(sig.sign());
    }

    public static PublicKey computePublicKey(BigInteger modulus, BigInteger exponent) {
        PublicKey pubKey = null;
        try {
            pubKey = (PublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubKey;
    }

    public static byte[] paddingSHA1OID(byte[] hashedData) throws Exception {
        DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(HASH_SHA1);
        DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, hashedData);
        return digestInfo.getEncoded();
    }

    public static byte[] paddingSHA256OID(byte[] hashedData) throws Exception {
        DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(HASH_SHA256);
        DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, hashedData);
        return digestInfo.getEncoded();
    }

    public static byte[] paddingSHA384OID(byte[] hashedData) throws Exception {
        DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(HASH_SHA384);
        DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, hashedData);
        return digestInfo.getEncoded();
    }

    public static byte[] paddingSHA512OID(byte[] hashedData) throws Exception {
        DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(HASH_SHA512);
        DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, hashedData);
        return digestInfo.getEncoded();
    }

    public static byte[] paddingMD5OID(byte[] hashedData) throws Exception {
        DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(HASH_MD5);
        DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, hashedData);
        return digestInfo.getEncoded();
    }

    public static byte[] generateKeystore(
            String keyName,
            byte[] encPubKey,
            PrivateKey privateKey,
            String password) throws Exception {

        String subjectDn = "CN=" + keyName;
        X509Certificate selfsignCertificate = generateSelfSignCertificate(
                subjectDn, encPubKey, privateKey);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null);

        X509Certificate[] chain = new X509Certificate[1];
        chain[0] = selfsignCertificate;
        keyStore.setKeyEntry(keyName, privateKey, password.toCharArray(), chain);
        OutputStream os = new ByteArrayOutputStream();
        keyStore.store(os, password.toCharArray());
        return ((ByteArrayOutputStream) os).toByteArray();
    }

    public static X509Certificate generateSelfSignCertificate(
            String subjectDN,
            byte[] encPubKey,
            PrivateKey privateKey) throws Exception {

        X500Name issuer = new X500Name(subjectDN);
        X500Name subject = new X500Name(subjectDN);

        RDN[] rdns = subject.getRDNs();

        Calendar c = Calendar.getInstance();
        Date validFrom = c.getTime();
        c.add(Calendar.DATE, 3650);
        Date validTo = c.getTime();

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                issuer,
                new BigInteger(1, Utils.genRandomArray(16)),
                validFrom,
                validTo,
                subject,
                SubjectPublicKeyInfo.getInstance(encPubKey));

        // Authority Info Access
        GeneralName ocspLocation = new GeneralName(6, "http://mobile-id.vn:81/ejbca/publicweb/status/ocsp");
        certBuilder.addExtension(new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.1"),
                false, new AuthorityInformationAccess(X509ObjectIdentifiers.ocspAccessMethod, ocspLocation));

        // CRL Distribution Points
        String crls = "https://mobile-id.vn/crl/Mobile-ID.crl";
        StringTokenizer tokenizer = new StringTokenizer(crls, ";", false);
        ArrayList distpoints = new ArrayList();
        while (tokenizer.hasMoreTokens()) {
            // 6 is URI
            String uri = tokenizer.nextToken();
            GeneralName gn = new GeneralName(6, uri);
            ASN1EncodableVector vec = new ASN1EncodableVector();
            vec.add(gn);
            GeneralNames gns = GeneralNames.getInstance(new DERSequence(vec));
            DistributionPointName dpn = new DistributionPointName(0, gns);
            distpoints.add(new DistributionPoint(dpn, null, null));
        }
        if (distpoints.size() > 0) {
            CRLDistPoint ext = new CRLDistPoint((DistributionPoint[]) distpoints.toArray(new DistributionPoint[0]));
            certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.31"),
                    true, ext);
        }

        // Subject Key Identifier
        SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(
                (ASN1Sequence) new ASN1InputStream(encPubKey).readObject());
        certBuilder.addExtension(
                new ASN1ObjectIdentifier("2.5.29.14"),
                false,
                new SubjectKeyIdentifier(hashData(subjectPublicKeyInfo.getPublicKeyData().getBytes(), HASH_SHA1)));

        // Authority Key Identifier
        SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(
                (ASN1Sequence) new ASN1InputStream(encPubKey).readObject());
        certBuilder.addExtension(
                new ASN1ObjectIdentifier("2.5.29.35"),
                false,
                new AuthorityKeyIdentifier(info));

        // Basic Constraints 
        certBuilder.addExtension(
                new ASN1ObjectIdentifier("2.5.29.19"),
                true,
                new BasicConstraints(false));

        // Key Usage
        certBuilder.addExtension(
                new ASN1ObjectIdentifier("2.5.29.15"),
                true,
                new KeyUsage(
                        KeyUsage.digitalSignature
                        | KeyUsage.keyEncipherment
                        | KeyUsage.dataEncipherment
                        | KeyUsage.nonRepudiation));

        // Extended Key Usage
        KeyPurposeId keyPurposeId[] = {
            KeyPurposeId.id_kp_serverAuth,
            KeyPurposeId.id_kp_clientAuth,
            KeyPurposeId.id_kp_emailProtection};
        certBuilder.addExtension(
                new ASN1ObjectIdentifier("2.5.29.37"),
                true,
                new ExtendedKeyUsage(keyPurposeId));

        // Subject Alternative Name
        certBuilder.addExtension(
                new ASN1ObjectIdentifier("2.5.29.17"),
                false,
                new GeneralNames(new GeneralName(GeneralName.rfc822Name, "giatk@mobile-id.vn")));

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = builder.build(privateKey);          
        byte[] certBytes = certBuilder.build(signer).getEncoded();
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));        
        return certificate;        
    }

    public static String getPKCS1Signature(String data, String relyingPartyKeyStore, String relyingPartyKeyStorePassword) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        InputStream is = new FileInputStream(relyingPartyKeyStore);
        keystore.load(is, relyingPartyKeyStorePassword.toCharArray());

        Enumeration<String> e = keystore.aliases();
        String aliasName = "";
        while (e.hasMoreElements()) {
            aliasName = e.nextElement();
        }
        PrivateKey key = (PrivateKey) keystore.getKey(aliasName,
                relyingPartyKeyStorePassword.toCharArray());

        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(key);
        sig.update(data.getBytes());
        return DatatypeConverter.printBase64Binary(sig.sign());
    }

    public static byte[] md5(byte[] data) {
        byte[] result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            result = md.digest();
        } catch (NoSuchAlgorithmException e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("No Such Algorithm Exception.");
            }
            e.printStackTrace();
        }
        return result;
    }     

    public static ASN1Object getExtensionValue(X509Certificate cert, String oid) throws IOException {
        byte[] bytes = cert.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(bytes));
        ASN1OctetString octs = (ASN1OctetString) aIn.readObject();
        aIn = new ASN1InputStream(new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
    }

    private static String getStringFromGeneralName(ASN1Object names) throws IOException {
        DERTaggedObject taggedObject = (DERTaggedObject) names;
        return new String(ASN1OctetString.getInstance(taggedObject, false).getOctets(), "ISO-8859-1");
    }

    public static String getSubjectKeyIdentifier(X509Certificate cert) {
        byte[] extensionValue = cert.getExtensionValue("2.5.29.14");
        if (DEROctetString.getInstance(extensionValue) == null) {
            if (LogHandler.isShowWarnLog()) {
                LOG.warn("WARNING!!!. SubjectKeyIdentifier NOT found for DN " + cert.getSubjectDN().toString());
            }
            return "";
        }
        byte[] octets = DEROctetString.getInstance(extensionValue).getOctets();
        SubjectKeyIdentifier subjectKeyIdentifier = SubjectKeyIdentifier.getInstance(octets);
        byte[] keyIdentifier = subjectKeyIdentifier.getKeyIdentifier();
        //String keyIdentifierHex = new String(Hex.encode(keyIdentifier));
        String keyIdentifierHex = DatatypeConverter.printHexBinary(keyIdentifier).toLowerCase();
        return keyIdentifierHex;
    }

    public static String getIssuerKeyIdentifier(X509Certificate cert) {
        byte[] extensionValue = cert.getExtensionValue("2.5.29.35");
        if (DEROctetString.getInstance(extensionValue) == null) {
            if (LogHandler.isShowWarnLog()) {
                LOG.warn("WARNING!!!. IssuerKeyIdentifier NOT found for DN " + cert.getSubjectDN().toString());
            }
            return "";
        }
        byte[] octets = DEROctetString.getInstance(extensionValue).getOctets();
        AuthorityKeyIdentifier authorityKeyIdentifier = AuthorityKeyIdentifier.getInstance(octets);
        byte[] keyIdentifier = authorityKeyIdentifier.getKeyIdentifier();
//        String keyIdentifierHex = new String(Hex.encode(keyIdentifier));
        String keyIdentifierHex = DatatypeConverter.printHexBinary(keyIdentifier).toLowerCase();
        return keyIdentifierHex;
    }

    public static String convertToBase64PEMString(Certificate x509Cert) {
        String pem = "";
        try {
            StringWriter sw = new StringWriter();
            try ( PEMWriter pw = new PEMWriter(sw)) {
                pw.writeObject(x509Cert);
            }
            return sw.toString();
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while converting X509Certificate to Base64 PEM String.");
            }
            e.printStackTrace();
        }
        return pem;
    }

    private static byte[] randomBytes(int length) {
        Random randomno = new Random();
        byte[] nbyte = new byte[length];
        randomno.nextBytes(nbyte);
        return nbyte;
    }

    public static String generateAccessKeyID() {
        return java.util.Base64.getUrlEncoder().encodeToString(randomBytes(15)).toUpperCase();
    }

    public static String generateXApiKey() {
        return java.util.Base64.getUrlEncoder().encodeToString(randomBytes(30));
    }

    public static String generateSecretKey() {
        return java.util.Base64.getUrlEncoder().encodeToString(randomBytes(30));
    }

    public static KeyPair generateRSAKey(int lenght) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(lenght < 1024 ? 2048 : lenght);
        return kpg.generateKeyPair();
    }

    /*
    public static String getCRLDistributionPoint1(final Certificate certificate) {
        String crlUri = null;
        try {
            if (CertTools.getCrlDistributionPoint(certificate) != null) {
                crlUri = (new URLRedirection()).getUrl(CertTools.getCrlDistributionPoint(certificate).toString());
            } else {
                return null;
            }
        } catch (Exception e) {
            if (LogAndCacheManager.isShowErrorLog()) {
                LOG.error("Error while getting CRL URI. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        }
        return crlUri;
    }
     */
    public static List<String> getCRLDistributionPoints(X509Certificate certificate) {
        byte[] crlDistributionPoint = certificate.getExtensionValue(Extension.cRLDistributionPoints.getId());
        if (crlDistributionPoint == null) {
            return null;
        }
        List<String> crlUrls = new ArrayList<String>();
        try {
            CRLDistPoint distPoint = CRLDistPoint.getInstance(JcaX509ExtensionUtils.parseExtensionValue(crlDistributionPoint));

            for (DistributionPoint dp : distPoint.getDistributionPoints()) {
                DistributionPointName dpn = dp.getDistributionPoint();
                // Look for URIs in fullName
                if (dpn != null) {
                    if (dpn.getType() == DistributionPointName.FULL_NAME) {
                        GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
                        // Look for an URI
                        for (int j = 0; j < genNames.length; j++) {
                            if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                                String url = DERIA5String.getInstance(genNames[j].getName()).getString();
                                crlUrls.add(url);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            crlUrls = null;
        }
        return crlUrls;
    }

    public static boolean isCACertificate(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return false;
        }
        boolean[] keyUsages = Crypto.getKeyUsage(x509Certificate);
        if (keyUsages != null) {
            if (keyUsages[5]) {
                return true;
            } else {
                return false;
            }
        } else {
            int pathLen = x509Certificate.getBasicConstraints();
            if (pathLen != -1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isRootCACertificate(X509Certificate x509Certificate) {
        boolean isCA = isCACertificate(x509Certificate);
        if (isCA) {
            if (x509Certificate.getSubjectDN().equals(x509Certificate.getIssuerDN())) {
                return true;
            }
        }
        return false;
    }

    /*
    public static List<X509Certificate> sortX509CertificateList(List<X509Certificate> chains) {
        List<X509Certificate> sortedList = new ArrayList<>();
        X509Certificate index = null;
        for (X509Certificate x : chains) {
            if (!isCACertificate(x)) {
                sortedList.add(x);
                index = x;
                break;
            }
        }

        while (!getSubjectKeyIdentifier(index).equals(getIssuerKeyIdentifier(index))
                && sortedList.size() != chains.size()) {
            for (X509Certificate x : chains) {
                if (getIssuerKeyIdentifier(index) != null) {
                    if (getIssuerKeyIdentifier(index).equals(getSubjectKeyIdentifier(x))) {
                        sortedList.add(x);
                        index = x;
                        break;
                    }
                }
            }
        }
        return sortedList;
    }
     */
    public static List<X509Certificate> sortX509Chain(List<X509Certificate> certs) throws Exception {
        LinkedList<X509Certificate> sortedCerts = new LinkedList<X509Certificate>();
        LinkedList<X509Certificate> unsortedCerts = new LinkedList<X509Certificate>(certs);
        // take the first argument of the unsorted List, remove it, and set it
        // as the first element of the sorted List
        sortedCerts.add(unsortedCerts.pollFirst());
        int escapeCounter = 0;

        while (!unsortedCerts.isEmpty()) {
            int initialSize = unsortedCerts.size();
            // take the next element of the unsorted List, remove it, and test
            // if it can be added either at the beginning or the end of the
            // sorted list. If it cannot be added at either side put it back at
            // the end of the unsorted List. Go ahead until there are no more
            // elements in the unsorted List
            X509Certificate currentCert = unsortedCerts.pollFirst();
            if (currentCert.getIssuerX500Principal().equals(sortedCerts.peekFirst().getSubjectX500Principal())) {
                sortedCerts.offerFirst(currentCert);
            } else if (currentCert.getSubjectX500Principal().equals(sortedCerts.peekLast().getIssuerX500Principal())) {
                sortedCerts.offerLast(currentCert);
            } else {
                unsortedCerts.offerLast(currentCert);
            }
            // to prevent a endless loop, the following construct escapes the
            // loop if no change is made after each remaining, yet unsorted,
            // certificate has been tested twice if it fits the chain
            if (unsortedCerts.size() == initialSize) {
                escapeCounter++;
                if (escapeCounter >= (2 * initialSize)) {
                    throw new Exception();
                }
            } else {
                escapeCounter = 0;
            }
        }
        return sortedCerts;
    }

    public static boolean validateCertificateValidity(X509Certificate x509) {
        try {
            x509.checkValidity();
            return true;
        } catch (CertificateExpiredException e) {

        } catch (CertificateNotYetValidException e) {

        }
        return false;
    }

    public static byte[] aesEncryption(byte[] value, byte[] keyData, byte[] ivData) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(ivData);
        SecretKeySpec skeySpec = new SecretKeySpec(keyData, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(value);
        return encrypted;
    }

    public static byte[] aesDecryption(byte[] encrypted, byte[] keyData, byte[] ivData) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(ivData);
        SecretKeySpec skeySpec = new SecretKeySpec(keyData, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] original = cipher.doFinal(encrypted);
        return original;
    }

    public static byte[] rsaEncryption(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] rsaDecryption(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static PublicKey getPublicKey(byte[] encoded) {
        PublicKey pubKeyString = null;
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            pubKeyString = kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubKeyString;
    }

    public static byte[] calcHmacSha256(byte[] secretKey, byte[] message) {
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return hmacSha256;
    }

    public static X509CRL generateX509CrlObject(byte[] crlData) {
        X509CRL x509crl = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            x509crl = (X509CRL) cf.generateCRL(new ByteArrayInputStream(crlData));
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while construct X509CRL object.");
            }
        }
        return x509crl;
    }

    public static boolean hasRelationship(X509Certificate child, X509Certificate parent) {
        try {
            child.verify(parent.getPublicKey());
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public static boolean hasIdPkixOcspNoCheckExtension(X509Certificate certificate) {
        final byte[] extensionValue = certificate.getExtensionValue(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck.getId());
        if (extensionValue != null) {
            try {
                final ASN1Primitive derObject = toASN1Primitive(extensionValue);
                if (derObject instanceof DEROctetString) {
                    return isDEROctetStringNull((DEROctetString) derObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static <T extends ASN1Primitive> T toASN1Primitive(final byte[] bytes) {
        try {
            return (T) ASN1Primitive.fromByteArray(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isDEROctetStringNull(final DEROctetString derOctetString) {
        final byte[] derOctetStringBytes = derOctetString.getOctets();
        final ASN1Primitive asn1Null = toASN1Primitive(derOctetStringBytes);
        return DERNull.INSTANCE.equals(asn1Null);
    }

    public static boolean isCRLExpired(byte[] crlData) {
        X509CRL x509CRL = generateX509CrlObject(crlData);
        if (x509CRL != null) {
            Date now = Calendar.getInstance().getTime();
            Date nextUpdate = x509CRL.getNextUpdate();
            if (nextUpdate.before(now)) {
                return true;
            }
        }
        return false;
    }

    public static X509Certificate getOcspSigner(BasicOCSPResp basicResponse) {
        X509Certificate ocspSigner = null;
        try {
            if (basicResponse == null) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("BasicOCSPResp is NULL or EMPTY");
                }
                return null;
            }
            X509CertificateHolder[] x509CertificateHolder = basicResponse.getCerts();
            for (int i = 0; i < x509CertificateHolder.length; i++) {
                X509Certificate certOcspResp = new JcaX509CertificateConverter().setProvider("BC").getCertificate(x509CertificateHolder[i]);
                return certOcspResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting OCSP Signer.");
            }
        }
        return ocspSigner;
    }

    //Update by Gia
    public static List<X509Certificate> getCertificate(String pemformat) throws IOException
    {        
        pemformat = pemformat.replace("\n", "");        
        String splitString = X509Factory.END_CERT + X509Factory.BEGIN_CERT;        
        String[] temp = pemformat.split(splitString);                
        List<X509Certificate> result = new ArrayList<>();        
        for (String cert : temp) {                        
            cert = cert.replaceAll(X509Factory.BEGIN_CERT, "");
            cert = cert.replaceAll(X509Factory.END_CERT, "");
            cert = cert.replace("\n", "");            
            byte[] bytes = new BASE64Decoder().decodeBuffer(cert);
            try ( InputStream inputStream = new ByteArrayInputStream(bytes)) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                java.security.cert.Certificate certificate = cf.generateCertificate(inputStream);

                if (certificate instanceof X509Certificate) {
                    result.add((X509Certificate) certificate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
